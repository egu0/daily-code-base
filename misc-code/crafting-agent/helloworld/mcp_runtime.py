import json
import os
import re
from pathlib import Path
from typing import Any, Callable

from aisuite.mcp import MCPClient
from loguru import logger

DEFAULT_MCP_CONFIG_PATH = Path(__file__).resolve().parent / "mcp_config.json"
MCP_CONFIG_ENV = "HELLOWORLD_MCP_CONFIG"
ENV_PATTERN = re.compile(r"^\$\{([A-Za-z_][A-Za-z0-9_]*)\}$")


def _expand_env_values(value: Any) -> Any:
    if isinstance(value, str):
        match = ENV_PATTERN.match(value)
        if match:
            return os.environ.get(match.group(1), "")
        return value
    if isinstance(value, list):
        return [_expand_env_values(item) for item in value]
    if isinstance(value, dict):
        return {key: _expand_env_values(item) for key, item in value.items()}
    return value


def load_mcp_configs(path: str | Path | None = None) -> list[dict[str, Any]]:
    config_path = Path(path) if path is not None else Path(
        os.environ.get(MCP_CONFIG_ENV, DEFAULT_MCP_CONFIG_PATH)
    )
    if not config_path.exists():
        return []

    payload = json.loads(config_path.read_text(encoding="utf-8"))
    if isinstance(payload, list):
        return _expand_env_values(payload)
    if isinstance(payload, dict):
        servers = payload.get("servers", [])
        if not isinstance(servers, list):
            raise ValueError("MCP config 'servers' must be a list")
        return _expand_env_values(servers)
    raise ValueError("MCP config must be a list or an object with a 'servers' list")


class MCPToolRegistry:
    def __init__(
        self,
        configs: list[dict[str, Any]],
        client_factory: Callable[[dict[str, Any]], Any] | None = None,
    ):
        self._client_factory = client_factory or MCPClient.from_config
        self._tool_routes: dict[str, tuple[Any, str]] = {}
        self.tool_schemas: list[dict[str, Any]] = []
        self.clients: list[Any] = []

        for config in configs:
            self._register_server(config)

    def _register_server(self, config: dict[str, Any]) -> None:
        try:
            client = self._client_factory(config)
            self.clients.append(client)
            allowed_tools = set(config.get("allowed_tools") or [])
            server_name = config["name"]

            for tool in client.list_tools():
                original_name = tool["name"]
                if allowed_tools and original_name not in allowed_tools:
                    continue

                public_name = f"{server_name}__{original_name}"
                self._tool_routes[public_name] = (client, original_name)
                self.tool_schemas.append(self._schema_for_tool(public_name, tool))
        except Exception as exc:
            logger.warning(f"! skipped MCP server {config.get('name', '<unnamed>')} · {exc}")

    @staticmethod
    def _schema_for_tool(public_name: str, tool: dict[str, Any]) -> dict[str, Any]:
        parameters = tool.get("inputSchema") or {"type": "object", "properties": {}}
        if "type" not in parameters:
            parameters = {"type": "object", **parameters}

        return {
            "type": "function",
            "function": {
                "name": public_name,
                "description": tool.get("description") or "",
                "parameters": parameters,
            },
        }

    def has_tool(self, name: str) -> bool:
        return name in self._tool_routes

    def call_tool(self, name: str, args: dict[str, Any]) -> Any:
        client, original_name = self._tool_routes[name]
        return client.call_tool(original_name, args)

    def close(self) -> None:
        for client in self.clients:
            close = getattr(client, "close", None)
            if close:
                close()


def load_default_mcp_registry() -> MCPToolRegistry:
    try:
        configs = load_mcp_configs()
    except (OSError, ValueError, json.JSONDecodeError) as exc:
        logger.warning(f"! skipped MCP config · {exc}")
        configs = []
    return MCPToolRegistry(configs)
