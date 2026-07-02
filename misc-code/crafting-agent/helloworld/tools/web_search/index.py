import json
import os
import urllib.request
import urllib.error

enabled = False


def web_search(query):
    api_key = os.environ.get("TAVILY_API_KEY", "")
    if not api_key:
        return "Error: TAVILY_API_KEY environment variable is not set"

    body = {
        "query": query,
        "max_results": 5,
        "search_depth": "advanced",
        "include_answer": "basic",
    }

    req = urllib.request.Request(
        "https://api.tavily.com/search",
        data=json.dumps(body).encode("utf-8"),
        headers={
            "Content-Type": "application/json",
            "Authorization": f"Bearer {api_key}",
        },
        method="POST",
    )

    try:
        with urllib.request.urlopen(req, timeout=15) as resp:
            data = json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as e:
        body = e.read().decode("utf-8", errors="replace")
        return f"Error: Tavily API returned {e.code}: {body}"
    except Exception as e:
        return f"Error: {e}"

    lines = []
    if data.get("answer"):
        lines.append(f"Answer: {data['answer']}")
        lines.append("")

    lines.append(f"Search results for: {data.get('query', query)}")
    lines.append("")

    for i, result in enumerate(data.get("results", []), 1):
        lines.append(f"{i}. {result.get('title', 'Untitled')}")
        lines.append(f"   URL: {result.get('url', 'N/A')}")
        content = result.get("content", "")
        if content:
            lines.append(f"   {content}")
        lines.append("")

    return "\n".join(lines)


schema = {
    "type": "function",
    "function": {
        "name": "web_search",
        "description": "Search the web using Tavily. Returns search results with titles, URLs and content snippets.",
        "parameters": {
            "type": "object",
            "properties": {
                "query": {
                    "type": "string",
                    "description": "The search query string.",
                },
            },
            "required": ["query"],
        },
    },
}

func = web_search
