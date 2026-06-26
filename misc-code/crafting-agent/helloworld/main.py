from dotenv import load_dotenv
import os
from openai import OpenAI
from loguru import logger
import json
import sys
from .tools import tools_map, tool_schemas
from .mcp_runtime import MCPToolRegistry, load_default_mcp_registry
from .session_store import (
    ChatSession,
    create_session,
    latest_session,
    load_session,
    record_request,
    record_response,
    save_session,
)
from datetime import datetime
import argparse

LOG_FORMAT = "<level>{message}</level>"
ANSI_DIM = "\033[2m"
ANSI_RESET = "\033[0m"

logger.remove(0)
logger.add(sys.stdout, colorize=True, format=LOG_FORMAT)

load_dotenv()

models_context_ = {"deepseek-v4-flash": 1000_000, "deepseek-v4-pro": 1000_000}

client = OpenAI(base_url="https://api.deepseek.com/", api_key=os.getenv("DEEPSEEK_KEY"))
MODEL = "deepseek-v4-flash"


def initial_messages():
    return [
        {
            "role": "system",
            "content": f"You are a helpful agent. Use tools when needed. If needed, current time is {datetime.now()}",
        }
    ]


messages = initial_messages()


def parse_args(argv=None):
    parser = argparse.ArgumentParser(description="Run the helloworld CLI agent.")
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
    return parser.parse_args(argv)


def start_session(args) -> ChatSession:
    if args.list_sessions:
        from .session_store import list_sessions

        for item in list_sessions():
            print(f"{item.id}\t{item.updated_at}\t{len(item.messages)} messages")
        raise SystemExit(0)

    if args.resume_latest:
        session = latest_session()
        if session:
            return session
        return create_session(initial_messages())

    if args.session:
        session = load_session(args.session)
        if session:
            return session
        return create_session(initial_messages(), session_id=args.session)

    return create_session(initial_messages())


def restore_messages(session: ChatSession):
    messages[:] = session.messages or initial_messages()


def persist_messages(session: ChatSession):
    session.messages = list(messages)
    save_session(session)


def combined_tool_schemas(mcp_registry: MCPToolRegistry | None):
    if not mcp_registry:
        return tool_schemas
    return tool_schemas + mcp_registry.tool_schemas


def execute_tool(name, args, mcp_registry: MCPToolRegistry | None = None):
    if name in tools_map:
        return tools_map[name](**args)
    if mcp_registry and mcp_registry.has_tool(name):
        return mcp_registry.call_tool(name, args)
    raise KeyError(f"unknown tool: {name}")


def format_session_log(session_id):
    return f"• session {session_id}"


def format_loaded_tools_log(count):
    return f"• loaded {count} MCP tools"


def format_request_log():
    return "› requesting model"


def format_usage_log(prompt_tokens, completion_tokens):
    return f"✓ done · tokens in {prompt_tokens} / out {completion_tokens}"


def format_tool_log(name, displayed_args):
    return f"→ tool {name}({displayed_args})"


def format_tool_args(args):
    return ", ".join(f"{key}={value!r}" for key, value in args.items())


def format_tool_approval_prompt(name, args):
    return f"Approve tool call {name}({format_tool_args(args)})? [Y/n]: "


def approve_tool_call(name, args, input_fn=input):
    answer = input_fn(format_tool_approval_prompt(name, args)).strip().lower()
    return answer not in {"n", "no"}


def execute_tool_with_approval(
    name,
    args,
    mcp_registry: MCPToolRegistry | None = None,
    input_fn=input,
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


def prompt_user(input_fn=input) -> str:
    return input_fn("You: ").strip()


def run(argv=None):
    args = parse_args(argv)
    chat_session = start_session(args)
    restore_messages(chat_session)
    logger.info(format_session_log(chat_session.id))

    mcp_registry = load_default_mcp_registry()
    if mcp_registry.tool_schemas:
        logger.info(format_loaded_tools_log(len(mcp_registry.tool_schemas)))

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
                }
                request_log_path = record_request(chat_session, request_payload)
                resp = client.chat.completions.create(
                    **request_payload,
                )
                record_response(request_log_path, completion_response_to_json(resp))
                logger.debug(
                    format_usage_log(
                        resp.usage.prompt_tokens,
                        resp.usage.completion_tokens,
                    )
                )
                msg = resp.choices[0].message
                display = format_assistant_display(msg)
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

                    s_args = format_tool_args(args)
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


if __name__ == "__main__":
    try:
        run()
    except KeyboardInterrupt:
        print("\nGoodbye!")
