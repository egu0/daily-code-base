import sys
import json
import pytest
from datetime import timezone, timedelta
from pathlib import Path
from types import SimpleNamespace
from prompt_toolkit.formatted_text import ANSI

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld import main
from helloworld import skills
from helloworld.session_store import SESSION_DIR_ENV, create_session, save_session
from helloworld.tools import tool_schemas, tools_map


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


def test_run_list_sessions_does_not_load_mcp(monkeypatch, tmp_path):
    monkeypatch.setenv(SESSION_DIR_ENV, str(tmp_path / "sessions"))
    monkeypatch.setattr(main, "agent_workdir", lambda path=None: tmp_path)
    monkeypatch.setattr(main, "close_process_manager", lambda: None)

    def fail_load_mcp():
        raise AssertionError("MCP should not load for --list-sessions")

    monkeypatch.setattr(main, "load_default_mcp_registry", fail_load_mcp)

    with pytest.raises(SystemExit) as exc:
        main.run(["--list-sessions"])

    assert exc.value.code == 0


def test_list_sessions_prints_newest_first_with_readable_local_time(
    monkeypatch, tmp_path, capsys
):
    monkeypatch.setenv(SESSION_DIR_ENV, str(tmp_path))
    old = create_session([], session_dir=tmp_path, session_id="sess_old")
    new = create_session(
        [{"role": "user", "content": "hello"}],
        session_dir=tmp_path,
        session_id="sess_new",
    )
    updated_times = {
        old.id: "2026-06-29T01:00:00+00:00",
        new.id: "2026-06-29T02:11:50.272327+00:00",
    }
    save_session(old, session_dir=tmp_path)
    save_session(new, session_dir=tmp_path)
    for session in (old, new):
        session_file = tmp_path / session.id / "session.json"
        payload = json.loads(session_file.read_text(encoding="utf-8"))
        payload["updated_at"] = updated_times[session.id]
        session_file.write_text(json.dumps(payload), encoding="utf-8")
    local_tz = timezone(timedelta(hours=10), "AEST")
    monkeypatch.setattr(main, "local_timezone", lambda: local_tz)

    with pytest.raises(SystemExit) as exc:
        main.start_session(main.parse_args(["--list-sessions"]))

    assert exc.value.code == 0
    assert capsys.readouterr().out == (
        "sess_new\t2026-06-29 12:11:50 AEST\t1 messages\n"
        "sess_old\t2026-06-29 11:00:00 AEST\t0 messages\n"
    )


def test_list_skills_prints_multiline_skill_blocks(monkeypatch, tmp_path, capsys):
    skills_root = tmp_path / "skills"
    first_dir = skills_root / "frontend-design"
    second_dir = skills_root / "weather"
    first_dir.mkdir(parents=True)
    second_dir.mkdir()
    (first_dir / "SKILL.md").write_text(
        "---\n"
        "name: frontend-design\n"
        "description: Guidance for distinctive UI design.\n"
        "---\n",
        encoding="utf-8",
    )
    (second_dir / "SKILL.md").write_text(
        "---\n" "name: weather\n" "description: Weather planning guidance.\n" "---\n",
        encoding="utf-8",
    )
    monkeypatch.setattr(main, "resolve_skills_dir", lambda workdir=None: skills_root)

    with pytest.raises(SystemExit) as exc:
        main.start_session(main.parse_args(["--list-skills"]))

    assert exc.value.code == 0
    assert capsys.readouterr().out == (
        "name: frontend-design\n"
        "description: Guidance for distinctive UI design.\n"
        f"path: {first_dir / 'SKILL.md'}\n"
        "\n"
        "name: weather\n"
        "description: Weather planning guidance.\n"
        f"path: {second_dir / 'SKILL.md'}\n"
    )


def test_main_no_longer_exposes_trim_messages():
    assert not hasattr(main, "trim_messages")


def test_skills_dir_defaults_under_user_hello_agent():
    assert skills.SKILLS_DIR == Path.home() / ".hello-agent" / "skills"


