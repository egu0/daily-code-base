import json
import os
import re
import sys
import uuid
from dataclasses import dataclass, field
from datetime import datetime, timezone
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

SYSTEM_PROMPT = f"You are a helpful agent. Use tools when needed. If needed, current time is ${datetime.now()}"
DATA_DIR = Path(__file__).resolve().parent / "data"
SESSION_ID_PATTERN = re.compile(r"^[A-Za-z0-9_-]+$")

client = OpenAI(
    base_url="https://api.deepseek.com/",
    api_key=os.getenv("DEEPSEEK_KEY"),
)


@dataclass
class ToolApproval:
    tool_call_id: str
    name: str
    args: dict
    event: Event = field(default_factory=Event)
    approved: bool | None = None


@dataclass
class SessionState:
    id: str
    messages: list[dict] = field(
        default_factory=lambda: [{"role": "system", "content": SYSTEM_PROMPT}]
    )
    cancel_event: Event = field(default_factory=Event)
    lock: Lock = field(default_factory=Lock)
    pending_tool_approvals: dict[str, ToolApproval] = field(default_factory=dict)
    prompt_tokens: int | None = None
    enabled_tools: list[str] | None = None
    created_at: str = field(
        default_factory=lambda: datetime.now(timezone.utc).isoformat()
    )
    updated_at: str = field(
        default_factory=lambda: datetime.now(timezone.utc).isoformat()
    )


sessions: dict[str, SessionState] = {}
sessions_lock = Lock()


def validate_session_id(session_id: str) -> bool:
    return bool(session_id and SESSION_ID_PATTERN.fullmatch(session_id))


def session_dir(session_id: str) -> Path:
    return DATA_DIR / session_id


def session_file(session_id: str) -> Path:
    return session_dir(session_id) / "session.json"


def session_to_json(session: SessionState) -> dict:
    return {
        "id": session.id,
        "messages": session.messages,
        "prompt_tokens": session.prompt_tokens,
        "enabled_tools": session.enabled_tools,
        "created_at": session.created_at,
        "updated_at": session.updated_at,
    }


def save_session(session: SessionState) -> None:
    session.updated_at = datetime.now(timezone.utc).isoformat()
    directory = session_dir(session.id)
    directory.mkdir(parents=True, exist_ok=True)
    path = session_file(session.id)
    tmp_path = path.with_suffix(".json.tmp")
    tmp_path.write_text(
        json.dumps(session_to_json(session), ensure_ascii=False, indent=2),
        encoding="utf-8",
    )
    tmp_path.replace(path)


def load_session(session_id: str) -> SessionState | None:
    if not validate_session_id(session_id):
        return None

    path = session_file(session_id)
    if not path.exists():
        return None

    payload = json.loads(path.read_text(encoding="utf-8"))
    session = SessionState(
        id=payload["id"],
        messages=payload.get("messages") or [],
        prompt_tokens=payload.get("prompt_tokens"),
        enabled_tools=payload.get("enabled_tools"),
        created_at=payload.get("created_at") or datetime.now(timezone.utc).isoformat(),
        updated_at=payload.get("updated_at") or datetime.now(timezone.utc).isoformat(),
    )
    if not session.messages:
        session.messages.append({"role": "system", "content": SYSTEM_PROMPT})
    return session


def create_session() -> SessionState:
    session = SessionState(id=f"sess_{uuid.uuid4().hex}")
    save_session(session)
    with sessions_lock:
        sessions[session.id] = session
    return session


def get_session(session_id: str) -> SessionState | None:
    if not validate_session_id(session_id):
        return None

    with sessions_lock:
        session = sessions.get(session_id)
        if session:
            return session

    session = load_session(session_id)
    if not session:
        return None

    with sessions_lock:
        sessions[session.id] = session
    return session


def cancel_session(session_id: str) -> bool:
    session = get_session(session_id)
    if not session:
        return False
    session.cancel_event.set()
    with session.lock:
        pending = list(session.pending_tool_approvals.values())
    for approval in pending:
        approval.event.set()
    return True


