import importlib
from pathlib import Path

tools_dir = Path(__file__).parent

tools_map = {}
tool_schemas = []

for tool_path in sorted(tools_dir.iterdir()):
    if not tool_path.is_dir():
        continue
    if tool_path.name.startswith("_"):
        continue
    module_file = tool_path / "index.py"
    if not module_file.exists():
        continue
    module = importlib.import_module(f".{tool_path.name}.index", package="helloworld.tools")
    tools_map[module.schema["function"]["name"]] = module.func
    tool_schemas.append(module.schema)
