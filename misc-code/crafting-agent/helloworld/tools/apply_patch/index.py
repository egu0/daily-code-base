import os
from dataclasses import dataclass, field
from pathlib import Path


def _resolve_workspace():
    override = os.environ.get("APPLY_PATCH_WORKSPACE")
    if override:
        return Path(override).resolve()
    return Path.home().resolve()


# ---------------------------------------------------------------------------
# Data structures
# ---------------------------------------------------------------------------

@dataclass
class Hunk:
    """One edit hunk within a V4A diff section.

    ``orig_index`` is the offset *within the matched context* where
    ``del_lines`` should be removed and ``ins_lines`` inserted.
    """
    orig_index: int
    del_lines: list[str]
    ins_lines: list[str]


@dataclass
class PatchOperation:
    kind: str          # "write" | "delete" | "update"
    path: Path
    new_lines: list[str] = field(default_factory=list)   # for write
    sections: list = field(default_factory=list)          # for update: [(context, hunks), ...]


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def line_text(line):
    return line[1:]


def ensure_trailing_newline(text):
    return text if text.endswith("\n") or text == "" else text + "\n"


def path_from_directive(line, prefix):
    raw = line.removeprefix(prefix)
    candidate = Path(raw).expanduser().resolve()
    workspace = _resolve_workspace()
    workspace_str = str(workspace)
    candidate_str = str(candidate)

    if workspace_str in ("/", os.sep):
        if not candidate_str.startswith("/"):
            raise ValueError(f"path escapes workspace: {raw}")
        return candidate

    if not candidate_str.startswith(workspace_str + os.sep) and candidate_str != workspace_str:
        raise ValueError(f"path escapes workspace: {raw}")
    return candidate


# ---------------------------------------------------------------------------
# V4A diff parser — aligned with OpenAI agents apply_diff.py
# ---------------------------------------------------------------------------

def _read_v4a_section(lines, start):
    """Read one V4A section starting at ``lines[start]``.

    Returns (context, hunks, end_index) where *context* is the full context
    text (keep + delete lines) and *hunks* are the edit operations keyed
    by offset within that context.  Stops at ``@@``, ``***`` markers, or EOF.

    Algorithm adapted from OpenAI Agents SDK ``_read_section``.
    """
    context: list[str] = []
    del_lines: list[str] = []
    ins_lines: list[str] = []
    hunks: list[Hunk] = []

    idx = start
    while idx < len(lines):
        line = lines[idx]

        # Section terminators
        if line.startswith("@@") or line.startswith("*** "):
            break

        prefix = line[0] if line else " "
        content = line[1:] if line else ""

        if prefix == "+":
            ins_lines.append(content)
        elif prefix == "-":
            del_lines.append(content)
            context.append(content)
        elif prefix == " ":
            # Switching back to context after add/delete → emit pending hunk
            if del_lines or ins_lines:
                orig = len(context) - len(del_lines)
                hunks.append(Hunk(orig, list(del_lines), list(ins_lines)))
                del_lines.clear()
                ins_lines.clear()
            context.append(content)
        else:
            raise ValueError(
                "each update line must start with a space (context), "
                " - (remove), + (add), or @@ (hunk header)"
            )
        idx += 1

    # Emit any trailing hunk
    if del_lines or ins_lines:
        orig = len(context) - len(del_lines)
        hunks.append(Hunk(orig, list(del_lines), list(ins_lines)))

    return context, hunks, idx


def parse_update_diff(update_lines):
    """Parse V4A update lines into a list of (context, hunks) sections.

    Each ``@@`` line starts a new section.  Bare ``@@`` is accepted.
    """
    if not update_lines:
        raise ValueError("update block is empty")

    sections: list[tuple[list[str], list[Hunk]]] = []
    idx = 0
    while idx < len(update_lines):
        line = update_lines[idx]
        # Skip hunk header lines
        if line.startswith("@@"):
            idx += 1
            continue
        context, hunks, idx = _read_v4a_section(update_lines, idx)
        if context or hunks:
            sections.append((context, hunks))

    if not sections:
        raise ValueError("update block contains no V4A content")

    return sections


# ---------------------------------------------------------------------------
# Context matching & chunk application
# ---------------------------------------------------------------------------

def _find_context(file_lines, context, start_cursor):
    """Find *context* in *file_lines* starting from *start_cursor*.

    Returns the index where context begins, or -1 if not found.
    Tries exact match first, then rstrip, then strip (like OpenAI's fuzz).
    """
    if not context:
        return start_cursor

    for match_fn, _ in [
        (lambda x: x, 0),
        (lambda x: x.rstrip(), 1),
        (lambda x: x.strip(), 100),
    ]:
        for i in range(start_cursor, len(file_lines) - len(context) + 1):
            if all(
                match_fn(file_lines[i + j]) == match_fn(context[j])
                for j in range(len(context))
            ):
                return i
    return -1


