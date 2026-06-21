"""Unit tests for apply_patch core functions (V4A chunk‑based design).

After the redesign the update algorithm is aligned with the OpenAI Agents
SDK ``apply_diff.py``: context is used for *positioning*, hunks specify
what to delete and insert at offsets within the matched context.
"""

import os
import sys
from pathlib import Path

import pytest

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.tools.apply_patch.index import (
    Hunk,
    PatchOperation,
    apply_patch,
    ensure_trailing_newline,
    line_text,
    parse_patch,
    parse_update_diff,
    path_from_directive,
    plan_changes,
    result_verb,
    updated_text,
    validate_operations,
)

# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------


class TestLineText:
    def test_strips_leading_plus(self):
        assert line_text("+hello") == "hello"

    def test_strips_leading_minus(self):
        assert line_text("-world") == "world"

    def test_strips_leading_space(self):
        assert line_text(" unchanged") == "unchanged"

    def test_single_char_returns_empty(self):
        assert line_text("+") == ""

    def test_empty_string_returns_empty(self):
        assert line_text("") == ""


class TestEnsureTrailingNewline:
    def test_already_ends_with_newline(self):
        assert ensure_trailing_newline("hello\n") == "hello\n"

    def test_adds_missing_newline(self):
        assert ensure_trailing_newline("hello") == "hello\n"

    def test_empty_string_unchanged(self):
        assert ensure_trailing_newline("") == ""

    def test_multiline_already_ends_newline(self):
        assert ensure_trailing_newline("a\nb\n") == "a\nb\n"

    def test_multiline_adds_newline(self):
        assert ensure_trailing_newline("a\nb") == "a\nb\n"


class TestResultVerb:
    def test_write(self):
        assert result_verb("write") == "wrote"

    def test_delete(self):
        assert result_verb("delete") == "deleted"

    def test_update(self):
        assert result_verb("update") == "updated"

    def test_unknown_kind_raises(self):
        with pytest.raises(KeyError):
            result_verb("unknown")


# ---------------------------------------------------------------------------
# parse_update_diff — V4A chunk‑based parser
# ---------------------------------------------------------------------------


class TestParseUpdateDiff:
    """parse_update_diff splits V4A lines into (context, hunks) sections."""

    def test_basic_section(self):
        sections = parse_update_diff([
            " keep",
            "-remove",
            "+add",
            " more",
        ])
        assert len(sections) == 1
        context, hunks = sections[0]
        # context = keep + remove + more  (delete lines are in context too)
        assert context == ["keep", "remove", "more"]
        assert len(hunks) == 1
        assert hunks[0].orig_index == 1  # len(["keep"]) - 0 = 1?  Wait...
        # Actually: when we hit '+' we're in ins mode, when we hit ' ' we emit.
        # Sequence: ' ' keep → context=['keep']; '-' remove → del=['remove'], context=['keep','remove'];
        # '+' add → ins=['add']; ' ' more → emit hunk, then context+=more.
        # orig_index = len(['keep','remove']) - len(['remove']) = 1.  Correct!
        assert hunks[0].del_lines == ["remove"]
        assert hunks[0].ins_lines == ["add"]

    def test_skips_at_sign_headers(self):
        sections = parse_update_diff([
            "@@ -1 +1 @@",
            " unchanged",
            "-old",
            "+new",
        ])
        assert len(sections) == 1
        context, hunks = sections[0]
        assert context == ["unchanged", "old"]
        assert hunks[0].del_lines == ["old"]
        assert hunks[0].ins_lines == ["new"]

    def test_pure_addition(self):
        sections = parse_update_diff([
            " before",
            "+inserted",
            " after",
        ])
        _, hunks = sections[0]
        assert hunks[0].del_lines == []
        assert hunks[0].ins_lines == ["inserted"]

    def test_pure_deletion(self):
        sections = parse_update_diff([
            " before",
            "-middle",
            " after",
        ])
        context, hunks = sections[0]
        assert context == ["before", "middle", "after"]
        assert hunks[0].del_lines == ["middle"]
        assert hunks[0].ins_lines == []

    def test_multiple_sections_from_at_signs(self):
        """Each @@ starts a new section."""
        sections = parse_update_diff([
            "@@",
            " a",
            "-b",
            "+B",
            "@@",
            " x",
            "-y",
            "+Y",
        ])
        assert len(sections) == 2
        c1, _ = sections[0]
        c2, _ = sections[1]
        assert c1 == ["a", "b"]
        assert c2 == ["x", "y"]

    def test_rejects_invalid_prefix(self):
        with pytest.raises(ValueError, match="must start with a space"):
            parse_update_diff([" valid", "#bad"])

    def test_rejects_empty_lines(self):
        with pytest.raises(ValueError, match="update block is empty"):
            parse_update_diff([])

    def test_context_only_is_valid(self):
        sections = parse_update_diff([
            " line1",
            " line2",
        ])
        context, hunks = sections[0]
        assert context == ["line1", "line2"]
        assert hunks == []

    def test_empty_line_treated_as_keep(self):
        """An empty diff line is treated as keep (space prefix)."""
        sections = parse_update_diff([
            " before",
            "",
            "+added",
            " after",
        ])
        context, hunks = sections[0]
        # empty line → prefix '' → treated as space → content '' → added to context
        assert "" in context
        assert hunks[0].ins_lines == ["added"]


