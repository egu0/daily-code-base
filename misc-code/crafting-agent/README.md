# Crafting Agent

## Workflows

See more in `./workflows/`.

```bash
uv run --with jupyter jupyter lab
```

## General Agent

Quickstart:

```bash
uv run pytest
uv run -m helloworld.main
```

Sessions:

```bash
uv run -m helloworld.main --session sess_notes
uv run -m helloworld.main --resume-latest
uv run -m helloworld.main --list-sessions
```

Session data is stored under `helloworld/sessions/<session_id>/`. The current
conversation lives in `session.json`, and each model request is saved as
`1.json`, `2.json`, and so on. Each numbered file contains both the `request`
payload and the model `response`, including reasoning fields when the provider
returns them.

Refs:

- [OpenAI Chat Completion Create API](https://developers.openai.com/api/reference/resources/chat/subresources/completions/methods/create)
- [andrew-ng-agentic-ai-course](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course)
