import sys
from pathlib import Path


ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))


def _write_tool(package_dir, name, enabled_line=None):
    tool_dir = package_dir / name
    tool_dir.mkdir()
    lines = []
    if enabled_line:
        lines.append(enabled_line)
    lines.extend(
        [
            "def run():",
            f"    return '{name}'",
            "",
            "schema = {",
            "    'type': 'function',",
            "    'function': {",
            f"        'name': '{name}',",
            "        'description': 'test tool',",
            "        'parameters': {'type': 'object', 'properties': {}},",
            "    },",
            "}",
            "",
            "func = run",
        ]
    )
    (tool_dir / "index.py").write_text("\n".join(lines), encoding="utf-8")


def test_disabled_tools_are_not_registered(tmp_path):
    package_name = "samplepkg_disabled"
    package_root = tmp_path / package_name
    tools_dir = package_root / "tools"
    tools_dir.mkdir(parents=True)
    (package_root / "__init__.py").write_text("", encoding="utf-8")
    (tools_dir / "__init__.py").write_text("", encoding="utf-8")
    _write_tool(tools_dir, "enabled_tool", "enabled = True")
    _write_tool(tools_dir, "disabled_tool", "enabled = False")

    sys.path.insert(0, str(tmp_path))
    try:
        from helloworld.tools import load_tools

        tools_map, tool_schemas = load_tools(tools_dir, f"{package_name}.tools")
    finally:
        sys.path.remove(str(tmp_path))

    assert set(tools_map) == {"enabled_tool"}
    assert [schema["function"]["name"] for schema in tool_schemas] == ["enabled_tool"]


def test_tools_without_enabled_flag_default_to_registered(tmp_path):
    package_name = "samplepkg_legacy"
    package_root = tmp_path / package_name
    tools_dir = package_root / "tools"
    tools_dir.mkdir(parents=True)
    (package_root / "__init__.py").write_text("", encoding="utf-8")
    (tools_dir / "__init__.py").write_text("", encoding="utf-8")
    _write_tool(tools_dir, "legacy_tool")

    sys.path.insert(0, str(tmp_path))
    try:
        from helloworld.tools import load_tools

        tools_map, tool_schemas = load_tools(tools_dir, f"{package_name}.tools")
    finally:
        sys.path.remove(str(tmp_path))

    assert set(tools_map) == {"legacy_tool"}
    assert [schema["function"]["name"] for schema in tool_schemas] == ["legacy_tool"]