# ---------------------------------------------------------------------------
# parse_patch
# ---------------------------------------------------------------------------


class TestParsePatch:
    def test_single_write(self, tmp_path):
        p = tmp_path / "f.txt"
        ops = parse_patch(
            f"*** Begin Patch\n*** Write File: {p}\nhello\nworld\n*** End Patch"
        )
        assert len(ops) == 1
        assert ops[0].kind == "write"
        assert ops[0].path == p
        assert ops[0].new_lines == ["hello", "world"]

    def test_single_delete(self, tmp_path):
        p = tmp_path / "f.txt"
        ops = parse_patch(f"*** Begin Patch\n*** Delete File: {p}\n*** End Patch")
        assert len(ops) == 1
        assert ops[0].kind == "delete"
        assert ops[0].path == p

    def test_single_update(self, tmp_path):
        p = tmp_path / "f.txt"
        ops = parse_patch(
            f"*** Begin Patch\n*** Update File: {p}\n old\n-new\n+new\n*** End Patch"
        )
        assert len(ops) == 1
        assert ops[0].kind == "update"
        sections = ops[0].sections
        assert len(sections) == 1
        context, hunks = sections[0]
        # delete lines are part of context (OpenAI algorithm)
        assert context == ["old", "new"]
        assert len(hunks) == 1
        assert hunks[0].del_lines == ["new"]
        assert hunks[0].ins_lines == ["new"]

    def test_multiple_operations(self, tmp_path):
        a = tmp_path / "a.txt"
        b = tmp_path / "b.txt"
        ops = parse_patch(
            f"*** Begin Patch\n"
            f"*** Write File: {a}\nalpha\n"
            f"*** Delete File: {b}\n"
            f"*** End Patch"
        )
        assert len(ops) == 2
        assert ops[0].kind == "write"
        assert ops[1].kind == "delete"

    def test_all_three_kinds(self, tmp_path):
        w = tmp_path / "w.txt"
        u = tmp_path / "u.txt"
        d = tmp_path / "d.txt"
        ops = parse_patch(
            f"*** Begin Patch\n"
            f"*** Write File: {w}\ncontent\n"
            f"*** Update File: {u}\n old\n-new\n+NEW\n"
            f"*** Delete File: {d}\n"
            f"*** End Patch"
        )
        kinds = [op.kind for op in ops]
        assert kinds == ["write", "update", "delete"]


class TestParsePatchErrors:
    def test_missing_begin_marker(self):
        with pytest.raises(ValueError, match="must start with"):
            parse_patch("*** Write File: /tmp/x\nhi\n*** End Patch")

    def test_missing_end_marker(self):
        with pytest.raises(ValueError, match="must end with"):
            parse_patch("*** Begin Patch\n*** Write File: /tmp/x\nhi")

    def test_empty_patch(self):
        with pytest.raises(ValueError, match="must start with"):
            parse_patch("")

    def test_no_operations(self):
        with pytest.raises(ValueError, match="no operations"):
            parse_patch("*** Begin Patch\n*** End Patch")

    def test_unknown_directive(self):
        with pytest.raises(ValueError, match="unknown patch directive"):
            parse_patch("*** Begin Patch\n*** Unknown: x\n*** End Patch")


# ---------------------------------------------------------------------------
# path_from_directive
# ---------------------------------------------------------------------------