def test_run_changes_to_agent_workdir(monkeypatch, tmp_path):
    workdir = tmp_path / "project"
    workdir.mkdir()
    observed = {}

    monkeypatch.setattr(
        main,
        "load_default_mcp_registry",
        lambda: SimpleNamespace(tool_schemas=[], close=lambda: None),
    )
    monkeypatch.setattr(
        main,
        "start_session",
        lambda args, mcp_registry=None, workdir=None: SimpleNamespace(
            id="sess_test", messages=[]
        ),
    )
    monkeypatch.setattr(
        main,
        "restore_messages",
        lambda session, mcp_registry=None, workdir=None: None,
    )
    monkeypatch.setattr(
        main,
        "prompt_user",
        lambda **kwargs: (_ for _ in ()).throw(RuntimeError("stop loop")),
    )
    monkeypatch.setattr(main, "close_process_manager", lambda: None)
    monkeypatch.setattr(main, "agent_workdir", lambda path=None: workdir)

    def capture_log(session_id):
        observed["cwd"] = Path.cwd()
        return f"session {session_id}"

    monkeypatch.setattr(main, "format_session_log", capture_log)

    with pytest.raises(RuntimeError, match="stop loop"):
        main.run([])

    assert observed["cwd"] == workdir


def test_run_logs_startup_summary_in_order(monkeypatch, tmp_path):
    logs = []
    skills_dir = tmp_path / "skills"
    mcp_config_path = tmp_path / "mcp_config.json"

    monkeypatch.setattr(
        main,
        "load_default_mcp_registry",
        lambda: SimpleNamespace(tool_schemas=[{}, {}, {}], close=lambda: None),
    )
    monkeypatch.setattr(main, "resolve_skills_dir", lambda workdir=None: skills_dir)
    monkeypatch.setattr(
        main, "resolve_mcp_config_path", lambda workdir=None: mcp_config_path
    )
    monkeypatch.setattr(
        main,
        "discover_skills",
        lambda path: [
            SimpleNamespace(name="alpha", description="Alpha skill", path=path / "alpha"),
            SimpleNamespace(name="beta", description="Beta skill", path=path / "beta"),
        ],
    )
    monkeypatch.setattr(
        main,
        "start_session",
        lambda args, mcp_registry=None, workdir=None: SimpleNamespace(
            id="sess_test", messages=[]
        ),
    )
    monkeypatch.setattr(
        main,
        "restore_messages",
        lambda session, mcp_registry=None, workdir=None: None,
    )
    monkeypatch.setattr(
        main,
        "prompt_user",
        lambda **kwargs: (_ for _ in ()).throw(RuntimeError("stop loop")),
    )
    monkeypatch.setattr(main, "close_process_manager", lambda: None)
    monkeypatch.setattr(main, "agent_workdir", lambda path=None: tmp_path)
    monkeypatch.setattr(main.logger, "info", logs.append)

    with pytest.raises(RuntimeError, match="stop loop"):
        main.run([])

    assert logs[:4] == [
        "• session sess_test",
        f"• loaded 3 MCP tools · config {mcp_config_path}",
        f"• loaded 2 skills · dir {skills_dir}",
        f"• workdir {tmp_path}",
    ]


