from dotenv import load_dotenv
import os
from openai import OpenAI
from loguru import logger
import json
import sys
from .tools import tools_map, tool_schemas
from prompt_toolkit import PromptSession

logger.remove(0)
logger.add(
    sys.stdout, colorize=True, format="<green>{time}</green> <level>{message}</level>"
)

load_dotenv()

client = OpenAI(
    base_url="https://openrouter.ai/api/v1", api_key=os.getenv("OPENROUTER_KEY")
)
MODEL = "deepseek/deepseek-v4-flash"

messages = [
    {
        "role": "system",
        "content": "You are a helpful agent. Use tools when needed.",
    }
]


def run():
    while True:
        session = PromptSession()
        user_input = session.prompt("You: ").strip()
        if len("".join(user_input.split())) == 0:
            continue
        messages.append({"role": "user", "content": user_input})

        while True:
            # 请求 LLM
            logger.debug("👀 开始请求大语言模型...")
            resp = client.chat.completions.create(
                model=MODEL,
                messages=messages,
                tools=tool_schemas,
            )
            cost = getattr(resp.usage, "cost", 0.0)
            logger.debug(
                f"💰 请求结束。调用数据: in {resp.usage.prompt_tokens}, out {resp.usage.completion_tokens}, cost ${cost:.6f}"
            )
            msg = resp.choices[0].message

            # 没有更多调用时说明任务结束
            if not msg.tool_calls:
                print(f"\nAgent:\n{msg.content}\n\n------------------------")
                messages.append({"role": "assistant", "content": msg.content})
                break

            # 将 LLM 返回信息拼接起来（context + 要使用的 tools）
            messages.append(
                {
                    "role": "assistant",
                    "content": msg.content or "",
                    "tool_calls": [
                        # 所有要用到的工具信息
                        {
                            "id": tc.id,
                            "type": "function",
                            "function": {
                                "name": tc.function.name,
                                "arguments": tc.function.arguments,
                            },
                        }
                        for tc in msg.tool_calls
                    ],
                }
            )

            # 本地工具调用
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


if __name__ == "__main__":
    try:
        run()
    except KeyboardInterrupt:
        print("\nGoodbye!")