def _apply_sections(file_text, sections):
    """Apply parsed V4A sections to *file_text*.

    Each section is (context, hunks).  Context is used to locate the edit
    site; hunks specify what to delete and insert.

    Returns the modified text or raises ValueError on match failure.
    """
    file_lines = file_text.splitlines(keepends=True)
    cursor = 0

    for context, hunks in sections:
        matched = _find_context(
            [l.rstrip("\n").rstrip("\r") for l in file_lines],
            context,
            cursor,
        )
        if matched < 0:
            ctx_preview = "\n".join(context[:6])
            raise ValueError(f"context not found (cursor {cursor}):\n{ctx_preview}")

        # Offset every hunk's orig_index by the matched position
        for h in hunks:
            h.orig_index += matched

        cursor = matched + len(context)

    # --- Apply hunks in reverse order (so indices stay valid) ---
    all_hunks = sorted(
        (h for _, hunks in sections for h in hunks),
        key=lambda h: h.orig_index,
        reverse=True,
    )

    for h in all_hunks:
        del_end = h.orig_index + len(h.del_lines)
        # Normalise ins_lines to include trailing newline for the join below
        ins = [l + "\n" for l in h.ins_lines]
        file_lines[h.orig_index:del_end] = ins

    return "".join(file_lines)


# ---------------------------------------------------------------------------
# Patch parser
# ---------------------------------------------------------------------------

def parse_patch(patch):
    lines = patch.splitlines()
    if not lines or lines[0] != "*** Begin Patch":
        raise ValueError("patch must start with *** Begin Patch")
    if lines[-1] != "*** End Patch":
        raise ValueError("patch must end with *** End Patch")

    operations = []
    index = 1
    while index < len(lines) - 1:
        line = lines[index]

        # --- Write File (create or overwrite) -------------------------------
        if line.startswith("*** Write File: "):
            path = path_from_directive(line, "*** Write File: ")
            index += 1
            new_lines = []
            while index < len(lines) - 1 and not lines[index].startswith("*** "):
                new_lines.append(lines[index])
                index += 1
            operations.append(PatchOperation("write", path, new_lines=new_lines))
            continue

        # --- Delete File ----------------------------------------------------
        if line.startswith("*** Delete File: "):
            path = path_from_directive(line, "*** Delete File: ")
            operations.append(PatchOperation("delete", path))
            index += 1
            continue

        # --- Update File (V4A diff) -----------------------------------------
        if line.startswith("*** Update File: "):
            path = path_from_directive(line, "*** Update File: ")
            index += 1
            update_lines = []
            while index < len(lines) - 1 and not lines[index].startswith("*** "):
                update_lines.append(lines[index])
                index += 1
            sections = parse_update_diff(update_lines)
            operations.append(
                PatchOperation("update", path, sections=sections)
            )
            continue

        raise ValueError(f"unknown patch directive: {line}")

    if not operations:
        raise ValueError("patch contains no operations")
    return operations


# ---------------------------------------------------------------------------
# Validation & execution
# ---------------------------------------------------------------------------

def validate_operations(operations):
    for operation in operations:
        if operation.kind in {"delete", "update"} and not operation.path.exists():
            raise ValueError(f"file not found: {operation.path}")
        if operation.kind in {"update", "write"} and operation.path.is_dir():
            raise ValueError(f"path is a directory: {operation.path}")


def updated_text(operation):
    """Apply a V4A update operation to the file on disk.

    Each section's context is used independently for positioning;
    hunks specify what to delete and insert.  Sections are processed
    sequentially with a cursor that advances after each match.
    """
    text = operation.path.read_text(encoding="utf-8")
    return _apply_sections(text, operation.sections)


def plan_changes(operations):
    changes = []
    for operation in operations:
        if operation.kind == "write":
            text = ensure_trailing_newline("\n".join(operation.new_lines))
            changes.append(("write", operation.path, text))
        elif operation.kind == "delete":
            changes.append(("delete", operation.path, None))
        elif operation.kind == "update":
            changes.append(("write", operation.path, updated_text(operation)))
    return changes


def result_verb(kind):
    return {
        "write": "wrote",
        "delete": "deleted",
        "update": "updated",
    }[kind]


def apply_patch(patch):
    if not isinstance(patch, str):
        return "Error: patch must be a string"

    try:
        operations = parse_patch(patch)
        validate_operations(operations)
        changes = plan_changes(operations)
    except (OSError, UnicodeError, ValueError) as exc:
        return f"Error: {exc}"

    results = []
    for operation, change in zip(operations, changes):
        action, path, text = change
        try:
            if action == "write":
                path.parent.mkdir(parents=True, exist_ok=True)
                path.write_text(text, encoding="utf-8")
            elif action == "delete":
                path.unlink()
        except OSError as exc:
            return f"Error: {exc}"
        results.append(f"{result_verb(operation.kind)} {operation.path}")

    return "Patch applied:\n" + "\n".join(results)


# ---------------------------------------------------------------------------
# Tool schema
# ---------------------------------------------------------------------------

schema = {
    "type": "function",
    "function": {
        "name": "apply_patch",
        "description": (
            "Apply a structured patch to local files. Three operations: "
            "*** Write File (create or overwrite), *** Update File (V4A diff), "
            "*** Delete File.  V4A lines: @@ (hunk header), space (context), "
            "- (remove), + (add).  Context is used for positioning — matching "
            "is exact first, then whitespace‑tolerant."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "patch": {
                    "type": "string",
                    "description": (
                        "Patch text beginning with *** Begin Patch and ending with "
                        "*** End Patch.  Inside, use *** Write File: <path> followed "
                        "by the full file content, *** Delete File: <path>, or "
                        "*** Update File: <path> followed by a V4A diff where every "
                        "line starts with a space (context), - (remove), + (add), "
                        "or @@ (hunk header)."
                    ),
                },
            },
            "required": ["patch"],
        },
    },
}

func = apply_patch
