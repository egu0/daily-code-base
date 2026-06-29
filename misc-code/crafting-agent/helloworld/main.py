from dotenv import load_dotenv
import os
from openai import OpenAI
from loguru import logger
import json
import sys
from types import SimpleNamespace
from prompt_toolkit import prompt as pt_prompt
from .tools import tools_map, tool_schemas
from .config import agent_workdir
from .skills import SKILLS_DIR, discover_skills, format_skill_index
from .mcp_runtime import MCPToolRegistry, load_default_mcp_registry
from .tool_search import (
    MCPToolSearchRouter,
    BRIDGE_TOOL_NAMES,
    format_mcp_tool_index,
    tool_search_schemas,
)
from .session_store import (
    ChatSession,
    create_session,
    latest_session,
    load_session,
    record_request,
    record_response,
    save_session,
)
from datetime import datetime, timezone
import argparse
from .tools.background_process.manager import close_process_manager

LOG_FORMAT = "<level>{message}</level>"
ANSI_DIM = "\033[2m"
ANSI_RESET = "\033[0m"

logger.remove(0)
logger.add(sys.stdout, colorize=True, format=LOG_FORMAT)

load_dotenv()

models_context_ = {"deepseek-v4-flash": 1000_000, "deepseek-v4-pro": 1000_000}

client = OpenAI(base_url="https://api.deepseek.com/", api_key=os.getenv("DEEPSEEK_KEY"))
MODEL = "deepseek-v4-pro"


def initial_messages(mcp_registry: MCPToolRegistry | None = None):
    skill_index = format_skill_index(discover_skills(SKILLS_DIR))
    content = f"You are a helpful assitant."
    mcp_tool_index = format_mcp_tool_index(mcp_registry)
    if mcp_tool_index:
        content = f"{content}\n\n{mcp_tool_index}"
    if skill_index:
        content = f"{content}\n\n{skill_index}"
    return [
        {
            "role": "system",
            "content": content,
        }
    ]


messages = initial_messages()


def parse_args(argv=None):
    parser = argparse.ArgumentParser(description="Run the helloworld CLI agent.")
    parser.add_argument(
        "--workdir",
        help="Working directory for tool calls. Defaults to HELLO_AGENT_WORKDIR or the current directory.",
    )
    parser.add_argument(
        "--session",
        help="Resume an existing session id. Creates it if it does not exist.",
    )
    parser.add_argument(
        "--resume-latest",
        action="store_true",
        help="Resume the most recently updated session.",
    )
    parser.add_argument(
        "--list-sessions",
        action="store_true",
        help="List saved sessions and exit.",
    )
    parser.add_argument(
        "--list-skills",
        action="store_true",
        help="List enabled skills and exit.",
    )
    return parser.parse_args(argv)


def start_session(args, mcp_registry: MCPToolRegistry | None = None) -> ChatSession:
    if args.list_skills:
        for index, skill in enumerate(discover_skills(SKILLS_DIR)):
            if index:
                print()
            print(f"name: {skill.name}")
            print(f"description: {skill.description}")
            print(f"path: {skill.path}")
        raise SystemExit(0)

    if args.list_sessions:
        from .session_store import list_sessions

        for item in list_sessions():
            print(
                f"{item.id}\t"
                f"{format_session_updated_at(item.updated_at)}\t"
                f"{len(item.messages)} messages"
            )
        raise SystemExit(0)

    if args.resume_latest:
        session = latest_session()
        if session:
            return session
        return create_session(initial_messages(mcp_registry))

    if args.session:
        session = load_session(args.session)
        if session:
            return session
        return create_session(initial_messages(mcp_registry), session_id=args.session)

    return create_session(initial_messages(mcp_registry))


def restore_messages(session: ChatSession, mcp_registry: MCPToolRegistry | None = None):
    messages[:] = session.messages or initial_messages(mcp_registry)


def persist_messages(session: ChatSession):
    session.messages = list(messages)
    save_session(session)


def combined_tool_schemas(mcp_registry: MCPToolRegistry | None):
    if not mcp_registry or not mcp_registry.tool_schemas:
        return tool_schemas
    return tool_schemas + tool_search_schemas


def execute_tool(name, args, mcp_registry: MCPToolRegistry | None = None):
    if name in tools_map:
        return tools_map[name](**args)
    if mcp_registry and name in BRIDGE_TOOL_NAMES:
        return MCPToolSearchRouter(mcp_registry).execute(name, args)
    if mcp_registry and mcp_registry.has_tool(name):
        return mcp_registry.call_tool(name, args)
    raise KeyError(f"unknown tool: {name}")


def format_session_log(session_id):
    return f"• session {session_id}"


