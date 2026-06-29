import os
import subprocess

enabled = True

DEFAULT_TIMEOUT = 120
MAX_TIMEOUT = 600
MAX_OUTPUT_CHARS = 10_000


def _truncate(text: str, max_chars: int) -> str:
    if len(text) <= max_chars:
        return text
    return text[:max_chars] + f"\n... [truncated, {len(text) - max_chars} chars omitted]"


def _resolve_cwd(cwd: str | None) -> str:
    if cwd is None:
        return os.getcwd()
    resolved = os.path.expanduser(cwd)
    if not os.path.isdir(resolved):
        raise ValueError(f"cwd is not a directory: {resolved}")
    return resolved


def bash(command: str, cwd: str | None = None, timeout: int = DEFAULT_TIMEOUT) -> str:
    if not isinstance(command, str) or not command.strip():
        return "Error: command must be a non-empty string"

    timeout = min(int(timeout), MAX_TIMEOUT) if timeout else DEFAULT_TIMEOUT

    try:
        work_dir = _resolve_cwd(cwd)
    except ValueError as exc:
        return f"Error: {exc}"

    try:
        completed = subprocess.run(
            command,
            shell=True,
            capture_output=True,
            text=True,
            timeout=timeout,
            cwd=work_dir,
        )
    except subprocess.TimeoutExpired as exc:
        stdout = _truncate((exc.stdout or b"").decode("utf-8", errors="replace"), MAX_OUTPUT_CHARS)
        stderr = _truncate((exc.stderr or b"").decode("utf-8", errors="replace"), MAX_OUTPUT_CHARS)
        parts = [f"Error: command timed out after {timeout}s"]
        if stdout:
            parts.append(f"stdout (partial):\n{stdout}")
        if stderr:
            parts.append(f"stderr (partial):\n{stderr}")
        return "\n".join(parts)
    except OSError as exc:
        return f"Error: unable to execute command: {exc}"

    stdout = _truncate(completed.stdout, MAX_OUTPUT_CHARS)
    stderr = _truncate(completed.stderr, MAX_OUTPUT_CHARS)

    parts = [f"Exit code: {completed.returncode}"]
    if stdout:
        parts.append(stdout)
    if stderr:
        parts.append(f"stderr:\n{stderr}")

    return "\n".join(parts)


schema = {
    "type": "function",
    "function": {
        "name": "bash",
        "description": (
            "Execute a shell command with optional working directory and timeout. "
            "Returns exit code, stdout, and stderr. Default timeout is 120s, max 600s. "
            "Output is truncated at 10,000 characters."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "command": {
                    "type": "string",
                    "description": "The shell command to execute.",
                },
                "cwd": {
                    "type": "string",
                    "description": "Working directory for the command. Defaults to the current directory.",
                },
                "timeout": {
                    "type": "integer",
                    "description": "Timeout in seconds. Defaults to 120, maximum 600.",
                },
            },
            "required": ["command"],
        },
    },
}

func = bash
