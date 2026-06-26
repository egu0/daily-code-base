# Agent

## terminal

```bash
uv run python -m helloworld.main
```

```bash
uv run python -m helloworld.main --session sess_notes
uv run python -m helloworld.main --resume-latest
uv run python -m helloworld.main --list-sessions
```

Session data is stored under `sessions/<session_id>/`. The current conversation
lives in `session.json`, and each model request is saved as `1.json`, `2.json`,
and so on. Each numbered file contains both the `request` payload and the model
`response`, including reasoning fields when the provider returns them.

features:

- [x] tools use
- [x] MCP tools
- [x] session persistence
