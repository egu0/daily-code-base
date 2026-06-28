import sys
from pathlib import Path
from types import SimpleNamespace

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld import main
from helloworld.session_store import SESSION_DIR_ENV, create_session


def test_start_session_resumes_named_session(monkeypatch, tmp_path):
    monkeypatch.setenv(SESSION_DIR_ENV, str(tmp_path))
    create_session(
        [
            {"role": "system", "content": "system"},
            {"role": "user", "content": "old"},
        ],
        session_dir=tmp_path,
        session_id="sess_keep",
    )

    session = main.start_session(main.parse_args(["--session", "sess_keep"]))

    assert session.id == "sess_keep"
    assert session.messages[-1] == {"role": "user", "content": "old"}


def test_start_session_creates_named_session_when_missing(monkeypatch, tmp_path):
    monkeypatch.setenv(SESSION_DIR_ENV, str(tmp_path))

    session = main.start_session(main.parse_args(["--session", "sess_new"]))

    assert session.id == "sess_new"
    assert (tmp_path / "sess_new" / "session.json").exists()


def test_start_session_resumes_latest(monkeypatch, tmp_path):
    monkeypatch.setenv(SESSION_DIR_ENV, str(tmp_path))
    old = create_session([], session_dir=tmp_path, session_id="sess_old")
    new = create_session([], session_dir=tmp_path, session_id="sess_new")
    old.updated_at = "2026-01-01T00:00:00+00:00"
    new.updated_at = "2026-01-02T00:00:00+00:00"
    from helloworld.session_store import save_session

    save_session(old, session_dir=tmp_path)
    save_session(new, session_dir=tmp_path)

    session = main.start_session(main.parse_args(["--resume-latest"]))

    assert session.id == "sess_new"


def test_main_no_longer_exposes_trim_messages():
    assert not hasattr(main, "trim_messages")


def test_model_message_to_json_preserves_reasoning_content():
    class Message:
        role = "assistant"
        content = "answer"
        reasoning_content = "thinking"
        tool_calls = None

    assert main.model_message_to_json(Message()) == {
        "role": "assistant",
        "content": "answer",
        "reasoning_content": "thinking",
    }


def test_format_assistant_display_includes_reasoning_and_content_with_tool_calls():
    class Message:
        content = "I will read the file first."
        reasoning_content = "Need inspect before answering."
        tool_calls = [object()]

    assert main.format_assistant_display(Message()) == (
        "\033[2mAgent reasoning:\n"
        "Need inspect before answering.\033[0m\n"
        "Agent:\n"
        "I will read the file first."
    )


def test_format_assistant_display_handles_reasoning_without_content():
    class Message:
        content = None
        reasoning_content = "Need a tool call."
        tool_calls = [object()]

    assert main.format_assistant_display(Message()) == (
        "\033[2mAgent reasoning:\n"
        "Need a tool call.\033[0m"
    )


def test_format_assistant_display_has_no_separator():
    class Message:
        content = "answer"
        reasoning_content = None

    assert "------------------------" not in main.format_assistant_display(Message())


def test_log_format_omits_timestamp_and_prints_message_only():
    assert "{time}" not in main.LOG_FORMAT
    assert main.LOG_FORMAT == "<level>{message}</level>"


def test_format_status_logs_use_compact_style():
    assert main.format_session_log("sess_123") == "• session sess_123"
    assert main.format_loaded_tools_log(3) == "• loaded 3 MCP tools"
    assert main.format_request_log() == "› requesting model"
    assert main.format_usage_log(3032, 69) == "✓ done · tokens in 3032 / out 69"
    assert main.format_tool_log("read", "path='README.md'") == (
        "→ tool read(path='README.md')"
    )


def test_format_tool_approval_prompt_includes_tool_and_arguments():
    assert main.format_tool_approval_prompt("read", {"path": "README.md"}) == (
        "Approve tool call read(path='README.md')? [Y/n]: "
    )


