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
    if not resolved.is_dir() and not resolved.is_file():
        return None, f"Error: path '{path}' is not a regular file or directory"
    return resolved, None


def grep(pattern, path=".", glob=None, max_results=DEFAULT_MAX_RESULTS):
    if not isinstance(pattern, str) or not pattern:
        return "Error: pattern must be a non-empty string"
    if glob is not None and not isinstance(glob, str):
        return "Error: glob must be a string"
    if not isinstance(max_results, int) or max_results < 1:
        return "Error: max_results must be a positive integer"

    target, error = _validate_path(path)
    if error:
        return error

    cmd = [
        "rg",
        "--column",
        "--line-number",
        "--no-heading",
        "--color",
        "never",
    ]
    if glob:
        cmd.extend(["--glob", glob])
    cmd.extend(["--", pattern, str(target)])

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

    if completed.returncode == 1:
        return "No matches found"
    if completed.returncode != 0:
        detail = completed.stderr.strip() or "rg failed"
        return f"Error: {detail}"

    matches = [line for line in completed.stdout.splitlines() if line]
    selected = matches[:max_results]
    output = [f"Matches for /{pattern}/ in {target}:"]
    output.extend(selected)
    if len(matches) > max_results:
        output.append(
            f"... [truncated, showing {max_results} of {len(matches)} matches]"
        )
    return "\n".join(output)


schema = {
    "type": "function",
    "function": {
        "name": "grep",
        "description": (
            "Search file contents using ripgrep regular expressions. Returns "
            "path:line:column:match results and supports optional glob filtering."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "pattern": {
                    "type": "string",
                    "description": "Regular expression pattern to search for.",
                },
                "path": {
                    "type": "string",
                    "description": "File or directory to search. Defaults to the current directory.",
                },
                "glob": {
                    "type": "string",
                    "description": "Optional file glob filter, for example '*.py'.",
                },
                "max_results": {
                    "type": "integer",
                    "description": "Maximum number of matches to return. Defaults to 100.",
                },
            },
            "required": ["pattern"],
        },
    },
}

func = grep
