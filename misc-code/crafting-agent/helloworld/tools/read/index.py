from pathlib import Path

DEFAULT_FILE_LINES = 200
DEFAULT_DIRECTORY_ENTRIES = 100
READ_PREVIEW_BYTES = 4096


def normalize_range(start_line=None, end_line=None, default_count=DEFAULT_FILE_LINES):
    start = 1 if start_line is None else start_line
    end = start + default_count - 1 if end_line is None else end_line

    if not isinstance(start, int) or not isinstance(end, int):
        raise ValueError("start_line and end_line must be integers")
    if start < 1:
        raise ValueError("start_line must be greater than or equal to 1")
    if end < start:
        raise ValueError("end_line must be greater than or equal to start_line")

    return start, end


def line_range_notice(label, start, end, total):
    shown_end = min(end, total)
    if total == 0:
        shown_end = 0
    return f"{label} {start}-{shown_end} of {total}"


def format_truncated(end, total):
    if end < total:
        return f"\n... [truncated, use start_line={end + 1} to continue]"
    return ""


def format_file(path, start_line=None, end_line=None):
    start, end = normalize_range(start_line, end_line, DEFAULT_FILE_LINES)

    try:
        preview = path.read_bytes()[:READ_PREVIEW_BYTES]
    except OSError as exc:
        return f"Error: unable to read '{path}': {exc}"

    if b"\x00" in preview:
        return f"Error: '{path}' appears to be a binary file"

    try:
        text = path.read_text(encoding="utf-8")
    except UnicodeDecodeError:
        return f"Error: '{path}' is not valid UTF-8 text"
    except OSError as exc:
        return f"Error: unable to read '{path}': {exc}"

    lines = text.splitlines()
    selected = lines[start - 1 : end]
    output = [
        f"File: {path}",
        line_range_notice("Lines", start, end, len(lines)),
        "",
    ]
    output.extend(
        f"{line_number} | {line}"
        for line_number, line in enumerate(selected, start=start)
    )
    output.append(format_truncated(end, len(lines)))
    return "\n".join(output).rstrip()


def format_directory(path, start_line=None, end_line=None):
    start, end = normalize_range(start_line, end_line, DEFAULT_DIRECTORY_ENTRIES)

    try:
        entries = sorted(path.iterdir(), key=lambda entry: entry.name)
    except OSError as exc:
        return f"Error: unable to read directory '{path}': {exc}"

    selected = entries[start - 1 : end]
    output = [
        f"Directory: {path}",
        line_range_notice("Entries", start, end, len(entries)),
        "",
    ]
    for entry_number, entry in enumerate(selected, start=start):
        tag = "[dir]" if entry.is_dir() else "[file]"
        output.append(f"{entry_number} | {tag} {entry.name}")
    output.append(format_truncated(end, len(entries)))
    return "\n".join(output).rstrip()


def read(path, start_line=None, end_line=None):
    try:
        p = Path(path).expanduser().resolve()
    except (OSError, RuntimeError) as exc:
        return f"Error: invalid path '{path}': {exc}"

    if not p.exists():
        return f"Error: path '{path}' does not exist"
    if p.is_dir():
        return format_directory(p, start_line, end_line)
    if p.is_file():
        return format_file(p, start_line, end_line)
    return f"Error: path '{path}' is not a regular file or directory"


schema = {
    "type": "function",
    "function": {
        "name": "read",
        "description": (
            "Read a local file or directory. For files, returns the first 200 lines "
            "by default and supports 1-based inclusive start_line/end_line ranges. "
            "For directories, returns the first 100 entry names by default, including "
            "hidden files and directories, and supports the same range parameters."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "path": {
                    "type": "string",
                    "description": "Path to a file or directory. Supports ~ and relative paths.",
                },
                "start_line": {
                    "type": "integer",
                    "description": "1-based inclusive start line or directory entry number.",
                },
                "end_line": {
                    "type": "integer",
                    "description": "1-based inclusive end line or directory entry number.",
                },
            },
            "required": ["path"],
        },
    },
}

func = read
