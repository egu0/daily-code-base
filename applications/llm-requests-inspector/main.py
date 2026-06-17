import json
from enum import Enum
import sys
import traceback
import functools
import os
from datetime import datetime
from urllib.parse import urlencode

import httpx
from fastapi import FastAPI, Request
from starlette.responses import StreamingResponse
import functools


DATA_FILE_FORMAT = "%m-%d_%H-%M-%S-%f"
COMPLETIONS_ENDPOINT = "https://openrouter.ai/api/v1/chat/completions"
MESSAGES_ENDPOINT = "https://openrouter.ai/api/v1/messages"
RESPONSES_ENDPOINT = "https://api.poe.com/v1/responses"


app = FastAPI(title="LLM API Logger")


# 请求 API 类型
class RequestApiType(Enum):
    COMPLETIONS = 1
    MESSAGES = 2
    RESPONSES = 3
    NOTFOUND = 3


# region normal funcitons


def append_to_file(file_path, data):
    with open(file_path, "a", encoding="utf-8") as f:
        f.write(data)


def write_to_file(file_path, data):
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(data)


def load_dict_from_json_file(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        return json.load(f)


def load_from_file(file_path):
    with open(file_path, "r", encoding="utf-8") as f:
        return f.read()


def write_dict_to_file(file_path, data):
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)


def log(file, message):
    append_to_file(file, message + "\n")


def exception_handler_continue(func):
    @functools.wraps(func)
    def wrapper(*args, **kwargs):
        try:
            return func(*args, **kwargs)
        except Exception as e:
            print(f"函数 {func.__name__} 运行出错: {e}")
            traceback.print_exc(limit=3, file=sys.stdout)
            return None

    return wrapper


def get_req_id() -> str:
    """获取日志文件路径"""
    now = datetime.now()
    return now.strftime(DATA_FILE_FORMAT)


def get_log_file_path(req_id: str) -> str:
    """获取日志文件路径"""
    os.makedirs("logs", exist_ok=True)
    return "logs/" + req_id + ".log"


def get_data_dir(req_id: str) -> str:
    """获取请求数据保存路径"""
    request_dir = os.path.join("data", req_id)
    os.makedirs("data", exist_ok=True)
    os.makedirs(request_dir, exist_ok=True)
    return request_dir


# endregion

# region list API


def extract_resp_responses_api(chunks: list[dict]) -> tuple[str, str, list]:
    """从 /responses 的响应内容中提取数据"""
    full_reasoning = ""
    full_content = ""
    full_tool_calling = []

    for item in chunks:
        item_type = item.get("type", "")
        if item_type == "response.completed":
            response = item.get("response")
            if response and isinstance(response, dict):
                outputArr = response.get("output")
                if outputArr and isinstance(outputArr, list):
                    for output_item in outputArr:
                        output_item_type = output_item.get("type")
                        if not output_item_type:
                            continue
                        if output_item_type == "reasoning":
                            pass
                        elif output_item_type == "message":
                            content = output_item.get("content")
                            if (
                                content
                                and isinstance(content, list)
                                and len(content) > 0
                            ):
                                message_cell = content[0]
                                if isinstance(message_cell, dict):
                                    message = message_cell.get("text")
                                    if message and isinstance(message, str):
                                        full_content += message
                        elif output_item_type == "function_call":
                            full_tool_calling.append(output_item)

    return full_reasoning, full_content, full_tool_calling


def extract_resp_messages_api(chunks: list[dict]) -> tuple[str, str, list]:
    """从 /messages 的响应内容中提取数据"""
    parsed_info = {}
    for item in chunks:
        item_type = item.get("type", "")
        if item_type == "content_block_start":
            index = item.get("index")  # non null index
            content_block = item.get("content_block", {})
            if content_block:
                type = content_block.get("type", "")
                # thinking, text, tool_use ...
                if type in ("thinking", "text"):
                    parsed_info[str(index)] = {
                        "type": type,
                        "data": "",
                    }
                else:
                    parsed_info[str(index)] = {
                        "type": type,
                        "data": [item],
                    }
        elif item_type == "content_block_delta":
            index = item.get("index")  # non null index
            indexed_item = parsed_info.get(str(index), {})
            if indexed_item:
                indexed_item_type = indexed_item.get("type", "")
                if indexed_item_type in ("text", "thinking"):
                    delta = item.get("delta", {})
                    if delta:
                        indexed_item["data"] += delta.get(indexed_item_type)
                else:
                    data = indexed_item.get("data", [])
                    if data:
                        data.append(item)
        elif item_type == "content_block_stop":
            index = item.get("index")  # non null index
            indexed_item = parsed_info.get(str(index), {})
            if indexed_item:
                type = indexed_item.get("type")
                data = indexed_item.get("data")
                if type not in ("thinking", "text"):
                    data.append(item)

    full_reasoning = ""
    full_content = ""
    full_tool_calling = []

    for info in parsed_info.values():
        type = info.get("type", "")
        data = info.get("data")
        if type == "text":
            if full_content:
                full_content += "\n\n---\n\n"
            full_content += data
        elif type == "thinking":
            if full_reasoning:
                full_reasoning += "\n\n---\n\n"
            full_reasoning += data
        else:
            full_tool_calling += data

    if full_reasoning:
        full_reasoning = "<thinking>" + full_reasoning + "</thinking>"
    return full_reasoning, full_content, full_tool_calling