def format_loaded_tools_log(count):
    return f"• loaded {count} MCP tools"


def local_timezone():
    return datetime.now().astimezone().tzinfo


def format_session_updated_at(updated_at):
    try:
        parsed = datetime.fromisoformat(updated_at)
    except ValueError:
        return updated_at
    if parsed.tzinfo is None:
        parsed = parsed.replace(tzinfo=timezone.utc)
    local_time = parsed.astimezone(local_timezone())
    tz_name = local_time.tzname() or local_time.strftime("%z")
    return f"{local_time:%Y-%m-%d %H:%M:%S} {tz_name}".rstrip()


def format_request_log():
    return "› requesting model"


def format_usage_log(prompt_tokens, completion_tokens):
    return f"✓ done · tokens in {prompt_tokens} / out {completion_tokens}"


def format_tool_log(name, displayed_args):
    return f"→ tool {name}({displayed_args})"


def format_tool_args(args, tool_name=None):
    displayed = []
    for key, value in args.items():
        if tool_name == "apply_patch" and key == "patch" and isinstance(value, str):
            value = value[:100] + " ..." if len(value) > 100 else value
        displayed.append(f"{key}={value!r}")
    return ", ".join(displayed)


def format_tool_approval_prompt(name, args):
    return f"Approve tool call {name}({format_tool_args(args, name)})? [Y/n]: "


def approve_tool_call(name, args, input_fn=None):
    answer = (
        (input_fn or (lambda p: pt_prompt(p)))(format_tool_approval_prompt(name, args))
        .strip()
        .lower()
    )
    return answer not in {"n", "no"}


def execute_tool_with_approval(
    name,
    args,
    mcp_registry: MCPToolRegistry | None = None,
    input_fn=None,
):
    if not approve_tool_call(name, args, input_fn):
        return "Tool call rejected by user"
    return execute_tool(name, args, mcp_registry)


def message_reasoning(msg):
    return getattr(msg, "reasoning_content", None) or getattr(msg, "reasoning", None)


def dim_text(text):
    return f"{ANSI_DIM}{text}{ANSI_RESET}"


def format_assistant_display(msg):
    parts = []
    reasoning = message_reasoning(msg)
    content = getattr(msg, "content", None)

    if reasoning:
        parts.append(dim_text(f"Agent reasoning:\n{reasoning}"))
    if content:
        parts.extend(["Agent:", content])
    return "\n".join(parts)


def model_message_to_json(msg):
    payload = {
        "role": getattr(msg, "role", "assistant"),
        "content": getattr(msg, "content", None),
    }

    reasoning = message_reasoning(msg)
    if reasoning:
        payload["reasoning_content"] = reasoning

    tool_calls = getattr(msg, "tool_calls", None)
    if tool_calls:
        payload["tool_calls"] = [
            {
                "id": tc.id,
                "type": tc.type,
                "function": {
                    "name": tc.function.name,
                    "arguments": tc.function.arguments,
                },
            }
            for tc in tool_calls
        ]

    return payload


def usage_to_json(usage):
    if not usage:
        return None
    if hasattr(usage, "model_dump"):
        return usage.model_dump()
    return {
        name: getattr(usage, name)
        for name in ("prompt_tokens", "completion_tokens", "total_tokens")
        if hasattr(usage, name)
    }


def completion_response_to_json(resp):
    choice = resp.choices[0]
    return {
        "message": model_message_to_json(choice.message),
        "finish_reason": getattr(choice, "finish_reason", None),
        "usage": usage_to_json(getattr(resp, "usage", None)),
    }


def value_from(obj, name, default=None):
    if isinstance(obj, dict):
        return obj.get(name, default)
    return getattr(obj, name, default)


