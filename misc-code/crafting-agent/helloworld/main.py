from dotenv import load_dotenv
import os
from openai import OpenAI
from loguru import logger
import json
import sys
from .tools import tools_map, tool_schemas
from prompt_toolkit import PromptSession
from datetime import datetime

logger.remove(0)
logger.add(
    sys.stdout, colorize=True, format="<green>{time}</green> <level>{message}</level>"
)

load_dotenv()

models_context_ = {"deepseek-v4-flash": 1000_000, "deepseek-v4-pro": 1000_000}

client = OpenAI(base_url="https://api.deepseek.com/", api_key=os.getenv("DEEPSEEK_KEY"))
MODEL = "deepseek-v4-flash"
MAX_CONTEXT_MESSAGES = 20

messages = [
    {
        "role": "system",
        "content": f"You are a helpful agent. Use tools when needed. If needed, current time is ${datetime.now()}",
    }
]


def trim_messages():
    if len(messages) <= MAX_CONTEXT_MESSAGES:
        return

    system_messages = [msg for msg in messages if msg.get("role") == "system"]
    non_system_messages = [msg for msg in messages if msg.get("role") != "system"]
    keep_count = MAX_CONTEXT_MESSAGES - len(system_messages)
    recent_messages = non_system_messages[-keep_count:]

    # 把开头的 tool 类型消息去掉，避免上下文第一条变成孤立的 tool 消息
    while recent_messages and recent_messages[0].get("role") == "tool":
        recent_messages.pop(0)

    messages[:] = system_messages + recent_messages


def run():
    while True:
        session = PromptSession()
        user_input = session.prompt("You: ").strip()
        if len("".join(user_input.split())) == 0:
            continue
        messages.append({"role": "user", "content": user_input})
        trim_messages()

        while True:
            # 请求 LLM
            logger.debug("👀 Ruquesting..")
            resp = client.chat.completions.create(
                model=MODEL,
                messages=messages,
                tools=tool_schemas,
            )
            logger.debug(
                f"💰 Done. Costed tokens: in {resp.usage.prompt_tokens}, out {resp.usage.completion_tokens}"
            )
            msg = resp.choices[0].message

            # 没有更多调用时说明任务结束
            if not msg.tool_calls:
                print(f"Agent:\n{msg.content}\n------------------------")
                messages.append({"role": "assistant", "content": msg.content})
                trim_messages()
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

            for tc in msg.tool_calls:
                name = tc.function.name
                args = json.loads(tc.function.arguments)

                s_args = ", ".join(f"{k}={v!r}" for k, v in args.items())
                displayed_args = s_args if len(s_args) < 50 else s_args[:50] + " ..."
                logger.debug(f"🛠️ Exec: {name}({displayed_args})")

                result = tools_map[name](**args)

                messages.append(
                    {"role": "tool", "tool_call_id": tc.id, "content": str(result)}
                )
            trim_messages()


if __name__ == "__main__":
    try:
        run()
    except KeyboardInterrupt:
        print("\nGoodbye!")
