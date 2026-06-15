from __future__ import annotations

import json
from pathlib import Path

from src.utils import sanitize_for_path, write_json, write_text


# ---- sanitize_for_path ---------------------------------------------------

def test_sanitize_replaces_special_chars() -> None:
    assert sanitize_for_path('A/B:C*D?E"F<G>H|I') == "A-B-C-D-E-F-G-H-I"


def test_sanitize_collapses_whitespace() -> None:
    assert sanitize_for_path("hello   world\t\nnew") == "hello world new"


def test_sanitize_strips_leading_trailing_punctuation_and_space() -> None:
    assert sanitize_for_path("  .-hello world-.  ") == "hello world"


def test_sanitize_falls_back_to_untitled_when_result_empty() -> None:
    assert sanitize_for_path(r'*:?<>|/\\') == "untitled"


def test_sanitize_handles_chinese_characters() -> None:
    assert sanitize_for_path("澳洲482内定名额，") == "澳洲482内定名额，"


# ---- write_json ----------------------------------------------------------

def test_write_json_creates_parent_dirs_and_writes_content(tmp_path: Path) -> None:
    target = tmp_path / "deeply" / "nested" / "output.json"
    payload = {"key": "value", "list": [1, 2, 3]}

    write_json(target, payload)

    assert target.exists()
    assert target.parent.exists()
    loaded = json.loads(target.read_text(encoding="utf-8"))
    assert loaded == payload


def test_write_json_handles_unicode_payload(tmp_path: Path) -> None:
    target = tmp_path / "unicode.json"
    payload = {"title": "澳洲482内定名额", "emoji": "🎉"}

    write_json(target, payload)

    loaded = json.loads(target.read_text(encoding="utf-8"))
    assert loaded["title"] == "澳洲482内定名额"
    assert loaded["emoji"] == "🎉"


# ---- write_text ----------------------------------------------------------

def test_write_text_creates_parent_dirs_and_writes_content(tmp_path: Path) -> None:
    target = tmp_path / "deep" / "log.txt"
    content = "line 1\nline 2\n"

    write_text(target, content)

    assert target.exists()
    assert target.read_text(encoding="utf-8") == content