def test_run_prints_newline_between_streamed_reasoning_and_content(
    monkeypatch, tmp_path, capsys
):
    prompts = iter(["hi"])
    stream = [
        chunk(SimpleNamespace(role="assistant", reasoning_content="No tools needed.")),
        chunk(SimpleNamespace(content="Hello!"), finish_reason="stop"),
    ]

    monkeypatch.setattr(
        main,
        "load_default_mcp_registry",
        lambda: SimpleNamespace(tool_schemas=[], close=lambda: None),
    )
    monkeypatch.setattr(
        main,
        "start_session",
        lambda args, mcp_registry=None, workdir=None: SimpleNamespace(
            id="sess_test", messages=[]
        ),
    )
    monkeypatch.setattr(
        main,
        "restore_messages",
        lambda session, mcp_registry=None, workdir=None: None,
    )
    monkeypatch.setattr(main, "persist_messages", lambda session: None)
    monkeypatch.setattr(
        main, "record_request", lambda session, payload: tmp_path / "1.json"
    )
    monkeypatch.setattr(main, "record_response", lambda path, response: None)
    monkeypatch.setattr(main, "close_process_manager", lambda: None)
    monkeypatch.setattr(main, "agent_workdir", lambda path=None: tmp_path)
    monkeypatch.setattr(
        main,
        "client",
        SimpleNamespace(
            chat=SimpleNamespace(
                completions=SimpleNamespace(create=lambda **kwargs: stream)
            )
        ),
    )

    def prompt_once(**kwargs):
        try:
            return next(prompts)
        except StopIteration:
            raise RuntimeError("stop loop")

    monkeypatch.setattr(main, "prompt_user", prompt_once)

    with pytest.raises(RuntimeError, match="stop loop"):
        main.run([])

    output = capsys.readouterr().out
    assert "No tools needed.\033[0m\nAgent:" in output
    assert "No tools needed.\033[0mAgent:" not in output


def test_run_prints_tool_result_after_tool_execution(monkeypatch, tmp_path, capsys):
    prompts = iter(["read it"])
    streams = iter(
        [
            [
                chunk(
                    SimpleNamespace(
                        role="assistant",
                        tool_calls=[
                            SimpleNamespace(
                                index=0,
                                id="call_1",
                                type="function",
                                function=SimpleNamespace(
                                    name="read", arguments='{"path":"README.md"}'
                                ),
                            )
                        ],
                    ),
                    finish_reason="tool_calls",
                )
            ],
            [
                chunk(
                    SimpleNamespace(role="assistant", content="Done."),
                    finish_reason="stop",
                )
            ],
        ]
    )

    monkeypatch.setattr(
        main,
        "load_default_mcp_registry",
        lambda: SimpleNamespace(tool_schemas=[], close=lambda: None),
    )
    monkeypatch.setattr(
        main,
        "start_session",
        lambda args, mcp_registry=None, workdir=None: SimpleNamespace(
            id="sess_test", messages=[]
        ),
    )
    monkeypatch.setattr(
        main,
        "restore_messages",
        lambda session, mcp_registry=None, workdir=None: None,
    )
    monkeypatch.setattr(main, "persist_messages", lambda session: None)
    monkeypatch.setattr(
        main, "record_request", lambda session, payload: tmp_path / "1.json"
    )
    monkeypatch.setattr(main, "record_response", lambda path, response: None)
    monkeypatch.setattr(main, "close_process_manager", lambda: None)
    monkeypatch.setattr(main, "agent_workdir", lambda path=None: tmp_path)
    monkeypatch.setattr(main, "execute_tool_with_approval", lambda *args: "README text")
    monkeypatch.setattr(
        main,
        "client",
        SimpleNamespace(
            chat=SimpleNamespace(
                completions=SimpleNamespace(create=lambda **kwargs: next(streams))
            )
        ),
    )

    def prompt_once(**kwargs):
        try:
            return next(prompts)
        except StopIteration:
            raise RuntimeError("stop loop")

    monkeypatch.setattr(main, "prompt_user", prompt_once)

    with pytest.raises(RuntimeError, match="stop loop"):
        main.run([])

    output = capsys.readouterr().out
    assert "[tool] read result:" in output
    assert "README text" in output


def test_cli_reports_runtime_errors_without_traceback(monkeypatch, capsys):
    monkeypatch.setattr(
        main,
        "run",
        lambda argv=None: (_ for _ in ()).throw(RuntimeError("stream exploded")),
    )

    exit_code = main.cli([])

    captured = capsys.readouterr()
    assert exit_code == 1
    assert captured.out == "Error: stream exploded\n"
    assert "Traceback" not in captured.out
    assert "Traceback" not in captured.err