def create_tool_approval(
    session: SessionState,
    tool_call_id: str,
    name: str,
    args: dict,
) -> ToolApproval:
    approval = ToolApproval(tool_call_id=tool_call_id, name=name, args=args)
    with session.lock:
        session.pending_tool_approvals[tool_call_id] = approval
    return approval


def resolve_tool_approval(
    session: SessionState,
    tool_call_id: str,
    approved: bool,
) -> bool:
    with session.lock:
        approval = session.pending_tool_approvals.get(tool_call_id)
        if not approval:
            return False
        approval.approved = approved
        approval.event.set()
    return True


def approve_tool_call(session_id: str, tool_call_id: str, approved: bool) -> bool:
    session = get_session(session_id)
    if not session:
        return False
    return resolve_tool_approval(session, tool_call_id, approved)


def wait_for_tool_approval(
    session: SessionState,
    tool_call_id: str,
) -> bool | None:
    with session.lock:
        approval = session.pending_tool_approvals.get(tool_call_id)
    if not approval:
        return None

    while not session.cancel_event.is_set():
        if approval.event.wait(0.1):
            break

    with session.lock:
        session.pending_tool_approvals.pop(tool_call_id, None)

    if session.cancel_event.is_set() and approval.approved is None:
        return None
    return approval.approved


def update_session_tools(session_id: str, enabled_tools: list[str]) -> bool:
    session = get_session(session_id)
    if not session:
        return False
    with session.lock:
        session.enabled_tools = enabled_tools
        save_session(session)
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


def session_payload(session: SessionState) -> dict:
    enabled = session.enabled_tools
    if enabled is None:
        enabled = [s["function"]["name"] for s in tool_schemas]
    with session.lock:
        pending_tool_approvals = [
            {
                "toolCallId": approval.tool_call_id,
                "name": approval.name,
                "args": approval.args,
            }
            for approval in session.pending_tool_approvals.values()
        ]
    return {
        "sessionId": session.id,
        "messages": session.messages,
        "usage": usage_event(session)["data"],
        "enabledTools": enabled,
        "pendingToolApprovals": pending_tool_approvals,
        "createdAt": session.created_at,
        "updatedAt": session.updated_at,
    }


def update_prompt_tokens(session: SessionState, usage) -> bool:
    prompt_tokens = getattr(usage, "prompt_tokens", None)
    if prompt_tokens is None:
        return False
    with session.lock:
        session.prompt_tokens = prompt_tokens
        save_session(session)
    return True


def extract_reasoning_text(delta) -> str | None:
    for field_name in ("reasoning", "reasoning_content"):
        value = getattr(delta, field_name, None)
        if value:
            return value

    if isinstance(delta, dict):
        return delta.get("reasoning") or delta.get("reasoning_content")

    model_extra = getattr(delta, "model_extra", None) or {}
    return model_extra.get("reasoning") or model_extra.get("reasoning_content")


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
        save_session(session)
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
        reasoning_content = []
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

                reasoning = extract_reasoning_text(delta)
                if reasoning:
                    reasoning_content.append(reasoning)
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
        if reasoning_content:
            assistant_message["reasoning_content"] = "".join(reasoning_content)
        if tool_calls:
            assistant_message["tool_calls"] = tool_calls

        with session.lock:
            session.messages.append(assistant_message)
            save_session(session)
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
            result = ""
            status = "failed"
            try:
                args = json.loads(raw_args)
            except json.JSONDecodeError as exc:
                args = {}
                result = f"Error: invalid tool arguments JSON: {exc}"
                status = "failed"
            else:
                create_tool_approval(session, call_id, name, args)
                yield event(
                    "tool_call",
                    {
                        "toolCallId": call_id,
                        "name": name,
                        "args": args,
                        "status": "pending_approval",
                    },
                )
                approved = wait_for_tool_approval(session, call_id)
                if approved is None:
                    yield event("turn_done", {"stopReason": "cancelled"})
                    return
                if not approved:
                    result = "Tool call denied by user"
                    status = "denied"
                else:
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
                save_session(session)
                yield usage_event(session)
