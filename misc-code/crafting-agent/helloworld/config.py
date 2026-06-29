import os
from pathlib import Path

AGENT_HOME_ENV = "HELLO_AGENT_HOME"
AGENT_WORKDIR_ENV = "HELLO_AGENT_WORKDIR"
SKILLS_DIR_ENV = "HELLO_AGENT_SKILLS_DIR"
MCP_CONFIG_ENV = "HELLOWORLD_MCP_CONFIG"
SESSION_DIR_ENV = "HELLOWORLD_SESSION_DIR"


def agent_home() -> Path:
    return Path(os.environ.get(AGENT_HOME_ENV, Path.home() / ".hello-agent")).expanduser()


def default_skills_dir() -> Path:
    return Path(os.environ.get(SKILLS_DIR_ENV, agent_home() / "skills")).expanduser()


def default_mcp_config_path() -> Path:
    return agent_home() / "mcp_config.json"


def default_session_dir() -> Path:
    return agent_home() / "sessions"


def agent_workdir(path: str | Path | None = None) -> Path:
    candidate = path or os.environ.get(AGENT_WORKDIR_ENV) or os.getcwd()
    return Path(candidate).expanduser().resolve()
