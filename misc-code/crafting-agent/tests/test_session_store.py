import sys
import json
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.session_store import (
    create_session,
    latest_session,
    list_sessions,
    load_session,
    record_request,
    record_response,
    save_session,
)


def test_create_session_persists_messages(tmp_path):
    session = create_session(
        [{"role": "system", "content": "hello"}],
        session_dir=tmp_path,
        session_id="sess_test",
    )

    loaded = load_session("sess_test", session_dir=tmp_path)

    assert session.id == "sess_test"
    assert (tmp_path / "sess_test" / "session.json").exists()
    assert loaded is not None
    assert loaded.messages == [{"role": "system", "content": "hello"}]


def test_save_session_updates_existing_messages(tmp_path):
    session = create_session(
        [{"role": "system", "content": "hello"}],
        session_dir=tmp_path,
        session_id="sess_test",
    )
    session.messages.append({"role": "user", "content": "continue"})

    save_session(session, session_dir=tmp_path)

    loaded = load_session("sess_test", session_dir=tmp_path)
    assert loaded.messages[-1] == {"role": "user", "content": "continue"}


def test_latest_session_returns_most_recently_updated(tmp_path):
    old = create_session([], session_dir=tmp_path, session_id="sess_old")
    new = create_session([], session_dir=tmp_path, session_id="sess_new")
    old.updated_at = "2026-01-01T00:00:00+00:00"
    new.updated_at = "2026-01-02T00:00:00+00:00"
    save_session(old, session_dir=tmp_path)
    save_session(new, session_dir=tmp_path)

    assert latest_session(session_dir=tmp_path).id == "sess_new"


def test_list_sessions_sorts_newest_first(tmp_path):
    first = create_session([], session_dir=tmp_path, session_id="sess_first")
    second = create_session([], session_dir=tmp_path, session_id="sess_second")
    first.updated_at = "2026-01-01T00:00:00+00:00"
    second.updated_at = "2026-01-02T00:00:00+00:00"
    save_session(first, session_dir=tmp_path)
    save_session(second, session_dir=tmp_path)

    assert [session.id for session in list_sessions(session_dir=tmp_path)] == [
        "sess_second",
        "sess_first",
    ]


def test_invalid_session_id_is_rejected(tmp_path):
    assert load_session("../escape", session_dir=tmp_path) is None


def test_record_request_writes_incrementing_json_files(tmp_path):
    session = create_session([], session_dir=tmp_path, session_id="sess_test")

    first = record_request(session, {"model": "m1"}, session_dir=tmp_path)
    second = record_request(session, {"model": "m2"}, session_dir=tmp_path)

    assert first == tmp_path / "sess_test" / "1.json"
    assert second == tmp_path / "sess_test" / "2.json"
    assert json.loads(first.read_text(encoding="utf-8"))["request"] == {"model": "m1"}
    assert json.loads(second.read_text(encoding="utf-8"))["request"] == {"model": "m2"}


def test_record_response_updates_existing_request_file(tmp_path):
    session = create_session([], session_dir=tmp_path, session_id="sess_test")
    path = record_request(session, {"model": "m1"}, session_dir=tmp_path)

    record_response(path, {"content": "answer", "reasoning_content": "thinking"})

    payload = json.loads(path.read_text(encoding="utf-8"))
    assert payload["request"] == {"model": "m1"}
    assert payload["response"] == {
        "content": "answer",
        "reasoning_content": "thinking",
    }
