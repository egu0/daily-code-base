# Crafting Agent

## Workflows

See more in `./workflows/`.

```bash
uv run --with jupyter jupyter lab
```

## General Agent

### Shell command

Add this to `~/.zshrc` to make `hgent` available everywhere:

```zsh
export HELLO_AGENT_HOME="$HOME/.hello-agent"
hgent() {
  local hgent_cwd="$PWD"
  (cd "/path/to/crafting-agent" && HELLO_AGENT_WORKDIR="$hgent_cwd" <agent-start-command> "$@")
}
```

Reload the shell after editing:

```zsh
source ~/.zshrc
```

### Quickstart

```bash
uv run pytest
hgent
```

`hgent` starts the agent from this repository, but uses the directory where you
typed `hgent` as the tool working directory.

By default, the agent uses `~/.hello-agent` as its global home:

```text
~/.hello-agent/
  skills/
  mcp_config.json
  sessions/
```

Session data is stored under `~/.hello-agent/sessions/<session_id>/`. The current
conversation lives in `session.json`, and each model request is saved as
`1.json`, `2.json`, and so on. Each numbered file contains both the `request`
payload and the model `response`, including reasoning fields when the provider
returns them.

Environment overrides:

| Variable                 | Purpose                                                             |
| ------------------------ | ------------------------------------------------------------------- |
| `HELLO_AGENT_HOME`       | Base directory for global agent data. Defaults to `~/.hello-agent`. |
| `HELLO_AGENT_WORKDIR`    | Tool working directory when `--workdir` is not provided.            |
| `HELLO_AGENT_SKILLS_DIR` | Skills directory. Defaults to `$HELLO_AGENT_HOME/skills`.           |
| `HELLOWORLD_MCP_CONFIG`  | MCP config file. Defaults to `$HELLO_AGENT_HOME/mcp_config.json`.   |
| `HELLOWORLD_SESSION_DIR` | Session data directory. Defaults to `$HELLO_AGENT_HOME/sessions`.   |

### Skills

Skills are loaded from `~/.hello-agent/skills` by default. Each skill must live
in its own directory and contain a `SKILL.md` file:

```text
~/.hello-agent/skills/
  weather/
    SKILL.md
  frontend-design/
    SKILL.md
```

`SKILL.md` must start with YAML-style metadata. Required fields:

| Field         | Required | Description                                                  |
| ------------- | -------- | ------------------------------------------------------------ |
| `name`        | Yes      | Public skill name used by the agent and `read_skill`.        |
| `description` | Yes      | Short summary used in the system prompt for skill selection. |

Example:

```markdown
---
name: weather
description: Use for weather lookup and weather-related planning.
---

# Weather

Use this skill when the user asks about forecasts, conditions, or weather plans.
```

### MCP servers

MCP servers are loaded from `~/.hello-agent/mcp_config.json` by default. The file
can be either a top-level list or an object with a `servers` list. Prefer the
object form:

```json
{
  "servers": [
    {
      "name": "playwright",
      "enabled": true,
      "command": "npx",
      "args": ["-y", "@playwright/mcp@latest"],
      "env": {
        "EXAMPLE_API_KEY": "${EXAMPLE_API_KEY}"
      },
      "allowed_tools": ["browser_navigate", "browser_snapshot"]
    }
  ]
}
```

Server fields:

| Field           | Required | Description                                                                                                              |
| --------------- | -------- | ------------------------------------------------------------------------------------------------------------------------ |
| `name`          | Yes      | Local server alias. Tool names are exposed as `<name>__<tool>`, for example `playwright__browser_navigate`.              |
| `command`       | Yes      | Command used to start the MCP server, such as `npx`, `uvx`, or an absolute path.                                         |
| `type`          | No       | MCP client type. Defaults to `"mcp"` when omitted.                                                                       |
| `enabled`       | No       | Whether to load this server. Defaults to `true`; set to `false` to skip it.                                              |
| `args`          | No       | Command arguments as a list of strings.                                                                                  |
| `env`           | No       | Environment variables passed to the server. Values like `"${VAR_NAME}"` are expanded from the current shell environment. |
| `allowed_tools` | No       | Allowlist of original MCP tool names to expose. Omit or use an empty list to expose all tools from the server.           |

The model sees MCP tools through `tool_search`, `tool_describe`, and
`tool_call`. Local core tools such as `read`, `grep`, and `bash` remain directly
available.

Refs:

- [OpenAI Chat Completion Create API](https://developers.openai.com/api/reference/resources/chat/subresources/completions/methods/create)
- [andrew-ng-agentic-ai-course](https://github.com/egu0/daily-code-base/tree/main/artificial-intelligence/andrew-ng-agentic-ai-course)
