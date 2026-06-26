import subprocess
from pathlib import Path

DEFAULT_MAX_RESULTS = 100


def _validate_path(path):
    try:
        resolved = Path(path).expanduser().resolve()
    except (OSError, RuntimeError) as exc:
        return None, f"Error: invalid path '{path}': {exc}"

    if not resolved.exists():
        return None, f"Error: path '{path}' does not exist"
    if not resolved.is_dir():
        return None, f"Error: path '{path}' is not a directory"
    return resolved, None


def glob(pattern, path=".", max_results=DEFAULT_MAX_RESULTS):
    if not isinstance(pattern, str) or not pattern:
        return "Error: pattern must be a non-empty string"
    if not isinstance(max_results, int) or max_results < 1:
        return "Error: max_results must be a positive integer"

    root, error = _validate_path(path)
    if error:
        return error

    cmd = ["rg", "--files", "--glob", pattern, str(root)]
    try:
        completed = subprocess.run(
            cmd,
            check=False,
            capture_output=True,
            text=True,
        )
    except FileNotFoundError:
        return "Error: rg is not installed or not available on PATH"
    except OSError as exc:
        return f"Error: unable to run rg: {exc}"

    if completed.returncode not in (0, 1):
        detail = completed.stderr.strip() or "rg failed"
        return f"Error: {detail}"

    paths = [Path(line).resolve() for line in completed.stdout.splitlines() if line]
    if not paths:
        return "No files found"

    paths.sort(key=lambda item: item.stat().st_mtime, reverse=True)
    selected = paths[:max_results]
    output = [f"Files matching {pattern} in {root}:"]
    output.extend(str(item) for item in selected)
    if len(paths) > max_results:
        output.append(f"... [truncated, showing {max_results} of {len(paths)} files]")
    return "\n".join(output)


schema = {
    "type": "function",
    "function": {
        "name": "glob",
        "description": (
            "Find files by glob pattern using ripgrep. Returns matching file paths "
            "sorted by modification time, newest first. Respects ignore files."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "pattern": {
                    "type": "string",
                    "description": "Glob pattern to match, for example '*.py' or '**/*.md'.",
                },
                "path": {
                    "type": "string",
                    "description": "Directory to search. Defaults to the current directory.",
                },
                "max_results": {
                    "type": "integer",
                    "description": "Maximum number of files to return. Defaults to 100.",
                },
            },
            "required": ["pattern"],
        },
    },
}

func = glob