class TestPathFromDirective:
    def test_within_workspace(self, monkeypatch, tmp_path):
        monkeypatch.setenv("APPLY_PATCH_WORKSPACE", str(tmp_path))
        safe = tmp_path / "sub" / "file.txt"
        result = path_from_directive(f"*** Write File: {safe}", "*** Write File: ")
        assert result == safe

    def test_escapes_workspace_raises(self, monkeypatch, tmp_path):
        workspace = tmp_path / "ws"
        workspace.mkdir()
        monkeypatch.setenv("APPLY_PATCH_WORKSPACE", str(workspace))
        outside = tmp_path / "escape.txt"
        with pytest.raises(ValueError, match="path escapes workspace"):
            path_from_directive(f"*** Write File: {outside}", "*** Write File: ")

    def test_root_allows_absolute(self, monkeypatch, tmp_path):
        monkeypatch.setenv("APPLY_PATCH_WORKSPACE", "/")
        result = path_from_directive(
            f"*** Write File: {tmp_path / 'x.txt'}", "*** Write File: "
        )
        assert result == tmp_path / "x.txt"

    def test_dot_dot_escape_raises(self, monkeypatch, tmp_path):
        workspace = tmp_path / "ws"
        workspace.mkdir()
        monkeypatch.setenv("APPLY_PATCH_WORKSPACE", str(workspace))
        with pytest.raises(ValueError, match="path escapes workspace"):
            path_from_directive(
                f"*** Update File: {workspace / '..' / 'out.txt'}",
                "*** Update File: ",
            )

    def test_dot_dot_within_workspace(self, monkeypatch, tmp_path):
        workspace = tmp_path / "ws"
        workspace.mkdir()
        (workspace / "sub").mkdir()
        monkeypatch.setenv("APPLY_PATCH_WORKSPACE", str(workspace))
        result = path_from_directive(
            f"*** Update File: {workspace / 'sub' / '..' / 'f.txt'}",
            "*** Update File: ",
        )
        assert result == workspace / "f.txt"


# ---------------------------------------------------------------------------
# validate_operations
# ---------------------------------------------------------------------------


class TestValidateOperations:
    def test_write_accepts_new(self, tmp_path):
        ops = [PatchOperation("write", tmp_path / "new.md", new_lines=["# T"])]
        validate_operations(ops)

    def test_write_accepts_existing(self, tmp_path):
        p = tmp_path / "old.md"
        p.write_text("before", encoding="utf-8")
        ops = [PatchOperation("write", p, new_lines=["after"])]
        validate_operations(ops)

    def test_write_rejects_dir(self, tmp_path):
        d = tmp_path / "dir"
        d.mkdir()
        ops = [PatchOperation("write", d, new_lines=["x"])]
        with pytest.raises(ValueError, match="path is a directory"):
            validate_operations(ops)

    def test_delete_rejects_missing(self, tmp_path):
        ops = [PatchOperation("delete", tmp_path / "gone.txt")]
        with pytest.raises(ValueError, match="file not found"):
            validate_operations(ops)

    def test_delete_accepts_existing(self, tmp_path):
        p = tmp_path / "real.txt"
        p.write_text("x", encoding="utf-8")
        ops = [PatchOperation("delete", p)]
        validate_operations(ops)

    def test_update_rejects_missing(self, tmp_path):
        ops = [PatchOperation("update", tmp_path / "nope.txt",
                              sections=[(["a"], [Hunk(0, ["a"], ["b"])])])]
        with pytest.raises(ValueError, match="file not found"):
            validate_operations(ops)

    def test_update_rejects_dir(self, tmp_path):
        d = tmp_path / "dir"
        d.mkdir()
        ops = [PatchOperation("update", d,
                              sections=[(["a"], [Hunk(0, ["a"], ["b"])])])]
        with pytest.raises(ValueError, match="path is a directory"):
            validate_operations(ops)


# ---------------------------------------------------------------------------
# updated_text — context‑based chunk application
# ---------------------------------------------------------------------------


class TestUpdatedText:
    def test_replaces_single_occurrence(self, tmp_path):
        p = tmp_path / "f.txt"
        p.write_text("one\ntwo\nthree\n", encoding="utf-8")
        op = PatchOperation("update", p,
                            sections=[(["two"], [Hunk(0, ["two"], ["TWO"])])])
        result = updated_text(op)
        assert result == "one\nTWO\nthree\n"

    def test_context_not_found_raises(self, tmp_path):
        p = tmp_path / "f.txt"
        p.write_text("alpha\nbeta\n", encoding="utf-8")
        op = PatchOperation("update", p,
                            sections=[(["gamma"], [Hunk(0, ["gamma"], ["delta"])])])
        with pytest.raises(ValueError, match="context not found"):
            updated_text(op)

    def test_multiline_context(self, tmp_path):
        p = tmp_path / "f.txt"
        p.write_text("a\nb\nc\nd\n", encoding="utf-8")
        op = PatchOperation("update", p,
                            sections=[(["b", "c"], [Hunk(0, ["b", "c"], ["B", "C"])])])
        result = updated_text(op)
        assert result == "a\nB\nC\nd\n"

    def test_empty_new_text_is_deletion(self, tmp_path):
        p = tmp_path / "f.txt"
        p.write_text("keep\nremove\nkeep\n", encoding="utf-8")
        op = PatchOperation("update", p,
                            sections=[(["remove"], [Hunk(0, ["remove"], [])])])
        result = updated_text(op)
        assert result == "keep\nkeep\n"

    def test_fuzzy_whitespace_match(self, tmp_path):
        """Context matching tries rstrip then strip as fuzzy fallback."""
        p = tmp_path / "f.txt"
        p.write_text("  leading spaces  \ntwo\nthree\n", encoding="utf-8")
        op = PatchOperation("update", p,
                            sections=[(["  leading spaces"],
                                       [Hunk(0, ["  leading spaces"], ["trimmed"])])])
        result = updated_text(op)
        assert result == "trimmed\ntwo\nthree\n"

    def test_first_occurrence_when_duplicate(self, tmp_path):
        """Context appearing multiple times matches the first occurrence."""
        p = tmp_path / "f.txt"
        p.write_text("dup\ndup\n", encoding="utf-8")
        op = PatchOperation("update", p,
                            sections=[(["dup"], [Hunk(0, ["dup"], ["unique"])])])
        result = updated_text(op)
        assert result == "unique\ndup\n"