def test_cli_keyboard_interrupt_prints_resume_hint(monkeypatch, capsys):
    monkeypatch.setattr(main, "active_session_id", "sess_cli")
    monkeypatch.setattr(
        main,
        "run",
        lambda argv=None: (_ for _ in ()).throw(KeyboardInterrupt()),
    )

    exit_code = main.cli([])

    assert exit_code == 130
    assert capsys.readouterr().out == (
        "\nGoodbye!\n" "Resume chat: hgent --session sess_cli\n"
    )


def test_prompt_user_keyboard_interrupt_prints_resume_hint(monkeypatch, capsys):
    def interrupting_prompt(prompt):
        raise KeyboardInterrupt

    def stop_without_exiting(code):
        raise SystemExit(code)

    monkeypatch.setattr(main, "pt_prompt", interrupting_prompt)
    monkeypatch.setattr(main.os, "_exit", stop_without_exiting)

    with pytest.raises(SystemExit) as exc:
        main.prompt_user(session_id="sess_prompt")

    assert exc.value.code == 0
    assert capsys.readouterr().out == (
        "\nGoodbye!\n" "Resume chat: hgent --session sess_prompt\n"
    )


def test_run_passes_session_id_to_prompt_user(monkeypatch, tmp_path):
    observed = {}

    monkeypatch.setattr(
        main,
        "load_default_mcp_registry",
        lambda: SimpleNamespace(tool_schemas=[], close=lambda: None),
    )
    monkeypatch.setattr(
        main,
        "start_session",
        lambda args, mcp_registry=None, workdir=None: SimpleNamespace(
            id="sess_run", messages=[]
        ),
    )
    monkeypatch.setattr(
        main,
        "restore_messages",
        lambda session, mcp_registry=None, workdir=None: None,
    )
    monkeypatch.setattr(main, "close_process_manager", lambda: None)
    monkeypatch.setattr(main, "agent_workdir", lambda path=None: tmp_path)

    def capture_prompt(*, session_id=None):
        observed["session_id"] = session_id
        raise RuntimeError("stop loop")

    monkeypatch.setattr(main, "prompt_user", capture_prompt)

    with pytest.raises(RuntimeError, match="stop loop"):
        main.run([])

    assert observed["session_id"] == "sess_run"


def test_discover_skills_reads_name_and_description_from_skill_markdown(tmp_path):
    skill_dir = tmp_path / "skills" / "frontend-design"
    skill_dir.mkdir(parents=True)
    skill_file = skill_dir / "SKILL.md"
    skill_file.write_text(
        "---\n"
        "name: frontend-design\n"
        "description: Guidance for distinctive UI design.\n"
        "---\n"
        "# Frontend Design\n",
        encoding="utf-8",
    )

    discovered = skills.discover_skills(tmp_path / "skills")

    assert discovered == [
        skills.Skill(
            name="frontend-design",
            description="Guidance for distinctive UI design.",
            path=skill_file.resolve(),
        )
    ]


def test_initial_messages_includes_skill_index_and_read_skill_instruction(
    monkeypatch, tmp_path
):
    skill_dir = tmp_path / "skills" / "playwright-skill"
    skill_dir.mkdir(parents=True)
    skill_file = skill_dir / "SKILL.md"
    skill_file.write_text(
        "---\n"
        "name: playwright-skill\n"
        "description: Complete browser automation with Playwright.\n"
        "---\n"
        "# Playwright\n",
        encoding="utf-8",
    )
    monkeypatch.setattr(
        main, "resolve_skills_dir", lambda workdir=None: tmp_path / "skills"
    )

    system_message = main.initial_messages()[0]["content"]

    assert "Available skills:" in system_message
    assert "name: playwright-skill" in system_message
    assert "description: Complete browser automation with Playwright." in system_message
    assert "Use the read_skill tool" in system_message