def extract_resp_completions_api(chunks: list[dict]) -> tuple[str, str, list]:
    """从 /chat/completions 的响应内容中提取数据"""
    full_reasoning = ""
    full_content = ""
    full_tool_calling = []

    for item in chunks:
        choices = item.get("choices", [])
        if choices:
            delta = choices[0].get("delta", {})
            reasoning = delta.get("reasoning", "")
            content = delta.get("content", "")
            tool_calls = delta.get("tool_calls", [])
            if reasoning:
                full_reasoning += reasoning
            if content:
                full_content += content
            if tool_calls:
                full_tool_calling += tool_calls

    if full_reasoning:
        full_reasoning = "<thinking>" + full_reasoning + "</thinking>"
    return full_reasoning, full_content, full_tool_calling


@app.get("/api/requests")
async def list_requests():
    """获取所有请求数据。示例数据：
    ```
    [
      {
        "name": "xxxx",
        "model": "minimax/minimax-m2.7"
      },
      {
        "name": "yyyy",
        "model": "minimax/minimax-m2.7"
      }
    ]
    ```
    """

    requests_dir = "data"
    if not confirm_exists_of_path(requests_dir):
        return []
    dirs = sorted(os.listdir(requests_dir), reverse=False)
    result = []
    for d in dirs:
        req_path = os.path.join(requests_dir, d, "request.json")
        if confirm_exists_of_path(req_path):
            req_data = load_dict_from_json_file(req_path)
            result.append({"name": d, "model": req_data.get("model", "unknown")})
    return result


@exception_handler_continue
def load_request_api_type(req_id) -> RequestApiType:
    """获取请求 API 类型"""
    file_path = os.path.join("data", req_id, "meta.json")

    if not confirm_exists_of_path(file_path):
        return RequestApiType.NOTFOUND

    req_data = load_dict_from_json_file(file_path)
    if req_data:
        api_type = req_data.get("api_type")
        if api_type and isinstance(api_type, int):
            return RequestApiType(api_type)


@exception_handler_continue
def load_request_usage(req_id: str):
    """读取请求 usage 信息"""
    resp_path = os.path.join("data", req_id, "response.json")
    if confirm_exists_of_path(resp_path):
        resp_data = load_dict_from_json_file(resp_path)
        request_api_type = load_request_api_type(req_id)
        if RequestApiType.COMPLETIONS == request_api_type:
            for chunk in reversed(resp_data):
                if "usage" in chunk:
                    u = chunk["usage"]
                    return {
                        "prompt_tokens": u.get("prompt_tokens", 0),
                        "completion_tokens": u.get("completion_tokens", 0),
                        "cost": u.get("cost", 0),
                    }
        if RequestApiType.MESSAGES == request_api_type:
            for chunk in reversed(resp_data):
                chunk_type = chunk.get("type", "")
                if "message_delta" == chunk_type:
                    u = chunk["usage"]
                    return {
                        "prompt_tokens": u.get("input_tokens", 0),
                        "completion_tokens": u.get("output_tokens", 0),
                        "cost": u.get("cost", 0),
                    }
        if RequestApiType.RESPONSES == request_api_type:
            for chunk in reversed(resp_data):
                chunk_type = chunk.get("type", "")
                if "response.completed" == chunk_type:
                    response = chunk.get("response")
                    if response and isinstance(response, dict):
                        usage = response.get("usage")
                        if usage and isinstance(usage, dict):
                            return {
                                "prompt_tokens": usage.get("input_tokens", 0),
                                "completion_tokens": usage.get("output_tokens", 0),
                                "cost": 0,
                            }


def mode_from_type(type: str):
    if type in ("text", "thinking"):
        return "markdown"
    else:
        return "json"


