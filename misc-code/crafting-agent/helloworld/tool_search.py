import re
from typing import Any

from .mcp_runtime import MCPToolRegistry


tool_search_schemas = [
    {
        "type": "function",
        "function": {
            "name": "tool_search",
            "description": "Search available MCP tools by purpose, capability, or parameter name.",
            "parameters": {
                "type": "object",
                "properties": {
                    "query": {
                        "type": "string",
                        "description": "Natural language search query for the MCP tool you need.",
                    },
                    "limit": {
                        "type": "integer",
                        "description": "Maximum number of matching tools to return.",
                        "default": 5,
                    },
                },
                "required": ["query"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "tool_describe",
            "description": "Return the full schema for one MCP tool found with tool_search.",
            "parameters": {
                "type": "object",
                "properties": {
                    "name": {
                        "type": "string",
                        "description": "Public MCP tool name, such as server__tool.",
                    }
                },
                "required": ["name"],
            },
        },
    },
    {
        "type": "function",
        "function": {
            "name": "tool_call",
            "description": "Invoke one MCP tool by public name with arguments matching its schema.",
            "parameters": {
                "type": "object",
                "properties": {
                    "name": {
                        "type": "string",
                        "description": "Public MCP tool name, such as server__tool.",
                    },
                    "arguments": {
                        "type": "object",
                        "description": "Arguments for the selected MCP tool.",
                    },
                },
                "required": ["name", "arguments"],
            },
        },
    },
]


BRIDGE_TOOL_NAMES = {
    schema["function"]["name"] for schema in tool_search_schemas
}


def format_mcp_tool_index(registry: MCPToolRegistry | None) -> str:
    if not registry or not registry.tool_schemas:
        return ""

    lines = [
        "You have access to MCP tools.",
        "",
        "An MCP tool is an external capability provided by a connected server. "
        "MCP tools are listed by name and description only, and are not "
        "directly callable as function tools.",
        "",
        "If the user request matches an MCP tool description, use "
        "tool_describe with the MCP tool name to read its full argument schema "
        "before calling it. Then use tool_call with the MCP tool name and "
        "arguments. Use tool_search only when the listed MCP tools are not "
        "enough to choose the right tool.",
        "",
        "Available MCP tools:",
    ]
    for schema in registry.tool_schemas:
        function = schema["function"]
        name = function["name"]
        description = function.get("description") or "No description provided."
        lines.extend(
            [
                f"- name: {name}",
                f"  description: {description}",
            ]
        )
    return "\n".join(lines)


class MCPToolSearchRouter:
    def __init__(self, registry: MCPToolRegistry):
        self.registry = registry
        self._schemas_by_name = {
            schema["function"]["name"]: schema
            for schema in registry.tool_schemas
        }

    def has_bridge_tool(self, name: str) -> bool:
        return name in BRIDGE_TOOL_NAMES

    def execute(self, name: str, args: dict[str, Any]) -> Any:
        if name == "tool_search":
            return self.search(**args)
        if name == "tool_describe":
            return self.describe(**args)
        if name == "tool_call":
            return self.call(**args)
        raise KeyError(f"unknown tool search bridge: {name}")

    def search(self, query: str, limit: int = 5) -> list[dict[str, Any]]:
        terms = _tokenize(query)
        scored = []
        for schema in self.registry.tool_schemas:
            text = _search_text(schema)
            score = sum(text.count(term) for term in terms)
            if score:
                scored.append((score, schema))

        scored.sort(
            key=lambda item: (-item[0], item[1]["function"]["name"])
        )
        return [
            _summarize_schema(schema)
            for _, schema in scored[: max(1, limit)]
        ]

    def describe(self, name: str) -> dict[str, Any]:
        schema = self._schemas_by_name.get(name)
        if not schema:
            raise KeyError(f"unknown MCP tool: {name}")
        function = schema["function"]
        return {
            "name": function["name"],
            "description": function.get("description", ""),
            "parameters": function.get("parameters", {}),
        }

    def call(self, name: str, arguments: dict[str, Any] | None = None) -> Any:
        if not self.registry.has_tool(name):
            return f"Error: unknown MCP tool: {name}"
        return self.registry.call_tool(name, arguments or {})


def _tokenize(text: str) -> list[str]:
    return re.findall(r"[a-z0-9_]+", text.lower())


def _search_text(schema: dict[str, Any]) -> str:
    function = schema["function"]
    parameters = function.get("parameters", {})
    chunks = [
        function.get("name", ""),
        function.get("description", ""),
    ]
    for name, prop in parameters.get("properties", {}).items():
        chunks.append(name)
        if isinstance(prop, dict):
            chunks.append(prop.get("description", ""))
            chunks.append(prop.get("type", ""))
    return " ".join(chunks).lower()


def _summarize_schema(schema: dict[str, Any]) -> dict[str, Any]:
    function = schema["function"]
    parameters = function.get("parameters", {})
    properties = parameters.get("properties", {})
    return {
        "name": function["name"],
        "description": function.get("description", ""),
        "parameters": {
            name: _summarize_parameter(prop)
            for name, prop in properties.items()
        },
        "required": parameters.get("required", []),
    }


def _summarize_parameter(prop: Any) -> str:
    if not isinstance(prop, dict):
        return "unknown"
    kind = prop.get("type", "unknown")
    description = prop.get("description", "")
    if description:
        return f"{kind} - {description}"
    return kind
