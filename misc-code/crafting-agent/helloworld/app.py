import json
from http import HTTPStatus
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import urlparse

from .agent import (
    approve_tool_call,
    available_tools,
    cancel_session,
    create_session,
    get_session,
    session_payload,
    stream_prompt,
    update_session_tools,
)

HOST = "127.0.0.1"
PORT = 8877
STATIC_DIR = Path(__file__).resolve().parent / "static"


class Handler(SimpleHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(STATIC_DIR), **kwargs)

    def log_message(self, fmt, *args):
        print(f"{self.address_string()} - {fmt % args}")

    def end_headers(self):
        self.send_header("Cache-Control", "no-cache")
        super().end_headers()

    def do_GET(self):
        path = urlparse(self.path).path
        if path == "/api/tools":
            self.send_json({"tools": available_tools()})
            return
        if path.startswith("/api/sessions/"):
            session_id = path.split("/")[3]
            session = get_session(session_id)
            if not session:
                self.send_json({"error": "session not found"}, HTTPStatus.NOT_FOUND)
                return
            self.send_json(session_payload(session))
            return
        if path == "/":
            self.path = "/index.html"
        elif not path.startswith("/api/") and "." not in Path(path).name:
            self.path = "/index.html"
        super().do_GET()

    def do_POST(self):
        path = urlparse(self.path).path
        if path == "/api/sessions":
            session = create_session()
            self.send_json(session_payload(session))
            return

        if path.endswith("/tools") and path.startswith("/api/sessions/"):
            session_id = path.split("/")[3]
            body = self.read_json_body()
            enabled = body.get("enabledTools", [])
            if not isinstance(enabled, list):
                self.send_json({"error": "enabledTools must be a list"}, HTTPStatus.BAD_REQUEST)
                return
            updated = update_session_tools(session_id, enabled)
            self.send_json(
                {"updated": updated},
                HTTPStatus.OK if updated else HTTPStatus.NOT_FOUND,
            )
            return

        if path.endswith("/cancel") and path.startswith("/api/sessions/"):
            session_id = path.split("/")[3]
            cancelled = cancel_session(session_id)
            self.send_json(
                {"cancelled": cancelled},
                HTTPStatus.OK if cancelled else HTTPStatus.NOT_FOUND,
            )
            return

        if path.endswith("/tool-approval") and path.startswith("/api/sessions/"):
            session_id = path.split("/")[3]
            body = self.read_json_body()
            tool_call_id = body.get("toolCallId", "")
            approved = body.get("approved")
            if not isinstance(tool_call_id, str) or not tool_call_id:
                self.send_json({"error": "toolCallId is required"}, HTTPStatus.BAD_REQUEST)
                return
            if not isinstance(approved, bool):
                self.send_json({"error": "approved must be a boolean"}, HTTPStatus.BAD_REQUEST)
                return
            updated = approve_tool_call(session_id, tool_call_id, approved)
            self.send_json(
                {"updated": updated},
                HTTPStatus.OK if updated else HTTPStatus.NOT_FOUND,
            )
            return

        if path.endswith("/prompt/stream") and path.startswith("/api/sessions/"):
            session_id = path.split("/")[3]
            session = get_session(session_id)
            if not session:
                self.send_json({"error": "session not found"}, HTTPStatus.NOT_FOUND)
                return

            body = self.read_json_body()
            prompt = body.get("prompt", "")
            enabled_tools = body.get("enabledTools", [])
            if not isinstance(prompt, str) or not prompt.strip():
                self.send_json({"error": "prompt is required"}, HTTPStatus.BAD_REQUEST)
                return

            self.stream_events(session, prompt, enabled_tools)
            return

        self.send_json({"error": "not found"}, HTTPStatus.NOT_FOUND)

    def read_json_body(self):
        length = int(self.headers.get("Content-Length", "0"))
        if length == 0:
            return {}
        raw = self.rfile.read(length)
        return json.loads(raw.decode("utf-8"))

    def send_json(self, body, status=HTTPStatus.OK):
        payload = json.dumps(body, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(payload)))
        self.send_header("Cache-Control", "no-cache")
        self.end_headers()
        self.wfile.write(payload)

    def stream_events(self, session, prompt, enabled_tools):
        self.send_response(HTTPStatus.OK)
        self.send_header("Content-Type", "application/x-ndjson; charset=utf-8")
        self.send_header("X-Content-Type-Options", "nosniff")
        self.send_header("Cache-Control", "no-cache")
        self.send_header("Connection", "close")
        self.end_headers()

        for item in stream_prompt(session, prompt, enabled_tools):
            line = json.dumps(item, ensure_ascii=False).encode("utf-8") + b"\n"
            try:
                self.wfile.write(line)
                self.wfile.flush()
            except BrokenPipeError:
                cancel_session(session.id)
                break


def run():
    server = ThreadingHTTPServer((HOST, PORT), Handler)
    print(f"helloworld-web running at http://{HOST}:{PORT}")
    server.serve_forever()


if __name__ == "__main__":
    try:
        run()
    except KeyboardInterrupt:
        print("\nGoodbye!")