def format_messages(req_id, req_data):
    api_type = load_request_api_type(req_id)
    if api_type == RequestApiType.MESSAGES or api_type == RequestApiType.COMPLETIONS:
        return req_data.get("messages"), req_data
    elif api_type == RequestApiType.RESPONSES:
        messages = []
        # instructions => system
        instructions = req_data.get("instructions")
        if instructions and isinstance(instructions, str):
            req_data["instructions"] = ""
            messages.append({"role": "system", "content": instructions})
        # tools, passed
        # input => messages
        input = req_data.get("input")
        if input and isinstance(input, list):
            for item in input:
                type = item.get("type")
                role = item.get("role")
                content = item.get("content")
                if type == "reasoning":
                    content = item.get("content")
                    if not content:
                        continue
                    elif isinstance(content, str):
                        messages.append({"role": "assistant", "content": content})
                elif type == "message":
                    # TODO 将两者分开
                    if role in ("user", "developer"):
                        if not content:
                            continue
                        elif isinstance(content, str):
                            messages.append({"role": role, "content": content})
                        elif isinstance(content, list):
                            for ctt_item in content:
                                ctt_item_type = ctt_item.get("type")
                                ctt_item_text = ctt_item.get("text")
                                if ctt_item_type == "input_text" and ctt_item_text:
                                    messages.append(
                                        {"role": role, "content": ctt_item_text}
                                    )
                else:
                    item["role"] = "tool"
                    messages.append(item)
            req_data["input"] = []
        return messages, req_data


@exception_handler_continue
def load_request_data(req_id: str):
    """加载请求数据。数据格式：
    ```
    [
        {
            "role": "system|user|assistant|tool|tools|meta",
            "content": [
                {
                    "mode": "markdown|json",
                    "data": "字符串或dict"
                }
            ]
        }
    ]
    ```
    """
    result_data = []
    req_data = None
    data_dir = os.path.join("data", req_id, "request.json")
    if not confirm_exists_of_path(data_dir):
        return result_data
    req_data = load_dict_from_json_file(data_dir)
    if req_data:
        messages, request_data = format_messages(req_id, req_data)
        if messages and isinstance(messages, list):
            system = request_data.get("system")
            tools = request_data.get("tools")
            if system and isinstance(system, list):
                messages = [{"role": "system", "content": system}] + messages
            if tools and isinstance(tools, list):
                messages = messages + [{"role": "tools", "content": tools}]
            if messages:
                for msg in messages:
                    role = msg.get("role")  # role ==> system|user|assistant|tool|tools
                    content = msg.get("content")
                    if not content:
                        result_data.append(
                            {"role": role, "content": [{"mode": "json", "data": msg}]}
                        )
                    elif isinstance(content, str):
                        if role == "system" and msg.keys() == {"role", "content"}:
                            result_data.append(
                                {
                                    "role": role,
                                    "content": [{"mode": "markdown", "data": content}],
                                }
                            )
                        else:
                            result_data.append(
                                {
                                    "role": role,
                                    "content": [{"mode": "json", "data": msg}],
                                }
                            )
                    elif isinstance(content, list):
                        final_data = []
                        for item in content:
                            type = item.get("type")
                            all_keys_cloned = set(item.keys())
                            all_keys_cloned.discard("cache_control")
                            all_keys_cloned.discard("citations")
                            all_keys_cloned.discard("signature")
                            # TODO 添加更多你看到的无关选项
                            if all_keys_cloned == {"type", "text"} and type in (
                                "text",
                                "thinking",
                            ):
                                final_data.append(
                                    {"mode": "markdown", "data": item.get("text")}
                                )
                            else:
                                final_data.append({"mode": "json", "data": item})
                        result_data.append({"role": role, "content": final_data})
        # 保留请求元数据
        for key in ("messages", "system", "tools"):
            if key in request_data.keys():
                request_data.update({key: []})
        result_data.append(
            {"role": "meta", "content": [{"mode": "json", "data": request_data}]}
        )
    return result_data


def confirm_exists_of_path(path: str) -> bool:
    return os.path.exists(path)


@exception_handler_continue
def load_request_model(req_id: str):
    data_dir = os.path.join("data", req_id, "request.json")
    if not confirm_exists_of_path(data_dir):
        return ""
    req_data = load_dict_from_json_file(data_dir)
    return req_data.get("model", "")


