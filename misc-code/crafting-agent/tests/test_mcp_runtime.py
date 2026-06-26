import json
import sys
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.mcp_runtime import MCP_CONFIG_ENV, MCPToolRegistry, load_default_mcp_registry, load_mcp_configs


class FakeMCPClient:
    created_configs = []

    def __init__(self, config):
        self.config = config
        self.name = config["name"]
        self.calls = []
        FakeMCPClient.created_configs.append(config)

    @classmethod
    def from_config(cls, config):
        return cls(config)

    def list_tools(self):
        return [
            {
                "name": "echo",
                "description": "Echo input",
                "inputSchema": {
                    "type": "object",
                    "properties": {"text": {"type": "string"}},
                    "required": ["text"],
                },
            },
            {
                "name": "hidden",
                "description": "Hidden tool",
                "inputSchema": {"type": "object", "properties": {}},
            },
        ]

    def call_tool(self, tool_name, arguments):
        self.calls.append((tool_name, arguments))
        return {"tool": tool_name, "arguments": arguments}


def test_load_mcp_configs_returns_empty_for_missing_file(tmp_path):
    assert load_mcp_configs(tmp_path / "missing.json") == []


def test_load_mcp_configs_accepts_servers_object(tmp_path):
    config_path = tmp_path / "mcp.json"
    config_path.write_text(
        json.dumps(
            {
                "servers": [
                    {
                        "type": "mcp",
                        "name": "fake",
                        "command": "fake-command",
                        "args": ["--stdio"],
                    }
                ]
            }
        ),
        encoding="utf-8",
    )

    assert load_mcp_configs(config_path) == [
        {
            "type": "mcp",
            "name": "fake",
            "command": "fake-command",
            "args": ["--stdio"],
        }
    ]


def test_load_mcp_configs_expands_env_placeholders(monkeypatch, tmp_path):
    monkeypatch.setenv("TAVILY_KEY", "test-key")
    config_path = tmp_path / "mcp.json"
    config_path.write_text(
        json.dumps(
            {
                "servers": [
                    {
                        "type": "mcp",
                        "name": "tavily",
                        "command": "npx",
                        "env": {"TAVILY_API_KEY": "${TAVILY_KEY}"},
                    }
                ]
            }
        ),
        encoding="utf-8",
    )

    assert load_mcp_configs(config_path)[0]["env"] == {"TAVILY_API_KEY": "test-key"}


def test_load_default_mcp_registry_ignores_invalid_config(monkeypatch, tmp_path):
    config_path = tmp_path / "bad.json"
    config_path.write_text("{bad json", encoding="utf-8")
    monkeypatch.setenv(MCP_CONFIG_ENV, str(config_path))

    registry = load_default_mcp_registry()

    assert registry.tool_schemas == []


def test_registry_prefixes_mcp_tool_schemas_and_filters_allowed_tools():
    registry = MCPToolRegistry(
        [
            {
                "type": "mcp",
                "name": "fake",
                "command": "fake-command",
                "allowed_tools": ["echo"],
            }
        ],
        client_factory=FakeMCPClient.from_config,
    )

    schemas = registry.tool_schemas

    assert len(schemas) == 1
    assert schemas[0]["type"] == "function"
    assert schemas[0]["function"]["name"] == "fake__echo"
    assert schemas[0]["function"]["description"] == "Echo input"
    assert schemas[0]["function"]["parameters"]["required"] == ["text"]


def test_registry_dispatches_prefixed_tool_name_to_original_mcp_tool():
    registry = MCPToolRegistry(
        [{"type": "mcp", "name": "fake", "command": "fake-command"}],
        client_factory=FakeMCPClient.from_config,
    )

    result = registry.call_tool("fake__echo", {"text": "hello"})

    assert result == {"tool": "echo", "arguments": {"text": "hello"}}


def test_registry_reports_tool_ownership():
    registry = MCPToolRegistry(
        [{"type": "mcp", "name": "fake", "command": "fake-command"}],
        client_factory=FakeMCPClient.from_config,
    )

    assert registry.has_tool("fake__echo") is True
    assert registry.has_tool("echo") is False
    assert registry.has_tool("missing__echo") is False


def test_main_dispatcher_uses_mcp_registry_for_prefixed_tools():
    from helloworld.main import execute_tool

    registry = MCPToolRegistry(
        [{"type": "mcp", "name": "fake", "command": "fake-command"}],
        client_factory=FakeMCPClient.from_config,
    )

    assert execute_tool("fake__echo", {"text": "hello"}, registry) == {
        "tool": "echo",
        "arguments": {"text": "hello"},
    }
