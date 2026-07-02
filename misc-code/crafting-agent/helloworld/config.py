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


def project_skills_dir(workdir: str | Path | None = None) -> Path:
    return agent_workdir(workdir) / ".hello-agent" / "skills"


def resolve_skills_dir(workdir: str | Path | None = None) -> Path:
    explicit_dir = os.environ.get(SKILLS_DIR_ENV)
    if explicit_dir:
        return Path(explicit_dir).expanduser()

    project_dir = project_skills_dir(workdir)
    if project_dir.exists():
        return project_dir

    return default_skills_dir()


def default_mcp_config_path() -> Path:
    return agent_home() / "mcp_config.json"


def project_mcp_config_path(workdir: str | Path | None = None) -> Path:
    return agent_workdir(workdir) / ".hello-agent" / "mcp_config.json"


def resolve_mcp_config_path(workdir: str | Path | None = None) -> Path:
    explicit_path = os.environ.get(MCP_CONFIG_ENV)
    if explicit_path:
        return Path(explicit_path).expanduser()

    project_config = project_mcp_config_path(workdir)
    if project_config.exists():
        return project_config

    return default_mcp_config_path()


def default_session_dir() -> Path:
    return agent_home() / "sessions"


def agent_workdir(path: str | Path | None = None) -> Path:
    candidate = path or os.environ.get(AGENT_WORKDIR_ENV) or os.getcwd()
    return Path(candidate).expanduser().resolve()
