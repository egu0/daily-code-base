import os
import sys
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.tools import tool_schemas, tools_map
from helloworld.tools.glob.index import glob, schema


def test_glob_finds_files_by_pattern_sorted_by_modified_time(tmp_path):
    old_file = tmp_path / "old.py"
    new_file = tmp_path / "pkg" / "new.py"
    ignored = tmp_path / "notes.md"
    new_file.parent.mkdir()
    old_file.write_text("old\n", encoding="utf-8")
    new_file.write_text("new\n", encoding="utf-8")
    ignored.write_text("notes\n", encoding="utf-8")
    os.utime(old_file, (100, 100))
    os.utime(new_file, (200, 200))

    result = glob("**/*.py", path=str(tmp_path))

    assert result.splitlines() == [
        f"Files matching **/*.py in {tmp_path}:",
        str(new_file),
        str(old_file),
    ]


def test_glob_reports_no_matches(tmp_path):
    (tmp_path / "notes.txt").write_text("alpha\n", encoding="utf-8")

    result = glob("*.py", path=str(tmp_path))

    assert result == "No files found"


def test_glob_tool_is_registered():
    assert schema["function"]["name"] == "glob"
    assert tools_map["glob"] is glob
    assert any(item["function"]["name"] == "glob" for item in tool_schemas)