def test_initial_messages_uses_project_skills_dir(monkeypatch, tmp_path):
    workdir = tmp_path / "project"
    skill_dir = workdir / ".hello-agent" / "skills" / "project-skill"
    skill_dir.mkdir(parents=True)
    skill_file = skill_dir / "SKILL.md"
    skill_file.write_text(
        "---\n"
        "name: project-skill\n"
        "description: Project-local instructions.\n"
        "---\n"
        "# Project Skill\n",
        encoding="utf-8",
    )
    monkeypatch.delenv("HELLO_AGENT_SKILLS_DIR", raising=False)
    monkeypatch.setenv("HELLO_AGENT_HOME", str(tmp_path / "global-agent"))

    system_message = main.initial_messages(workdir=workdir)[0]["content"]

    assert "name: project-skill" in system_message
    assert f"path: {skill_file.resolve()}" in system_message


def test_initial_messages_includes_agent_workdir(monkeypatch, tmp_path):
    workdir = tmp_path / "project"
    workdir.mkdir()
    monkeypatch.setattr(main, "agent_workdir", lambda path=None: workdir)

    system_message = main.initial_messages()[0]["content"]

    assert "Current project directory:" in system_message
    assert str(workdir) in system_message
    assert (
        "If a file path is relative, resolve it from this directory" in system_message
    )


def test_start_session_creates_system_message_with_workdir(monkeypatch, tmp_path):
    workdir = tmp_path / "project"
    workdir.mkdir()
    monkeypatch.setattr(main, "agent_workdir", lambda path=None: workdir)
    monkeypatch.setattr(
        main,
        "create_session",
        lambda messages, session_id=None: SimpleNamespace(
            id=session_id or "sess_test",
            messages=messages,
        ),
    )

    session = main.start_session(
        SimpleNamespace(
            list_skills=False,
            list_sessions=False,
            resume_latest=False,
            session=None,
        ),
        mcp_registry=None,
        workdir=workdir,
    )

    assert str(workdir) in session.messages[0]["content"]


def test_initial_messages_omits_mcp_tool_section_without_mcp_tools(
    monkeypatch, tmp_path
):
    monkeypatch.setattr(
        main, "resolve_skills_dir", lambda workdir=None: tmp_path / "skills"
    )

    system_message = main.initial_messages()[0]["content"]

    assert "You have access to MCP tools." not in system_message


def test_initial_messages_includes_mcp_tool_names_and_descriptions(
    monkeypatch, tmp_path
):
    monkeypatch.setattr(
        main, "resolve_skills_dir", lambda workdir=None: tmp_path / "skills"
    )
    mcp_registry = SimpleNamespace(
        tool_schemas=[
            {
                "type": "function",
                "function": {
                    "name": "fake__echo",
                    "description": "Echo input back to the user",
                    "parameters": {
                        "type": "object",
                        "properties": {"text": {"type": "string"}},
                        "required": ["text"],
                    },
                },
            }
        ]
    )

    system_message = main.initial_messages(mcp_registry)[0]["content"]

    assert "You have access to MCP tools." in system_message
    assert (
        "An MCP tool is an external capability provided by a connected server. "
        "MCP tools are listed by name and description only, and are not "
        "directly callable as function tools."
    ) in system_message
    assert (
        "If the user request matches an MCP tool description, use "
        "tool_describe with the MCP tool name to read its full argument schema "
        "before calling it. Then use tool_call with the MCP tool name and "
        "arguments. Use tool_search only when the listed MCP tools are not "
        "enough to choose the right tool."
    ) in system_message
    assert "Available MCP tools:" in system_message
    assert "- name: fake__echo" in system_message
    assert "  description: Echo input back to the user" in system_message
    assert "call tool_search to find relevant tools" not in system_message
    assert '"properties"' not in system_message
    assert '"required"' not in system_message


def test_read_skill_returns_full_skill_markdown_by_name(tmp_path):
    skill_dir = tmp_path / "skills" / "playwright-skill"
    skill_dir.mkdir(parents=True)
    skill_file = skill_dir / "SKILL.md"
    content = (
        "---\n"
        "name: playwright-skill\n"
        "description: Complete browser automation with Playwright.\n"
        "---\n"
        "# Playwright\n"
        "Follow these instructions.\n"
    )
    skill_file.write_text(content, encoding="utf-8")

    result = skills.read_skill("playwright-skill", tmp_path / "skills")

    assert result == {
        "name": "playwright-skill",
        "path": str(skill_file.resolve()),
        "content": content,
    }


