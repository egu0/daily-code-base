import sys
import threading
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.agent import (  # noqa: E402
    SessionState,
    create_tool_approval,
    resolve_tool_approval,
    session_payload,
    tool_is_enabled,
    wait_for_tool_approval,
)


def test_tool_approval_allows_waiting_call():
    session = SessionState(id="sess_test")
    create_tool_approval(session, "call_1", "read", {"path": "."})

    result = {}
    waiter = threading.Thread(
        target=lambda: result.setdefault(
            "approved", wait_for_tool_approval(session, "call_1")
        )
    )
    waiter.start()

    assert resolve_tool_approval(session, "call_1", True) is True
    waiter.join(timeout=1)

    assert result["approved"] is True
    assert "call_1" not in session.pending_tool_approvals


def test_tool_approval_denies_waiting_call():
    session = SessionState(id="sess_test")
    create_tool_approval(session, "call_1", "read", {"path": "."})

    result = {}
    waiter = threading.Thread(
        target=lambda: result.setdefault(
            "approved", wait_for_tool_approval(session, "call_1")
        )
    )
    waiter.start()

    assert resolve_tool_approval(session, "call_1", False) is True
    waiter.join(timeout=1)

    assert result["approved"] is False
    assert "call_1" not in session.pending_tool_approvals


def test_tool_approval_rejects_unknown_call():
    session = SessionState(id="sess_test")

    assert resolve_tool_approval(session, "missing", True) is False


def test_session_payload_includes_pending_tool_approvals():
    session = SessionState(id="sess_test")
    create_tool_approval(session, "call_1", "read", {"path": "."})

    payload = session_payload(session)

    assert payload["pendingToolApprovals"] == [
        {"toolCallId": "call_1", "name": "read", "args": {"path": "."}}
    ]


def test_tool_is_enabled_rejects_disabled_tool():
    assert tool_is_enabled("write", ["read", "apply_patch"]) is False


def test_tool_is_enabled_allows_enabled_tool():
    assert tool_is_enabled("write", ["read", "write"]) is True
