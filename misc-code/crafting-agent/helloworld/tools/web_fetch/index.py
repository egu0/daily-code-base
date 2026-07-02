import json
import os
import urllib.request
import urllib.error

enabled = False

MAX_CONTENT_LENGTH = 3000


def web_fetch(urls, max_chars=MAX_CONTENT_LENGTH):
    api_key = os.environ.get("TAVILY_API_KEY", "")
    if not api_key:
        return "Error: TAVILY_API_KEY environment variable is not set"

    if isinstance(urls, str):
        urls = [urls]

    body = {
        "urls": urls,
        "extract_depth": "basic",
        "format": "markdown",
    }

    req = urllib.request.Request(
        "https://api.tavily.com/extract",
        data=json.dumps(body).encode("utf-8"),
        headers={
            "Content-Type": "application/json",
            "Authorization": f"Bearer {api_key}",
        },
        method="POST",
    )

    try:
        with urllib.request.urlopen(req, timeout=30) as resp:
            data = json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8", errors="replace")
        return f"Error: Tavily API returned {e.code}: {body}"
    except Exception as e:
        return f"Error: {e}"

    lines = []

    for result in data.get("results", []):
        url = result.get("url", "")
        content = result.get("raw_content", "")
        if max_chars and len(content) > max_chars:
            content = (
                content[:max_chars]
                + f"\n\n... [truncated, {len(result['raw_content']) - max_chars} more chars]"
            )
        lines.append(f"## {url}")
        lines.append("")
        lines.append(content)
        lines.append("")

    for failed in data.get("failed_results", []):
        lines.append(f"## Failed: {failed.get('url', '')}")
        lines.append(f"  Error: {failed.get('error', 'unknown')}")
        lines.append("")

    if not data.get("results") and not data.get("failed_results"):
        return "No content extracted."

    return "\n".join(lines)


schema = {
    "type": "function",
    "function": {
        "name": "web_fetch",
        "description": "Extract content from one or more web page URLs. Returns content in markdown format.",
        "parameters": {
            "type": "object",
            "properties": {
                "urls": {
                    "type": "array",
                    "items": {"type": "string"},
                    "description": "One or more URLs to extract content from.",
                },
                "max_chars": {
                    "type": "integer",
                    "description": "Max characters per extracted page (default 3000). Content beyond this is truncated with a note. Set to 0 for unlimited.",
                },
            },
            "required": ["urls"],
        },
    },
}

func = web_fetch
