import json
import os
import sys
import uuid
from dataclasses import dataclass, field
from pathlib import Path
from threading import Event, Lock

from dotenv import load_dotenv
from openai import OpenAI

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from .main import MODEL, models_context_  # noqa: E402
from .tools import tool_schemas, tools_map  # noqa: E402

load_dotenv(ROOT_DIR / ".env")

SYSTEM_PROMPT = "You are a helpful agent. Use tools when needed."

client = OpenAI(
    base_url="https://api.deepseek.com/",
    api_key=os.getenv("DEEPSEEK_KEY"),
)


@dataclass
class SessionState:
    id: str
    messages: list[dict] = field(
        default_factory=lambda: [{"role": "system", "content": SYSTEM_PROMPT}]
    )
    cancel_event: Event = field(default_factory=Event)
    lock: Lock = field(default_factory=Lock)
    prompt_tokens: int | None = None


sessions: dict[str, SessionState] = {}
sessions_lock = Lock()


def create_session() -> SessionState:
    session = SessionState(id=f"sess_{uuid.uuid4().hex}")
    with sessions_lock:
        sessions[session.id] = session
    return session


def get_session(session_id: str) -> SessionState | None:
    with sessions_lock:
        return sessions.get(session_id)


def cancel_session(session_id: str) -> bool:
    session = get_session(session_id)
    if not session:
        return False
    session.cancel_event.set()
    return True


def available_tools() -> list[dict]:
    tools = []
    for schema in tool_schemas:
        function = schema["function"]
        tools.append(
            {
                "name": function["name"],
                "description": function.get("description", ""),
                "schema": schema,
            }
        )
    return tools


def select_tool_schemas(enabled_tools: list[str]) -> list[dict]:
    enabled = set(enabled_tools)
    return [schema for schema in tool_schemas if schema["function"]["name"] in enabled]


def event(event_type: str, data: dict) -> dict:
    return {"event": event_type, "data": data}


def model_context_size() -> int | None:
    return models_context_.get(MODEL)


def usage_event(session: SessionState) -> dict:
    return event(
        "usage_update",
        {
            "model": MODEL,
            "used": session.prompt_tokens,
            "size": model_context_size(),
            "messageCount": len(session.messages),
        },
    )


def update_prompt_tokens(session: SessionState, usage) -> bool:
    prompt_tokens = getattr(usage, "prompt_tokens", None)
    if prompt_tokens is None:
        return False
    with session.lock:
        session.prompt_tokens = prompt_tokens
    return True


def compact_tool_calls(tool_calls) -> list[dict]:
    return [
        {
            "id": tc.id,
            "type": "function",
            "function": {
                "name": tc.function.name,
                "arguments": tc.function.arguments,
            },
        }
        for tc in tool_calls
    ]


def stream_prompt(
    session: SessionState,
    prompt: str,
    enabled_tools: list[str],
):
    session.cancel_event.clear()
    with session.lock:
        session.messages.append({"role": "user", "content": prompt})
        yield usage_event(session)

    yield event("turn_started", {"sessionId": session.id})

    while True:
        if session.cancel_event.is_set():
            yield event("turn_done", {"stopReason": "cancelled"})
            return

        selected_schemas = select_tool_schemas(enabled_tools)
        response = client.chat.completions.create(
            model=MODEL,
            messages=session.messages,
            tools=selected_schemas or None,
            stream=True,
            stream_options={"include_usage": True},
        )

        assistant_content = []
        tool_call_chunks: dict[int, dict] = {}
        message_id = f"msg_{uuid.uuid4().hex}"

        try:
            for chunk in response:
                if session.cancel_event.is_set():
                    yield event("turn_done", {"stopReason": "cancelled"})
                    return

                usage = getattr(chunk, "usage", None)
                if usage and update_prompt_tokens(session, usage):
                    yield usage_event(session)

                if not chunk.choices:
                    continue

                delta = chunk.choices[0].delta
                content = getattr(delta, "content", None)
                if content:
                    assistant_content.append(content)
                    yield event(
                        "agent_message_chunk",
                        {"messageId": message_id, "text": content},
                    )

                reasoning = getattr(delta, "reasoning", None)
                if reasoning:
                    yield event(
                        "reasoning_chunk",
                        {"messageId": f"reasoning_{message_id}", "text": reasoning},
                    )

                for tc in getattr(delta, "tool_calls", None) or []:
                    index = tc.index
                    current = tool_call_chunks.setdefault(
                        index,
                        {
                            "id": "",
                            "type": "function",
                            "function": {"name": "", "arguments": ""},
                        },
                    )
                    if tc.id:
                        current["id"] = tc.id
                    if tc.type:
                        current["type"] = tc.type
                    if tc.function:
                        if tc.function.name:
                            current["function"]["name"] += tc.function.name
                        if tc.function.arguments:
                            current["function"]["arguments"] += tc.function.arguments
        except Exception as exc:
            yield event("error", {"message": str(exc)})
            return

        tool_calls = [tool_call_chunks[i] for i in sorted(tool_call_chunks)]
        for tc in tool_calls:
            if not tc["id"]:
                tc["id"] = f"call_{uuid.uuid4().hex}"

        assistant_message = {
            "role": "assistant",
            "content": "".join(assistant_content) or "",
        }
        if tool_calls:
            assistant_message["tool_calls"] = tool_calls

        with session.lock:
            session.messages.append(assistant_message)
            yield usage_event(session)

        if not tool_calls:
            yield event("turn_done", {"stopReason": "end_turn"})
            return

        for tc in tool_calls:
            if session.cancel_event.is_set():
                yield event("turn_done", {"stopReason": "cancelled"})
                return

            call_id = tc["id"]
            name = tc["function"]["name"]
            raw_args = tc["function"]["arguments"] or "{}"
            try:
                args = json.loads(raw_args)
            except json.JSONDecodeError as exc:
                args = {}
                result = f"Error: invalid tool arguments JSON: {exc}"
                status = "failed"
            else:
                yield event(
                    "tool_call",
                    {
                        "toolCallId": call_id,
                        "name": name,
                        "args": args,
                        "status": "pending",
                    },
                )
                yield event(
                    "tool_call_update",
                    {"toolCallId": call_id, "status": "in_progress"},
                )
                try:
                    result = tools_map[name](**args)
                    status = "completed"
                except Exception as exc:
                    result = f"Error: {exc}"
                    status = "failed"

            yield event(
                "tool_call_update",
                {
                    "toolCallId": call_id,
                    "status": status,
                    "result": str(result),
                },
            )

            with session.lock:
                session.messages.append(
                    {
                        "role": "tool",
                        "tool_call_id": call_id,
                        "content": str(result),
                    }
                )
                yield usage_event(session)