@exception_handler_continue
def load_response_messages(req_id: str):
    """加载响应数据。数据格式：
    ```
    [
        {
            "role": "thinking|output|tool-callings",
            "content": [
                {
                    "mode": "markdown|json",
                    "data": "字符串或dict"
                }
            ]
        }
    ]
    ```
    """
    response_files = {
        "thinking.md": {"role": "thinking", "mode": "markdown"},
        "output.md": {"role": "output", "mode": "markdown"},
        "tool-callings.json": {"role": "tool-callings", "mode": "json"},
    }
    response_messages = []
    for file in response_files.keys():
        file_path = os.path.join("data", req_id, file)
        if confirm_exists_of_path(file_path):
            file_content = load_from_file(file_path)
            response_messages.append(
                {
                    "role": response_files.get(file).get("role"),
                    "content": [
                        {
                            "mode": response_files.get(file).get("mode"),
                            "data": file_content,
                        }
                    ],
                }
            )
    return response_messages


@app.get("/api/requests/{req_id}")
async def get_request_detail(req_id: str):
    """获取某次 API 请求的详细数据，包括 usage、模型信息、请求数据、响应数据"""
    if not confirm_exists_of_path(os.path.join("data", req_id)):
        return {"error": "not found"}

    model = load_request_model(req_id)
    if model is None:
        model = ""
    usage = load_request_usage(req_id)
    if not usage:
        usage = {
            "prompt_tokens": 0,
            "completion_tokens": 0,
            "cost": 0,
        }
    messages = load_request_data(req_id)
    if not messages:
        messages = []
    resp_messages = load_response_messages(req_id)
    if not resp_messages:
        resp_messages = []

    return {
        "name": req_id,  # 请求ID
        "model": model,  # 所使用的模型
        "messages": messages,  # 请求详细信息
        "resp_messages": resp_messages,  # 响应数据
        "usage": usage,  # 模型调用usage数据
    }


# endregion

# region proxy APIs


# OpenAI Responses API
@app.post("/api/v1/responses")
async def responses(request: Request):
    req_id = get_req_id()
    log_file_path = get_log_file_path(req_id)

    headers = request.headers
    body_bytes = await request.body()
    body_str = body_bytes.decode("utf-8")

    log(log_file_path, f"请求完整地址：\n{str(request.url)}")
    log(log_file_path, f"请求头：\n{headers}")
    log(log_file_path, f"模型请求体：\n{body_str}")

    params = dict(request.query_params)
    message_url = f"{RESPONSES_ENDPOINT}?{urlencode(params)}"
    body = await request.json()

    data_dir = get_data_dir(req_id)
    write_dict_to_file(os.path.join(data_dir, "request.json"), body)
    write_dict_to_file(
        os.path.join(data_dir, "meta.json"),
        {"api_type": RequestApiType.RESPONSES.value},
    )

    log(log_file_path, "模型返回：\n")

    async def event_stream():
        resp_chunks = []
        async with httpx.AsyncClient(timeout=None) as client:
            async with client.stream(
                "POST",
                message_url,
                json=body,
                headers={
                    "Content-Type": "application/json",
                    "Authorization": request.headers.get("Authorization"),
                },
            ) as response:
                async for line in response.aiter_lines():
                    log(log_file_path, line)
                    yield f"{line}\n"
                    # 收集有效数据
                    if line.startswith("data: ") and not line.endswith("[DONE]"):
                        json_str = line[len("data: ") :]
                        try:
                            chunk_data = json.loads(json_str)
                            resp_chunks.append(chunk_data)
                        except json.JSONDecodeError:
                            pass
                    elif line.startswith("{") and line.endswith("}"):
                        try:
                            chunk_data = json.loads(line)
                            resp_chunks.append(chunk_data)
                        except json.JSONDecodeError:
                            pass

        write_dict_to_file(os.path.join(data_dir, "response.json"), resp_chunks)

        thinking, output, tool_calling = extract_resp_responses_api(resp_chunks)
        if thinking:
            write_to_file(os.path.join(data_dir, "thinking.md"), thinking)
        if output:
            write_to_file(os.path.join(data_dir, "output.md"), output)
        if tool_calling:
            write_dict_to_file(
                os.path.join(data_dir, "tool-callings.json"), tool_calling
            )

    return StreamingResponse(event_stream(), media_type="text/event-stream")


