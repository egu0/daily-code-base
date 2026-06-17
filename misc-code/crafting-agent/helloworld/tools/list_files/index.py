from pathlib import Path


def list_files(path):
    p = Path(path).expanduser().resolve()
    if not p.exists():
        return f"Error: path '{path}' does not exist"
    if p.is_file():
        return f"  [file] {p.name}"
    lines = []
    for entry in sorted(p.iterdir(), key=lambda e: (not e.is_dir(), e.name)):
        tag = " [dir]" if entry.is_dir() else "[file]"
        lines.append(f"  {tag} {entry.name}")
    return f"Contents of {p}:\n" + "\n".join(lines)


schema = {
    "type": "function",
    "function": {
        "name": "list_files",
        "description": "List contents of a directory. If given a file path, returns that file's info. Returns an error message if the path does not exist.",
        "parameters": {
            "type": "object",
            "properties": {
                "path": {
                    "type": "string",
                    "description": "Path to a directory or file. Supports ~ and relative paths.",
                },
            },
            "required": ["path"],
        },
    },
}

func = list_files