def test_read_skill_tool_is_registered():
    assert "read_skill" in tools_map
    assert any(schema["function"]["name"] == "read_skill" for schema in tool_schemas)


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
        "\033[2mAgent reasoning:\n" "Need a tool call.\033[0m"
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
    assert main.format_loaded_tools_log(3, "/tmp/mcp.json") == (
        "• loaded 3 MCP tools · config /tmp/mcp.json"
    )
    assert main.format_loaded_skills_log(2, "/tmp/skills") == (
        "• loaded 2 skills · dir /tmp/skills"
    )
    assert main.format_workdir_log("/tmp/project") == "• workdir /tmp/project"
    assert main.format_request_log() == "› requesting model"
    assert main.format_usage_log(3032, 69) == "✓ done · tokens in 3032 / out 69"
    assert main.format_tool_log("read", "path='README.md'") == (
        "→ tool read(path='README.md')"
    )


def test_format_tool_args_shows_full_apply_patch_content():
    patch = "a" * 120

    displayed_args = main.format_tool_args({"patch": patch}, "apply_patch")

    assert displayed_args == f"patch='{patch}'"
    assert "..." not in displayed_args


def test_format_displayed_tool_args_does_not_truncate_apply_patch_content():
    patch = "a" * 120

    displayed_args = main.format_displayed_tool_args({"patch": patch}, "apply_patch")

    assert displayed_args == f"patch='{patch}'"
    assert "..." not in displayed_args


def test_format_displayed_tool_args_truncates_other_long_tool_args():
    displayed_args = main.format_displayed_tool_args({"query": "a" * 120}, "search")

    assert displayed_args == "query='" + ("a" * 43) + " ..."


def test_format_tool_approval_prompt_includes_tool_and_arguments():
    assert main.format_tool_approval_prompt("read", {"path": "README.md"}) == (
        f"{main.ANSI_APPROVAL}Approve tool call read:{main.ANSI_RESET}\n"
        f"  {main.ANSI_DIM}path{main.ANSI_RESET}: README.md\n"
        f"{main.ANSI_APPROVAL}Proceed? [Y/n/i]: {main.ANSI_RESET}"
    )


def test_format_tool_approval_prompt_formats_command_parameters_readably():
    prompt = main.format_tool_approval_prompt(
        "bash",
        {
            "command": "pytest tests/test_main_session.py -q",
            "cwd": "/tmp/project",
            "timeout": 30,
        },
    )

    assert prompt == (
        f"{main.ANSI_APPROVAL}Approve tool call bash:{main.ANSI_RESET}\n"
        f"  {main.ANSI_DIM}command{main.ANSI_RESET}: pytest tests/test_main_session.py -q\n"
        f"  {main.ANSI_DIM}cwd{main.ANSI_RESET}: /tmp/project\n"
        f"  {main.ANSI_DIM}timeout{main.ANSI_RESET}: 30\n"
        f"{main.ANSI_APPROVAL}Proceed? [Y/n/i]: {main.ANSI_RESET}"
    )


def test_format_tool_approval_prompt_pretty_prints_structured_parameters():
    prompt = main.format_tool_approval_prompt(
        "remote__search",
        {"query": {"text": "agent frameworks", "limit": 3}},
    )

    assert prompt == (
        f"{main.ANSI_APPROVAL}Approve tool call remote__search:{main.ANSI_RESET}\n"
        f"  {main.ANSI_DIM}query{main.ANSI_RESET}:\n"
        "    {\n"
        '      "limit": 3,\n'
        '      "text": "agent frameworks"\n'
        "    }\n"
        f"{main.ANSI_APPROVAL}Proceed? [Y/n/i]: {main.ANSI_RESET}"
    )


