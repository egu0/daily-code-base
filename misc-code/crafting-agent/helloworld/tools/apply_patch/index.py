from dataclasses import dataclass, field
from pathlib import Path
from typing import Literal

from helloworld.config import agent_workdir

enabled = True


def _resolve_workspace() -> Path:
    return agent_workdir()


# ---------------------------------------------------------------------------
# Data structures
# ---------------------------------------------------------------------------


@dataclass
class Hunk:
    """One edit hunk within a V4A diff section.

    orig_index is the offset within the matched context where del_lines should
    be removed and ins_lines inserted.
    """

    orig_index: int
    del_lines: list[str]
    ins_lines: list[str]


@dataclass
class PatchOperation:
    kind: Literal["write", "delete", "update"]
    path: Path
    new_lines: list[str] = field(default_factory=list)
    sections: list[tuple[list[str], list[Hunk]]] = field(default_factory=list)


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------


def ensure_trailing_newline(text: str) -> str:
    return text if text.endswith("\n") or text == "" else text + "\n"


def path_from_directive(line: str, prefix: str) -> Path:
    raw = line.removeprefix(prefix).strip()
    if not raw:
        raise ValueError(f"missing path in directive: {line}")

    workspace = _resolve_workspace()

    # Relative paths are resolved from the workspace.
    # Absolute paths are allowed only if they still resolve inside the workspace.
    candidate = Path(raw).expanduser()
    if not candidate.is_absolute():
        candidate = workspace / candidate
    candidate = candidate.resolve()

    try:
        candidate.relative_to(workspace)
    except ValueError:
        raise ValueError(f"path escapes workspace: {raw}")

    return candidate


# ---------------------------------------------------------------------------
# V4A diff parser
# ---------------------------------------------------------------------------


def _read_v4a_section(
    lines: list[str],
    start: int,
) -> tuple[list[str], list[Hunk], int]:
    """Read one V4A section starting at lines[start].

    Returns (context, hunks, end_index).

    context is the full context text made from:
    - space-prefixed context lines
    - minus-prefixed delete lines

    hunks are the edit operations keyed by offset within that context.

    Stops at:
    - @@ hunk separator
    - *** patch directive
    - EOF

    Important:
    @@ may be followed by extra text, but this implementation does not parse it.
    The content after @@ is ignored and only acts as a hunk/section separator.
    """

    context: list[str] = []
    del_lines: list[str] = []
    ins_lines: list[str] = []
    hunks: list[Hunk] = []

    idx = start

    while idx < len(lines):
        line = lines[idx]

        # Section terminators.
        # @@ is only a separator. Anything after @@ is intentionally ignored.
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
            # Switching back to context after add/delete emits the pending hunk.
            if del_lines or ins_lines:
                orig = len(context) - len(del_lines)
                hunks.append(Hunk(orig, list(del_lines), list(ins_lines)))
                del_lines.clear()
                ins_lines.clear()

            context.append(content)

        else:
            raise ValueError(
                "each update line must start with a space (context), "
                "- (remove), + (add), or @@ (hunk separator)"
            )

        idx += 1

    # Emit any trailing hunk.
    if del_lines or ins_lines:
        orig = len(context) - len(del_lines)
        hunks.append(Hunk(orig, list(del_lines), list(ins_lines)))

    return context, hunks, idx


def parse_update_diff(
    update_lines: list[str],
) -> list[tuple[list[str], list[Hunk]]]:
    """Parse V4A update lines into a list of (context, hunks) sections.

    Each @@ line starts a new section.

    Important:
    The text after @@ is not parsed, not used as an anchor, and not interpreted
    as a git/unified-diff line range. For example:

        @@ -10,7 +10,12 @@
        @@ function_name
        @@

    are all treated the same way by this parser: as section separators.
    Actual positioning depends on the context/delete lines below the @@ line.
    """

    if not update_lines:
        raise ValueError("update block is empty")

    sections: list[tuple[list[str], list[Hunk]]] = []
    idx = 0

    while idx < len(update_lines):
        line = update_lines[idx]

        # Skip hunk separator lines.
        # Anything after @@ is intentionally ignored.
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


def _find_context(
    file_lines: list[str],
    context: list[str],
    start_cursor: int,
) -> int:
    """Find context in file_lines starting from start_cursor.

    Returns the index where context begins, or -1 if not found.

    Matching strategy:
    1. exact match
    2. rstrip match
    3. strip match
    """

    if start_cursor < 0:
        start_cursor = 0

    if start_cursor > len(file_lines):
        start_cursor = len(file_lines)

    if not context:
        return start_cursor

    matchers = [
        lambda x: x,
        lambda x: x.rstrip(),
        lambda x: x.strip(),
    ]

    for match_fn in matchers:
        for i in range(start_cursor, len(file_lines) - len(context) + 1):
            if all(
                match_fn(file_lines[i + j]) == match_fn(context[j])
                for j in range(len(context))
            ):
                return i

    return -1


