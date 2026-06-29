import pytest
import sys
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.mcp_runtime import MCPToolRegistry


class FakeMCPClient:
    def __init__(self, config):
        self.calls = []

    @classmethod
    def from_config(cls, config):
        return cls(config)

    def list_tools(self):
        return [
            {
                "name": "search",
                "description": "Search the web for current information",
                "inputSchema": {
                    "type": "object",
                    "properties": {
                        "query": {
                            "type": "string",
                            "description": "Search query text",
                        },
                        "max_results": {
                            "type": "integer",
                            "description": "Maximum results to return",
                        },
                    },
                    "required": ["query"],
                },
            },
            {
                "name": "forecast",
                "description": "Get weather forecast for a city",
                "inputSchema": {
                    "type": "object",
                    "properties": {"city": {"type": "string"}},
                    "required": ["city"],
                },
            },
        ]

    def call_tool(self, tool_name, arguments):
        self.calls.append((tool_name, arguments))
        return {"tool": tool_name, "arguments": arguments}


def make_registry():
    return MCPToolRegistry(
        [{"type": "mcp", "name": "remote", "command": "fake-command"}],
        client_factory=FakeMCPClient.from_config,
    )


def test_tool_search_schemas_expose_three_bridge_tools():
    from helloworld.tool_search import tool_search_schemas

    names = [schema["function"]["name"] for schema in tool_search_schemas]

    assert names == ["tool_search", "tool_describe", "tool_call"]


def test_tool_search_returns_ranked_mcp_tool_summaries():
    from helloworld.tool_search import MCPToolSearchRouter

    router = MCPToolSearchRouter(make_registry())

    result = router.search("web current search", limit=1)

    assert result == [
        {
            "name": "remote__search",
            "description": "Search the web for current information",
            "parameters": {
                "query": "string - Search query text",
                "max_results": "integer - Maximum results to return",
            },
            "required": ["query"],
        }
    ]


def test_tool_describe_returns_full_schema_for_named_mcp_tool():
    from helloworld.tool_search import MCPToolSearchRouter

    router = MCPToolSearchRouter(make_registry())

    result = router.describe("remote__forecast")

    assert result["name"] == "remote__forecast"
    assert result["description"] == "Get weather forecast for a city"
    assert result["parameters"]["required"] == ["city"]


def test_tool_call_dispatches_to_mcp_registry():
    from helloworld.tool_search import MCPToolSearchRouter

    registry = make_registry()
    router = MCPToolSearchRouter(registry)

    result = router.call("remote__search", {"query": "agent frameworks"})

    assert result == {
        "tool": "search",
        "arguments": {"query": "agent frameworks"},
    }


def test_tool_describe_rejects_unknown_tool():
    from helloworld.tool_search import MCPToolSearchRouter

    router = MCPToolSearchRouter(make_registry())

    with pytest.raises(KeyError, match="unknown MCP tool"):
        router.describe("missing__tool")
