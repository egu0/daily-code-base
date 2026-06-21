import sys
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.tools.read.index import read, schema
from helloworld.tools import tools_map, tool_schemas


def write_lines(path: Path, count: int) -> None:
    path.write_text(
        "\n".join(f"line {number}" for number in range(1, count + 1)) + "\n",
        encoding="utf-8",
    )


def test_read_file_defaults_to_first_200_lines(tmp_path):
    file_path = tmp_path / "notes.txt"
    write_lines(file_path, 205)

    result = read(str(file_path))

    assert "File:" in result
    assert "Lines 1-200 of 205" in result
    assert "1 | line 1" in result
    assert "200 | line 200" in result
    assert "201 | line 201" not in result
    assert "truncated" in result


def test_read_file_supports_line_ranges(tmp_path):
    file_path = tmp_path / "notes.txt"
    write_lines(file_path, 10)

    result = read(str(file_path), start_line=3, end_line=5)

    assert "Lines 3-5 of 10" in result
    assert "2 | line 2" not in result
    assert "3 | line 3" in result
    assert "5 | line 5" in result
    assert "6 | line 6" not in result


def test_read_directory_defaults_to_first_100_entries_including_hidden(tmp_path):
    for number in range(1, 103):
        (tmp_path / f"file_{number:03}.txt").write_text("x", encoding="utf-8")
    (tmp_path / ".hidden").write_text("secret", encoding="utf-8")
    (tmp_path / "folder").mkdir()

    result = read(str(tmp_path))

    assert "Directory:" in result
    assert "Entries 1-100 of 104" in result
    assert "1 | [file] .hidden" in result
    assert "2 | [file] file_001.txt" in result
    assert "100 | [file] file_099.txt" in result
    assert "101 | [file] file_100.txt" not in result
    assert "truncated" in result


def test_read_directory_supports_entry_ranges(tmp_path):
    for name in ["a.txt", "b.txt", "c.txt", "d.txt"]:
        (tmp_path / name).write_text("x", encoding="utf-8")

    result = read(str(tmp_path), start_line=2, end_line=3)

    assert "Entries 2-3 of 4" in result
    assert "1 | [file] a.txt" not in result
    assert "2 | [file] b.txt" in result
    assert "3 | [file] c.txt" in result
    assert "4 | [file] d.txt" not in result


def test_read_tool_is_registered():
    assert schema["function"]["name"] == "read"
    assert tools_map["read"] is read
    assert any(item["function"]["name"] == "read" for item in tool_schemas)