def test_format_tool_approval_prompt_truncates_apply_patch_content():
    patch = "a" * 120

    prompt = main.format_tool_approval_prompt("apply_patch", {"patch": patch})

    assert prompt == (
        "Approve tool call apply_patch("
        f"patch='{patch[:100]} ...'"
        ")? [Y/n]: "
    )
    assert patch not in prompt


def test_approve_tool_call_accepts_enter_y_and_yes():
    prompts = []

    def approving_input(prompt):
        prompts.append(prompt)
        return "yes"

    assert main.approve_tool_call("read", {"path": "README.md"}, approving_input)
    assert prompts == ["Approve tool call read(path='README.md')? [Y/n]: "]

    assert main.approve_tool_call("read", {}, lambda _: "")
    assert main.approve_tool_call("read", {}, lambda _: "y")
    assert main.approve_tool_call("read", {}, lambda _: "YES")


def test_approve_tool_call_rejects_n_and_no():
    assert not main.approve_tool_call("read", {}, lambda _: "n")
    assert not main.approve_tool_call("read", {}, lambda _: "no")


def test_execute_tool_with_approval_rejects_without_running_tool(monkeypatch):
    def fail_execute(*args, **kwargs):
        raise AssertionError("tool should not run")

    monkeypatch.setattr(main, "execute_tool", fail_execute)

    result = main.execute_tool_with_approval(
        "read",
        {"path": "README.md"},
        input_fn=lambda _: "n",
    )

    assert result == "Tool call rejected by user"


def test_execute_tool_with_approval_runs_when_approved(monkeypatch):
    calls = []

    def fake_execute(name, args, mcp_registry=None):
        calls.append((name, args, mcp_registry))
        return "tool result"

    monkeypatch.setattr(main, "execute_tool", fake_execute)

    result = main.execute_tool_with_approval(
        "read",
        {"path": "README.md"},
        mcp_registry="registry",
        input_fn=lambda _: "y",
    )

    assert result == "tool result"
    assert calls == [("read", {"path": "README.md"}, "registry")]


def test_prompt_user_uses_plain_input_to_avoid_prompt_toolkit_sigint_stack():
    calls = []

    def fake_input(prompt):
        calls.append(prompt)
        return "hello"

    assert main.prompt_user(input_fn=fake_input) == "hello"
    assert calls == ["You: "]


def chunk(delta, finish_reason=None, usage=None):
    return SimpleNamespace(
        choices=[
            SimpleNamespace(
                delta=delta,
                finish_reason=finish_reason,
            )
        ],
        usage=usage,
    )


def test_stream_completion_to_response_streams_text_and_builds_message():
    writes = []
    stream = [
        chunk(SimpleNamespace(role="assistant", content="Hel")),
        chunk(SimpleNamespace(content="lo")),
        chunk(SimpleNamespace(), finish_reason="stop"),
    ]

    resp = main.stream_completion_to_response(stream, write_text=writes.append)

    assert writes == ["Hel", "lo"]
    assert resp.choices[0].message.role == "assistant"
    assert resp.choices[0].message.content == "Hello"
    assert resp.choices[0].message.tool_calls is None
    assert resp.choices[0].finish_reason == "stop"


def test_stream_completion_to_response_reconstructs_tool_call_deltas():
    stream = [
        chunk(
            SimpleNamespace(
                tool_calls=[
                    SimpleNamespace(
                        index=0,
                        id="call_1",
                        type="function",
                        function=SimpleNamespace(name="read", arguments='{"pa'),
                    )
                ]
            )
        ),
        chunk(
            SimpleNamespace(
                tool_calls=[
                    SimpleNamespace(
                        index=0,
                        function=SimpleNamespace(arguments='th":"README.md"}'),
                    )
                ]
            ),
            finish_reason="tool_calls",
        ),
    ]

    resp = main.stream_completion_to_response(stream)
    tool_call = resp.choices[0].message.tool_calls[0]

    assert tool_call.id == "call_1"
    assert tool_call.type == "function"
    assert tool_call.function.name == "read"
    assert tool_call.function.arguments == '{"path":"README.md"}'
    assert resp.choices[0].finish_reason == "tool_calls"