# Claude Code 使用这个 API
# https://platform.claude.com/docs/en/api/python/messages
@app.post("/api/v1/messages")
async def messages(request: Request):
    req_id = get_req_id()
    log_file_path = get_log_file_path(req_id)

    headers = request.headers
    body_bytes = await request.body()
    body_str = body_bytes.decode("utf-8")

    log(log_file_path, f"请求完整地址：\n{str(request.url)}")
    log(log_file_path, f"请求头：\n{headers}")
    log(log_file_path, f"模型请求体：\n{body_str}")

    params = dict(request.query_params)
    message_url = f"{MESSAGES_ENDPOINT}?{urlencode(params)}"
    body = await request.json()

    data_dir = get_data_dir(req_id)
    write_dict_to_file(os.path.join(data_dir, "request.json"), body)
    write_dict_to_file(
        os.path.join(data_dir, "meta.json"), {"api_type": RequestApiType.MESSAGES.value}
    )

    log(log_file_path, "模型返回：\n")

    async def event_stream():
        resp_chunks = []
        async with httpx.AsyncClient(timeout=None) as client:
            async with client.stream(
                "POST",
                message_url,
                json=body,
                headers={
                    "Content-Type": "application/json",
                    "Accept": "text/event-stream",
                    "Authorization": request.headers.get("Authorization"),
                },
            ) as response:
                async for line in response.aiter_lines():
                    log(log_file_path, line)
                    yield f"{line}\n"
                    # 收集有效数据
                    if line.startswith("data: ") and not line.endswith("[DONE]"):
                        json_str = line[len("data: ") :]
                        try:
                            chunk_data = json.loads(json_str)
                            resp_chunks.append(chunk_data)
                        except json.JSONDecodeError:
                            pass
                    elif line.startswith("{") and line.endswith("}"):
                        try:
                            chunk_data = json.loads(line)
                            resp_chunks.append(chunk_data)
                        except json.JSONDecodeError:
                            pass

        write_dict_to_file(os.path.join(data_dir, "response.json"), resp_chunks)

        thinking, output, tool_calling = extract_resp_messages_api(resp_chunks)
        if thinking:
            write_to_file(os.path.join(data_dir, "thinking.md"), thinking)
        if output:
            write_to_file(os.path.join(data_dir, "output.md"), output)
        if tool_calling:
            write_dict_to_file(
                os.path.join(data_dir, "tool-callings.json"), tool_calling
            )

    return StreamingResponse(event_stream(), media_type="text/event-stream")


# OpenAI 兼容 API
@app.post("/chat/completions")
async def proxy_request(request: Request):
    req_id = get_req_id()
    log_file_path = get_log_file_path(req_id)

    headers = request.headers
    body_bytes = await request.body()
    body_str = body_bytes.decode("utf-8")

    log(log_file_path, f"请求完整地址：\n{str(request.url)}")
    log(log_file_path, f"请求头：\n{headers}")
    log(log_file_path, f"模型请求体：\n{body_str}")

    params = dict(request.query_params)
    completions_url = f"{COMPLETIONS_ENDPOINT}?{urlencode(params)}"
    body = await request.json()

    data_dir = get_data_dir(req_id)
    write_dict_to_file(os.path.join(data_dir, "request.json"), body)
    write_dict_to_file(
        os.path.join(data_dir, "meta.json"),
        {"api_type": RequestApiType.COMPLETIONS.value},
    )

    log(log_file_path, "模型返回：\n")

    async def event_stream():
        resp_chunks = []
        async with httpx.AsyncClient(timeout=None) as client:
            async with client.stream(
                "POST",
                completions_url,
                json=body,
                headers={
                    "Content-Type": "application/json",
                    "Accept": "text/event-stream",
                    "Authorization": request.headers.get("Authorization"),
                },
            ) as response:
                async for line in response.aiter_lines():
                    log(log_file_path, line)
                    yield f"{line}\n"
                    # 收集有效数据
                    if line.startswith("data: ") and not line.endswith("[DONE]"):
                        json_str = line[len("data: ") :]
                        try:
                            chunk_data = json.loads(json_str)
                            resp_chunks.append(chunk_data)
                        except json.JSONDecodeError:
                            pass
                    elif line.startswith("{") and line.endswith("}"):
                        try:
                            chunk_data = json.loads(line)
                            resp_chunks.append(chunk_data)
                        except json.JSONDecodeError:
                            pass

        write_dict_to_file(os.path.join(data_dir, "response.json"), resp_chunks)

        thinking, output, tool_calling = extract_resp_completions_api(resp_chunks)
        if thinking:
            write_to_file(os.path.join(data_dir, "thinking.md"), thinking)
        if output:
            write_to_file(os.path.join(data_dir, "output.md"), output)
        if tool_calling:
            write_dict_to_file(
                os.path.join(data_dir, "tool-callings.json"), tool_calling
            )

    return StreamingResponse(event_stream(), media_type="text/event-stream")


# endregion


@app.api_route(
    "/{path_name:path}",
    methods=["GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"],
)
async def catch_all(request: Request, path_name: str):
    return ""


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8000)