def test_format_tool_approval_prompt_shows_full_apply_patch_content():
    patch = "a" * 120

    prompt = main.format_tool_approval_prompt("apply_patch", {"patch": patch})

    assert prompt == (
        f"{main.ANSI_APPROVAL}Approve tool call apply_patch:{main.ANSI_RESET}\n"
        f"  {main.ANSI_DIM}patch{main.ANSI_RESET}: {patch}\n"
        f"{main.ANSI_APPROVAL}Proceed? [Y/n/i]: {main.ANSI_RESET}"
    )
    assert "..." not in prompt


def test_approve_tool_call_accepts_enter_y_and_yes():
    prompts = []

    def approving_input(prompt):
        prompts.append(prompt)
        return "yes"

    assert main.approve_tool_call("read", {"path": "README.md"}, approving_input)
    assert prompts == [
        f"{main.ANSI_APPROVAL}Approve tool call read:{main.ANSI_RESET}\n"
        f"  {main.ANSI_DIM}path{main.ANSI_RESET}: README.md\n"
        f"{main.ANSI_APPROVAL}Proceed? [Y/n/i]: {main.ANSI_RESET}"
    ]

    assert main.approve_tool_call("read", {}, lambda _: "")
    assert main.approve_tool_call("read", {}, lambda _: "y")
    assert main.approve_tool_call("read", {}, lambda _: "YES")


def test_approve_tool_call_prints_details_and_prompts_with_short_ansi_question(
    monkeypatch,
):
    prompts = []
    printed = []
    patch = "*** Begin Patch\n" + ("x" * 200) + "\n*** End Patch"

    def approving_prompt(prompt):
        prompts.append(prompt)
        return "yes"

    def capture_print(text):
        printed.append(text)

    monkeypatch.setattr(main, "pt_prompt", approving_prompt)
    monkeypatch.setattr(main, "print_formatted_text", capture_print)

    assert main.approve_tool_call("apply_patch", {"patch": patch})
    assert isinstance(prompts[0], ANSI)
    assert len(str(prompts[0])) < 80

    output = str(printed[0])
    assert "*** Begin Patch" in output
    assert "x" * 200 in output
    assert "*** End Patch" in output


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


def test_execute_tool_with_approval_rejects_with_instruction_without_running_tool(
    monkeypatch,
):
    prompts = []
    answers = iter(["i", "Use docs/api.md instead."])

    def fail_execute(*args, **kwargs):
        raise AssertionError("tool should not run")

    def scripted_input(prompt):
        prompts.append(prompt)
        return next(answers)

    monkeypatch.setattr(main, "execute_tool", fail_execute)

    result = main.execute_tool_with_approval(
        "read",
        {"path": "README.md"},
        input_fn=scripted_input,
    )

    assert result == (
        "Tool call rejected by user. Instruction: Use docs/api.md instead."
    )
    assert prompts == [
        f"{main.ANSI_APPROVAL}Approve tool call read:{main.ANSI_RESET}\n"
        f"  {main.ANSI_DIM}path{main.ANSI_RESET}: README.md\n"
        f"{main.ANSI_APPROVAL}Proceed? [Y/n/i]: {main.ANSI_RESET}",
        "Instruction for agent: ",
    ]


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


def test_stream_completion_to_response_streams_reasoning_and_builds_message():
    content_writes = []
    reasoning_writes = []
    stream = [
        chunk(SimpleNamespace(role="assistant", reasoning_content="Think ")),
        chunk(SimpleNamespace(reasoning_content="first.")),
        chunk(SimpleNamespace(content="Answer."), finish_reason="stop"),
    ]

    resp = main.stream_completion_to_response(
        stream,
        write_text=content_writes.append,
        write_reasoning=reasoning_writes.append,
    )

    assert reasoning_writes == ["Think ", "first."]
    assert content_writes == ["Answer."]
    assert resp.choices[0].message.reasoning_content == "Think first."
    assert resp.choices[0].message.content == "Answer."


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