def _lines_match_with_fuzz(actual: list[str], expected: list[str]) -> bool:
    """Match line slices using the same exact/rstrip/strip fallback as context."""

    if len(actual) != len(expected):
        return False

    matchers = [
        lambda x: x,
        lambda x: x.rstrip(),
        lambda x: x.strip(),
    ]

    return any(
        all(match_fn(left) == match_fn(right) for left, right in zip(actual, expected))
        for match_fn in matchers
    )


def _apply_sections(
    file_text: str,
    sections: list[tuple[list[str], list[Hunk]]],
) -> str:
    """Apply parsed V4A sections to file_text.

    Each section is (context, hunks).

    context is used to locate the edit site. hunks specify what to delete and
    insert.

    This function does not mutate the parsed Hunk objects.
    """

    file_lines = file_text.splitlines(keepends=True)
    comparable_lines = [line.rstrip("\n").rstrip("\r") for line in file_lines]

    cursor = 0
    absolute_hunks: list[Hunk] = []

    for context, hunks in sections:
        matched = _find_context(comparable_lines, context, cursor)

        if matched < 0:
            ctx_preview = "\n".join(context[:6])
            raise ValueError(f"context not found (cursor {cursor}):\n{ctx_preview}")

        # Convert section-relative hunk offsets to file-absolute offsets.
        # Do not mutate the original Hunk objects.
        for hunk in hunks:
            absolute_hunks.append(
                Hunk(
                    orig_index=matched + hunk.orig_index,
                    del_lines=list(hunk.del_lines),
                    ins_lines=list(hunk.ins_lines),
                )
            )

        cursor = matched + len(context)

    # Apply hunks in reverse order so earlier replacements do not shift later
    # target indices.
    for hunk in sorted(absolute_hunks, key=lambda h: h.orig_index, reverse=True):
        del_end = hunk.orig_index + len(hunk.del_lines)

        existing = [
            line.rstrip("\n").rstrip("\r")
            for line in file_lines[hunk.orig_index : del_end]
        ]

        if not _lines_match_with_fuzz(existing, hunk.del_lines):
            expected = "\n".join(hunk.del_lines[:6])
            actual = "\n".join(existing[:6])
            raise ValueError(
                "delete target mismatch:\n"
                f"expected:\n{expected}\n"
                f"actual:\n{actual}"
            )

        inserted = [line + "\n" for line in hunk.ins_lines]
        file_lines[hunk.orig_index : del_end] = inserted

    return "".join(file_lines)


# ---------------------------------------------------------------------------
# Patch parser
# ---------------------------------------------------------------------------


def parse_patch(patch: str) -> list[PatchOperation]:
    lines = patch.splitlines()

    while lines and not lines[0].strip():
        lines.pop(0)
    while lines and not lines[-1].strip():
        lines.pop()

    if not lines or lines[0] != "*** Begin Patch":
        raise ValueError("patch must start with *** Begin Patch")

    operations: list[PatchOperation] = []
    index = 0

    while index < len(lines):
        while index < len(lines) and not lines[index].strip():
            index += 1
        if index >= len(lines):
            break

        if lines[index] != "*** Begin Patch":
            raise ValueError(f"patch block must start with *** Begin Patch: {lines[index]}")

        index += 1

        while index < len(lines):
            line = lines[index]

            if line == "*** End Patch":
                index += 1
                break

            # --- Write File / Add File --------------------------------------
            #
            # Write File is the original local operation name.
            # Add File is accepted as a V4A-style alias.
            if line.startswith("*** Write File: ") or line.startswith("*** Add File: "):
                prefix = (
                    "*** Write File: "
                    if line.startswith("*** Write File: ")
                    else "*** Add File: "
                )

                path = path_from_directive(line, prefix)
                index += 1

                new_lines: list[str] = []
                while index < len(lines) and not lines[index].startswith("*** "):
                    new_lines.append(lines[index])
                    index += 1

                operations.append(PatchOperation("write", path, new_lines=new_lines))
                continue

            # --- Delete File ------------------------------------------------
            if line.startswith("*** Delete File: "):
                path = path_from_directive(line, "*** Delete File: ")
                operations.append(PatchOperation("delete", path))
                index += 1
                continue

            # --- Update File ------------------------------------------------
            if line.startswith("*** Update File: "):
                path = path_from_directive(line, "*** Update File: ")
                index += 1

                update_lines: list[str] = []
                while index < len(lines) and not lines[index].startswith("*** "):
                    update_lines.append(lines[index])
                    index += 1

                sections = parse_update_diff(update_lines)
                operations.append(PatchOperation("update", path, sections=sections))
                continue

            raise ValueError(f"unknown patch directive: {line}")
        else:
            raise ValueError("patch must end with *** End Patch")

    if not operations:
        raise ValueError("patch contains no operations")

    return operations


