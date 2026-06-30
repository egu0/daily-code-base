import sys
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.tools import tool_schemas, tools_map
from helloworld.tools.apply_patch.index import apply_patch, schema


# -- Write File (create) -------------------------------------------------------

def test_write_file_creates_new_file(tmp_path):
    path = tmp_path / "hello.txt"
    patch = f"""*** Begin Patch
*** Write File: {path}
hello
world
*** End Patch
"""

    result = apply_patch(patch)

    assert path.read_text(encoding="utf-8") == "hello\nworld\n"
    assert "wrote" in result


def test_write_file_overwrites_existing_file(tmp_path):
    path = tmp_path / "notes.md"
    path.write_text("old content\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Write File: {path}
fresh content
*** End Patch
"""

    result = apply_patch(patch)

    assert path.read_text(encoding="utf-8") == "fresh content\n"
    assert "wrote" in result


def test_add_file_alias_creates_new_file(tmp_path):
    path = tmp_path / "hello.txt"
    patch = f"""*** Begin Patch
*** Add File: {path}
hello
*** End Patch
"""

    result = apply_patch(patch)

    assert path.read_text(encoding="utf-8") == "hello\n"
    assert "wrote" in result


# -- Update File (V4A diff — the only update format) ---------------------------

def test_update_file_v4a_diff(tmp_path):
    path = tmp_path / "app.py"
    path.write_text("def hello():\n    return 'hi'\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Update File: {path}
@@
 def hello():
-    return 'hi'
+    return 'hello'
*** End Patch
"""

    result = apply_patch(patch)

    assert path.read_text(encoding="utf-8") == "def hello():\n    return 'hello'\n"
    assert "updated" in result


def test_update_file_multiline_context(tmp_path):
    path = tmp_path / "code.py"
    path.write_text("one\ntwo\nthree\nfour\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Update File: {path}
@@
 one
-two
-three
+2
+3
 four
*** End Patch
"""

    result = apply_patch(patch)

    assert path.read_text(encoding="utf-8") == "one\n2\n3\nfour\n"
    assert "updated" in result


def test_update_ignores_text_after_at_at_separator(tmp_path):
    path = tmp_path / "app.py"
    path.write_text("def real():\n    return 'old'\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Update File: {path}
@@ def imaginary_anchor():
 def real():
-    return 'old'
+    return 'new'
*** End Patch
"""

    result = apply_patch(patch)

    assert path.read_text(encoding="utf-8") == "def real():\n    return 'new'\n"
    assert "updated" in result


def test_update_rejects_when_context_does_not_match(tmp_path):
    path = tmp_path / "app.py"
    path.write_text("actual\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Update File: {path}
@@
-expected
+changed
*** End Patch
"""

    result = apply_patch(patch)

    assert path.read_text(encoding="utf-8") == "actual\n"
    assert "Error:" in result
    assert "context not found" in result


def test_update_matches_first_when_context_appears_twice(tmp_path):
    """With chunk-based application, context is used for positioning —
    the first match wins.  This is more robust than rejecting duplicates."""
    path = tmp_path / "dup.txt"
    path.write_text("dup\ndup\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Update File: {path}
@@
-dup
+unique
*** End Patch
"""

    result = apply_patch(patch)

    # First 'dup' is replaced; second remains
    assert path.read_text(encoding="utf-8") == "unique\ndup\n"
    assert "updated" in result


def test_update_rejects_invalid_line_prefix(tmp_path):
    path = tmp_path / "f.txt"
    path.write_text("line\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Update File: {path}
#invalid
*** End Patch
"""

    result = apply_patch(patch)

    assert "Error:" in result
    assert "must start with a space" in result


# -- Delete File ---------------------------------------------------------------

def test_delete_file_removes_existing(tmp_path):
    path = tmp_path / "old.txt"
    path.write_text("bye\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Delete File: {path}
*** End Patch
"""

    result = apply_patch(patch)

    assert not path.exists()
    assert "deleted" in result


def test_delete_rejects_missing_file(tmp_path):
    path = tmp_path / "gone.txt"
    patch = f"""*** Begin Patch
*** Delete File: {path}
*** End Patch
"""

    result = apply_patch(patch)

    assert "Error:" in result
    assert "file not found" in result


def test_delete_rejects_directory(tmp_path):
    path = tmp_path / "dir"
    path.mkdir()
    patch = f"""*** Begin Patch
*** Delete File: {path}
*** End Patch
"""

    result = apply_patch(patch)

    assert path.exists()
    assert "Error:" in result
    assert "refusing to delete directory" in result


# -- Multi-operation atomicity -------------------------------------------------

def test_multi_op_rollback_on_failure(tmp_path):
    """When one operation fails validation, earlier operations must not
    have been applied (atomic all-or-nothing)."""
    first = tmp_path / "first.txt"
    second = tmp_path / "second.txt"
    first.write_text("alpha\n", encoding="utf-8")
    second.write_text("beta\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Update File: {first}
@@
-alpha
+changed
*** Update File: {second}
@@
-missing
+changed
*** End Patch
"""

    result = apply_patch(patch)

    assert "Error:" in result
    # first.txt must NOT have been changed
    assert first.read_text(encoding="utf-8") == "alpha\n"
    assert second.read_text(encoding="utf-8") == "beta\n"


def test_multi_op_success_applies_all(tmp_path):
    add = tmp_path / "add.txt"
    delete = tmp_path / "delete.txt"
    delete.write_text("bye\n", encoding="utf-8")
    patch = (
        f"*** Begin Patch\n"
        f"*** Write File: {add}\nnew\n"
        f"*** Delete File: {delete}\n"
        f"*** End Patch"
    )
    result = apply_patch(patch)
    assert add.read_text(encoding="utf-8") == "new\n"
    assert not delete.exists()
    assert "wrote" in result
    assert "deleted" in result


def test_multiple_patch_envelopes_in_one_call_apply_all(tmp_path):
    first = tmp_path / "first.txt"
    second = tmp_path / "second.txt"
    patch = (
        f"*** Begin Patch\n"
        f"*** Write File: {first}\nalpha\n"
        f"*** End Patch\n"
        f"*** Begin Patch\n"
        f"*** Write File: {second}\nbeta\n"
        f"*** End Patch"
    )

    result = apply_patch(patch)

    assert first.read_text(encoding="utf-8") == "alpha\n"
    assert second.read_text(encoding="utf-8") == "beta\n"
    assert "wrote" in result


def test_multiple_patch_envelopes_tolerate_blank_lines(tmp_path):
    first = tmp_path / "first.txt"
    second = tmp_path / "second.txt"
    patch = (
        f"\n"
        f"*** Begin Patch\n"
        f"*** Write File: {first}\nalpha\n"
        f"*** End Patch\n"
        f"\n"
        f"*** Begin Patch\n"
        f"*** Write File: {second}\nbeta\n"
        f"*** End Patch\n"
        f"\n"
    )

    result = apply_patch(patch)

    assert first.read_text(encoding="utf-8") == "alpha\n"
    assert second.read_text(encoding="utf-8") == "beta\n"
    assert "wrote" in result


def test_multiple_patch_envelopes_update_same_file_accumulates(tmp_path):
    path = tmp_path / "sample.txt"
    path.write_text("a\nb\nc\n", encoding="utf-8")
    patch = f"""*** Begin Patch
*** Update File: {path}
@@ -1 +1 @@
-a
+A
*** End Patch
*** Begin Patch
*** Update File: {path}
@@ -2 +2 @@
-b
+B
*** End Patch
"""

    result = apply_patch(patch)

    assert path.read_text(encoding="utf-8") == "A\nB\nc\n"
    assert "updated" in result


def test_multiple_patch_envelopes_remain_atomic(tmp_path):
    first = tmp_path / "first.txt"
    missing = tmp_path / "missing.txt"
    patch = (
        f"*** Begin Patch\n"
        f"*** Write File: {first}\nalpha\n"
        f"*** End Patch\n"
        f"*** Begin Patch\n"
        f"*** Delete File: {missing}\n"
        f"*** End Patch"
    )

    result = apply_patch(patch)

    assert "Error:" in result
    assert not first.exists()


# -- Schema & registration -----------------------------------------------------

def test_schema_describes_v4a_format():
    description = schema["function"]["description"]
    patch_description = schema["function"]["parameters"]["properties"]["patch"][
        "description"
    ]
    assert "V4A" in description
    assert "*** Write File" in description
    assert "*** Add File" in description
    assert "*** Update File" in description
    assert "*** Delete File" in description
    assert "space (context)" in patch_description
    assert "- (remove)" in patch_description
    assert "+ (add)" in patch_description
    assert "hunk separator" in patch_description
    assert "text after @@" in patch_description
    assert "Multiple patch blocks" in patch_description
    assert "*** End Patch\n*** Begin Patch" in patch_description


def test_apply_patch_tool_is_registered():
    assert schema["function"]["name"] == "apply_patch"
    assert tools_map["apply_patch"] is apply_patch
    assert any(item["function"]["name"] == "apply_patch" for item in tool_schemas)
