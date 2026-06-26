import json
import os
import re
import uuid
from dataclasses import dataclass, field
from datetime import datetime, timezone
from pathlib import Path
from typing import Any

SESSION_ID_PATTERN = re.compile(r"^[A-Za-z0-9_-]+$")
SESSION_DIR_ENV = "HELLOWORLD_SESSION_DIR"
DEFAULT_SESSION_DIR = Path(__file__).resolve().parent / "sessions"


@dataclass
class ChatSession:
    id: str
    messages: list[dict[str, Any]] = field(default_factory=list)
    created_at: str = field(
        default_factory=lambda: datetime.now(timezone.utc).isoformat()
    )
    updated_at: str = field(
        default_factory=lambda: datetime.now(timezone.utc).isoformat()
    )


def session_dir(path: str | Path | None = None) -> Path:
    if path is not None:
        return Path(path)
    return Path(os.environ.get(SESSION_DIR_ENV, DEFAULT_SESSION_DIR))


def valid_session_id(session_id: str) -> bool:
    return bool(session_id and SESSION_ID_PATTERN.fullmatch(session_id))


def session_folder(session_id: str, session_dir: str | Path | None = None) -> Path:
    if not valid_session_id(session_id):
        raise ValueError(f"invalid session id: {session_id}")
    return Path(session_dir or globals()["session_dir"]()) / session_id


def session_path(session_id: str, session_dir: str | Path | None = None) -> Path:
    return session_folder(session_id, session_dir=session_dir) / "session.json"


def session_to_json(session: ChatSession) -> dict[str, Any]:
    return {
        "id": session.id,
        "messages": session.messages,
        "created_at": session.created_at,
        "updated_at": session.updated_at,
    }


def session_from_json(payload: dict[str, Any]) -> ChatSession:
    return ChatSession(
        id=payload["id"],
        messages=payload.get("messages") or [],
        created_at=payload.get("created_at") or datetime.now(timezone.utc).isoformat(),
        updated_at=payload.get("updated_at") or datetime.now(timezone.utc).isoformat(),
    )


def create_session(
    initial_messages: list[dict[str, Any]],
    session_dir: str | Path | None = None,
    session_id: str | None = None,
) -> ChatSession:
    new_id = session_id or f"sess_{uuid.uuid4().hex}"
    if not valid_session_id(new_id):
        raise ValueError(f"invalid session id: {new_id}")

    session = ChatSession(id=new_id, messages=list(initial_messages))
    save_session(session, session_dir=session_dir)
    return session


def save_session(session: ChatSession, session_dir: str | Path | None = None) -> None:
    session.updated_at = datetime.now(timezone.utc).isoformat()
    directory = globals()["session_dir"](session_dir)
    folder = session_folder(session.id, session_dir=directory)
    folder.mkdir(parents=True, exist_ok=True)
    path = session_path(session.id, session_dir=directory)
    tmp_path = path.with_suffix(".json.tmp")
    tmp_path.write_text(
        json.dumps(session_to_json(session), ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    tmp_path.replace(path)


def load_session(
    session_id: str,
    session_dir: str | Path | None = None,
) -> ChatSession | None:
    if not valid_session_id(session_id):
        return None
    path = session_path(session_id, session_dir=session_dir)
    if not path.exists():
        return None
    payload = json.loads(path.read_text(encoding="utf-8"))
    return session_from_json(payload)


def list_sessions(session_dir: str | Path | None = None) -> list[ChatSession]:
    directory = globals()["session_dir"](session_dir)
    if not directory.exists():
        return []

    sessions = []
    for path in directory.glob("*/session.json"):
        try:
            sessions.append(session_from_json(json.loads(path.read_text(encoding="utf-8"))))
        except (OSError, ValueError, KeyError, json.JSONDecodeError):
            continue
    return sorted(sessions, key=lambda item: item.updated_at, reverse=True)


def latest_session(session_dir: str | Path | None = None) -> ChatSession | None:
    sessions = list_sessions(session_dir=session_dir)
    return sessions[0] if sessions else None


def _next_request_number(folder: Path) -> int:
    numbers = [
        int(path.stem)
        for path in folder.glob("*.json")
        if path.stem.isdigit()
    ]
    return max(numbers, default=0) + 1


def record_request(
    session: ChatSession,
    request: dict[str, Any],
    session_dir: str | Path | None = None,
) -> Path:
    folder = session_folder(session.id, session_dir=globals()["session_dir"](session_dir))
    folder.mkdir(parents=True, exist_ok=True)
    path = folder / f"{_next_request_number(folder)}.json"
    tmp_path = path.with_suffix(".json.tmp")
    tmp_path.write_text(
        json.dumps({"request": request}, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    tmp_path.replace(path)
    return path


def record_response(path: str | Path, response: dict[str, Any]) -> None:
    log_path = Path(path)
    payload = json.loads(log_path.read_text(encoding="utf-8"))
    payload["response"] = response
    tmp_path = log_path.with_suffix(".json.tmp")
    tmp_path.write_text(
        json.dumps(payload, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    tmp_path.replace(log_path)