def stream_completion_to_response(stream, write_text=None):
    role = "assistant"
    content_parts = []
    reasoning_parts = []
    tool_call_parts = {}
    finish_reason = None
    usage = None

    for chunk in stream:
        usage = value_from(chunk, "usage", usage)
        choices = value_from(chunk, "choices", []) or []
        if not choices:
            continue

        choice = choices[0]
        finish_reason = value_from(choice, "finish_reason", finish_reason)
        delta = value_from(choice, "delta")
        if not delta:
            continue

        role = value_from(delta, "role", role) or role
        content = value_from(delta, "content")
        if content:
            content_parts.append(content)
            if write_text:
                write_text(content)

        reasoning = value_from(delta, "reasoning_content") or value_from(
            delta, "reasoning"
        )
        if reasoning:
            reasoning_parts.append(reasoning)

        for tool_call in value_from(delta, "tool_calls", []) or []:
            index = value_from(tool_call, "index")
            if index is None:
                index = len(tool_call_parts)
            part = tool_call_parts.setdefault(
                index,
                {
                    "id": None,
                    "type": "function",
                    "name": "",
                    "arguments": "",
                },
            )
            part["id"] = value_from(tool_call, "id", part["id"]) or part["id"]
            part["type"] = value_from(tool_call, "type", part["type"]) or part["type"]

            function = value_from(tool_call, "function")
            if function:
                name = value_from(function, "name")
                arguments = value_from(function, "arguments")
                if name:
                    part["name"] += name
                if arguments:
                    part["arguments"] += arguments

    tool_calls = None
    if tool_call_parts:
        tool_calls = [
            SimpleNamespace(
                id=part["id"],
                type=part["type"],
                function=SimpleNamespace(
                    name=part["name"],
                    arguments=part["arguments"],
                ),
            )
            for _, part in sorted(tool_call_parts.items())
        ]

    message = SimpleNamespace(
        role=role,
        content="".join(content_parts) or None,
        reasoning_content="".join(reasoning_parts) or None,
        tool_calls=tool_calls,
    )
    choice = SimpleNamespace(message=message, finish_reason=finish_reason)
    return SimpleNamespace(choices=[choice], usage=usage)


def prompt_user(input_fn=None) -> str:
    if input_fn is None:
        try:
            return pt_prompt("You: ").strip()
        except KeyboardInterrupt:
            print("\nGoodbye!")
            # os._exit avoids "Task exception was never retrieved"
            # warnings from prompt_toolkit's asyncio event loop
            # when combined with nest_asyncio.
            os._exit(0)
    return input_fn("You: ").strip()


def run(argv=None):
    args = parse_args(argv)
    os.chdir(agent_workdir(args.workdir))
    if args.list_sessions or args.list_skills:
        start_session(args)
        return

    mcp_registry = load_default_mcp_registry()
    if mcp_registry.tool_schemas:
        logger.info(format_loaded_tools_log(len(mcp_registry.tool_schemas)))

    chat_session = start_session(args, mcp_registry)
    restore_messages(chat_session, mcp_registry)
    logger.info(format_session_log(chat_session.id))

    try:
        while True:
            user_input = prompt_user()
            if len("".join(user_input.split())) == 0:
                continue
            messages.append({"role": "user", "content": user_input})
            persist_messages(chat_session)

            while True:
                # 请求 LLM
                logger.debug(format_request_log())
                request_payload = {
                    "model": MODEL,
                    "messages": list(messages),
                    "tools": combined_tool_schemas(mcp_registry),
                    "stream": True,
                }
                request_log_path = record_request(chat_session, request_payload)
                content_streamed = False

                def write_text(text):
                    nonlocal content_streamed
                    if not content_streamed:
                        print("Agent:")
                        content_streamed = True
                    print(text, end="", flush=True)

                resp = stream_completion_to_response(
                    client.chat.completions.create(
                        **request_payload,
                    ),
                    write_text=write_text,
                )
                if content_streamed:
                    print()
                record_response(request_log_path, completion_response_to_json(resp))
                if resp.usage:
                    logger.debug(
                        format_usage_log(
                            resp.usage.prompt_tokens,
                            resp.usage.completion_tokens,
                        )
                    )
                msg = resp.choices[0].message
                display = "" if content_streamed else format_assistant_display(msg)
                if display:
                    print(display)

                # 没有更多调用时说明任务结束
                if not msg.tool_calls:
                    messages.append({"role": "assistant", "content": msg.content})
                    persist_messages(chat_session)
                    break

                # 将 LLM 返回信息拼接起来（context + 要使用的 tools）
                messages.append(
                    {
                        "role": "assistant",
                        "content": msg.content or "",
                        "tool_calls": [
                            {
                                "id": tc.id,
                                "type": "function",
                                "function": {
                                    "name": tc.function.name,
                                    "arguments": tc.function.arguments,
                                },
                            }
                            for tc in msg.tool_calls  # 列表推导式子
                        ],
                    }
                )
                persist_messages(chat_session)

                for tc in msg.tool_calls:
                    name = tc.function.name
                    args = json.loads(tc.function.arguments)

                    s_args = format_tool_args(args, name)
                    displayed_args = (
                        s_args if len(s_args) < 50 else s_args[:50] + " ..."
                    )
                    logger.debug(format_tool_log(name, displayed_args))

                    result = execute_tool_with_approval(name, args, mcp_registry)

                    messages.append(
                        {"role": "tool", "tool_call_id": tc.id, "content": str(result)}
                    )
                    persist_messages(chat_session)
    finally:
        mcp_registry.close()
        close_process_manager()


if __name__ == "__main__":
    try:
        run()
    except KeyboardInterrupt:
        print("\nGoodbye!")
