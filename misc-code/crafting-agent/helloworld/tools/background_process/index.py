from .manager import get_process_manager


def background_process(
    action: str,
    command: str | None = None,
    cwd: str | None = None,
    process_id: str | None = None,
    stream: str = "both",
    lines: int = 100,
    input: str | None = None,
) -> str:
    mgr = get_process_manager()

    if action == "start":
        if not command:
            return "Error: 'command' is required for action 'start'"
        return mgr.start(command, cwd)

    if action == "stop":
        if not process_id:
            return "Error: 'process_id' is required for action 'stop'"
        return mgr.stop(process_id)

    if action == "status":
        if not process_id:
            return "Error: 'process_id' is required for action 'status'"
        return mgr.status(process_id)

    if action == "read_output":
        if not process_id:
            return "Error: 'process_id' is required for action 'read_output'"
        return mgr.read_output(process_id, stream, lines)

    if action == "send_input":
        if not process_id:
            return "Error: 'process_id' is required for action 'send_input'"
        if not input:
            return "Error: 'input' is required for action 'send_input'"
        return mgr.send_input(process_id, input)

    if action == "list":
        return mgr.list_all()

    return (
        f"Error: unknown action '{action}'. "
        "Valid actions: start, stop, status, read_output, send_input, list"
    )


schema = {
    "type": "function",
    "function": {
        "name": "background_process",
        "description": (
            "Manage long-running background processes (e.g., dev servers). "
            "Actions: start (launch a process), stop (kill by ID), "
            "status (get runtime info), read_output (read stdout/stderr logs), "
            "send_input (write to stdin), list (list all processes). "
            "Processes persist across agent restarts."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "action": {
                    "type": "string",
                    "description": (
                        "The action to perform: 'start', 'stop', 'status', "
                        "'read_output', 'send_input', or 'list'."
                    ),
                    "enum": ["start", "stop", "status", "read_output", "send_input", "list"],
                },
                "command": {
                    "type": "string",
                    "description": "The shell command to run. Required for 'start'.",
                },
                "cwd": {
                    "type": "string",
                    "description": "Working directory. Required for 'start', defaults to current directory.",
                },
                "process_id": {
                    "type": "string",
                    "description": "The process ID (8-char hex). Required for 'stop', 'status', 'read_output', 'send_input'.",
                },
                "stream": {
                    "type": "string",
                    "description": "Which stream to read: 'stdout', 'stderr', or 'both' (default). Used by 'read_output'.",
                    "enum": ["stdout", "stderr", "both"],
                },
                "lines": {
                    "type": "integer",
                    "description": "Number of last lines to read. Default 100. Used by 'read_output'.",
                },
                "input": {
                    "type": "string",
                    "description": "Text to send to the process stdin. Required for 'send_input'.",
                },
            },
            "required": ["action"],
        },
    },
}

func = background_process