# ---------------------------------------------------------------------------
# plan_changes
# ---------------------------------------------------------------------------


class TestPlanChanges:
    def test_write(self, tmp_path):
        p = tmp_path / "f.txt"
        changes = plan_changes([PatchOperation("write", p, new_lines=["x"])])
        assert changes == [("write", p, "x\n")]

    def test_delete(self, tmp_path):
        p = tmp_path / "old.txt"
        changes = plan_changes([PatchOperation("delete", p)])
        assert changes == [("delete", p, None)]

    def test_update(self, tmp_path):
        p = tmp_path / "f.txt"
        p.write_text("before\nafter\n", encoding="utf-8")
        op = PatchOperation("update", p,
                            sections=[(["before"], [Hunk(0, ["before"], ["BEFORE"])])])
        changes = plan_changes([op])
        assert changes[0][0] == "write"
        assert changes[0][1] == p
        assert "BEFORE" in changes[0][2]

    def test_mixed(self, tmp_path):
        wp = tmp_path / "w.txt"
        dp = tmp_path / "d.txt"
        dp.write_text("x", encoding="utf-8")
        ops = [
            PatchOperation("write", wp, new_lines=["hello"]),
            PatchOperation("delete", dp),
        ]
        changes = plan_changes(ops)
        assert changes[0] == ("write", wp, "hello\n")
        assert changes[1] == ("delete", dp, None)


# ---------------------------------------------------------------------------
# apply_patch edge cases
# ---------------------------------------------------------------------------


class TestApplyPatchEdgeCases:
    def test_non_string_returns_error(self):
        assert apply_patch(42) == "Error: patch must be a string"
        assert apply_patch(None) == "Error: patch must be a string"
        assert apply_patch([]) == "Error: patch must be a string"

    def test_rollback_on_validation_failure(self, tmp_path):
        first = tmp_path / "first.txt"
        first.write_text("alpha\n", encoding="utf-8")
        missing = tmp_path / "missing.txt"
        patch = (
            f"*** Begin Patch\n"
            f"*** Update File: {first}\n"
            f"-alpha\n"
            f"+changed\n"
            f"*** Update File: {missing}\n"
            f"-beta\n"
            f"+changed\n"
            f"*** End Patch"
        )
        result = apply_patch(patch)
        assert "Error:" in result
        assert first.read_text(encoding="utf-8") == "alpha\n"

    def test_multi_op_success(self, tmp_path):
        wp = tmp_path / "w.txt"
        dp = tmp_path / "d.txt"
        dp.write_text("bye", encoding="utf-8")
        patch = (
            f"*** Begin Patch\n"
            f"*** Write File: {wp}\nhello\n"
            f"*** Delete File: {dp}\n"
            f"*** End Patch"
        )
        result = apply_patch(patch)
        assert "wrote" in result
        assert "deleted" in result
        assert wp.read_text(encoding="utf-8") == "hello\n"
        assert not dp.exists()

    def test_parent_dirs_created(self, tmp_path):
        deep = tmp_path / "a" / "b" / "c.txt"
        patch = f"*** Begin Patch\n*** Write File: {deep}\ndeep\n*** End Patch"
        result = apply_patch(patch)
        assert "wrote" in result
        assert deep.read_text(encoding="utf-8") == "deep\n"

    def test_multiple_hunks_in_one_update(self, tmp_path):
        """Two hunks separated by @@ in one Update File block."""
        p = tmp_path / "code.py"
        p.write_text("import os\n\ndef old_a():\n    pass\n\ndef old_b():\n    pass\n")
        patch = (
            f"*** Begin Patch\n"
            f"*** Update File: {p}\n"
            f" def old_a():\n"
            f"-    pass\n"
            f"+    return 1\n"
            f"@@\n"
            f" def old_b():\n"
            f"-    pass\n"
            f"+    return 2\n"
            f"*** End Patch"
        )
        result = apply_patch(patch)
        assert "updated" in result
        content = p.read_text(encoding="utf-8")
        assert "return 1" in content
        assert "return 2" in content
        assert "pass" not in content