# ---------------------------------------------------------------------------
# Validation & execution
# ---------------------------------------------------------------------------


def validate_operations(operations: list[PatchOperation]) -> None:
    for operation in operations:
        if operation.kind in {"delete", "update"} and not operation.path.exists():
            raise ValueError(f"file not found: {operation.path}")

        if operation.kind in {"update", "write"} and operation.path.is_dir():
            raise ValueError(f"path is a directory: {operation.path}")

        if operation.kind == "delete" and operation.path.is_dir():
            raise ValueError(f"refusing to delete directory: {operation.path}")


def updated_text(operation: PatchOperation) -> str:
    """Apply a V4A update operation to the file on disk.

    Each section's context is used independently for positioning. hunks specify
    what to delete and insert. Sections are processed sequentially with a cursor
    that advances after each match.
    """

    text = operation.path.read_text(encoding="utf-8")
    return _apply_sections(text, operation.sections)


def plan_changes(
    operations: list[PatchOperation],
) -> list[tuple[Literal["write", "delete"], Path, str | None]]:
    pending: dict[Path, str | None] = {}

    for operation in operations:
        if operation.kind == "write":
            text = ensure_trailing_newline("\n".join(operation.new_lines))
            pending[operation.path] = text

        elif operation.kind == "delete":
            pending[operation.path] = None

        elif operation.kind == "update":
            base_text = pending.get(operation.path)
            if base_text is None:
                base_text = operation.path.read_text(encoding="utf-8")
            pending[operation.path] = _apply_sections(base_text, operation.sections)

    return [
        ("delete", path, None) if text is None else ("write", path, text)
        for path, text in pending.items()
    ]


def result_verb(kind: Literal["write", "delete", "update"]) -> str:
    return {
        "write": "wrote",
        "delete": "deleted",
        "update": "updated",
    }[kind]


def apply_patch(patch: str) -> str:
    if not isinstance(patch, str):
        return "Error: patch must be a string"

    try:
        operations = parse_patch(patch)
        validate_operations(operations)
        changes = plan_changes(operations)

    except (OSError, UnicodeError, ValueError) as exc:
        return f"Error: {exc}"

    results: list[str] = []

    for change in changes:
        action, path, text = change

        try:
            if action == "write":
                path.parent.mkdir(parents=True, exist_ok=True)
                assert text is not None
                path.write_text(text, encoding="utf-8")

            elif action == "delete":
                path.unlink()

        except OSError as exc:
            return f"Error: {exc}"

    for operation in operations:
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
            "Apply a structured patch to local files. "
            "Operations: "
            "*** Write File (create or overwrite), "
            "*** Add File (alias of Write File), "
            "*** Update File (V4A-style diff), "
            "*** Delete File. "
            "V4A update lines: @@ (hunk separator), space (context), "
            "- (remove), + (add). "
            "Important: content after @@ is not parsed and is not used as an "
            "anchor; the @@ line is only a hunk/section separator. "
            "Context/delete lines below @@ are used for positioning. "
            "Matching is exact first, then whitespace-tolerant."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "patch": {
                    "type": "string",
                    "description": (
                        "Patch text beginning with *** Begin Patch and ending "
                        "with *** End Patch. "
                        "Inside, use *** Write File: or *** Add File: followed "
                        "by the full file content, *** Delete File:, or "
                        "*** Update File: followed by a V4A-style diff where "
                        "every update line starts with a space (context), "
                        "- (remove), + (add), or @@ (hunk separator). "
                        "The text after @@ is ignored. For example, "
                        "@@, @@ function_name, and @@ -10,7 +10,12 @@ are all "
                        "treated as separators, not as parsed anchors or line "
                        "ranges. "
                        "Multiple patch blocks may be concatenated in one "
                        "call; blank lines before, after, or between blocks "
                        "are ignored. Example: "
                        "*** Begin Patch\n"
                        "*** Update File: sample.txt\n"
                        "@@\n"
                        "-a\n"
                        "+A\n"
                        "*** End Patch\n"
                        "*** Begin Patch\n"
                        "*** Update File: sample.txt\n"
                        "@@\n"
                        "-b\n"
                        "+B\n"
                        "*** End Patch"
                    ),
                },
            },
            "required": ["patch"],
        },
    },
}


func = apply_patch
