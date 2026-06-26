import sys
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.tools import tool_schemas, tools_map
from helloworld.tools.grep.index import grep, schema


def test_grep_finds_regex_matches_with_line_and_column(tmp_path):
    notes = tmp_path / "notes.txt"
    notes.write_text("alpha\nbeta gamma\nGamma beta\n", encoding="utf-8")

    result = grep("beta", path=str(tmp_path))

    assert "Matches for /beta/" in result
    assert f"{notes}:2:1:beta gamma" in result
    assert f"{notes}:3:7:Gamma beta" in result


def test_grep_supports_file_glob_filter(tmp_path):
    (tmp_path / "app.py").write_text("needle\n", encoding="utf-8")
    (tmp_path / "notes.md").write_text("needle\n", encoding="utf-8")

    result = grep("needle", path=str(tmp_path), glob="*.py")

    assert "app.py" in result
    assert "notes.md" not in result


def test_grep_reports_no_matches(tmp_path):
    (tmp_path / "notes.txt").write_text("alpha\n", encoding="utf-8")

    result = grep("missing", path=str(tmp_path))

    assert result == "No matches found"


def test_grep_tool_is_registered():
    assert schema["function"]["name"] == "grep"
    assert tools_map["grep"] is grep
    assert any(item["function"]["name"] == "grep" for item in tool_schemas)
