import sys
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld import config


def test_agent_home_defaults_to_user_hello_agent(monkeypatch):
    monkeypatch.delenv(config.AGENT_HOME_ENV, raising=False)
    monkeypatch.setattr(Path, "home", lambda: Path("/home/tester"))

    assert config.agent_home() == Path("/home/tester/.hello-agent")


def test_agent_paths_default_under_agent_home(monkeypatch):
    monkeypatch.setenv(config.AGENT_HOME_ENV, "/tmp/custom-agent")

    assert config.default_skills_dir() == Path("/tmp/custom-agent/skills")
    assert config.default_mcp_config_path() == Path("/tmp/custom-agent/mcp_config.json")
    assert config.default_session_dir() == Path("/tmp/custom-agent/sessions")


def test_resolve_skills_dir_prefers_explicit_environment(monkeypatch, tmp_path):
    workdir = tmp_path / "project"
    explicit_dir = tmp_path / "explicit-skills"
    workdir.mkdir()
    monkeypatch.setenv(config.SKILLS_DIR_ENV, str(explicit_dir))

    assert config.resolve_skills_dir(workdir) == explicit_dir


def test_resolve_skills_dir_prefers_project_skills(monkeypatch, tmp_path):
    workdir = tmp_path / "project"
    project_skills = workdir / ".hello-agent" / "skills"
    project_skills.mkdir(parents=True)
    monkeypatch.delenv(config.SKILLS_DIR_ENV, raising=False)
    monkeypatch.setenv(config.AGENT_HOME_ENV, str(tmp_path / "global-agent"))

    assert config.resolve_skills_dir(workdir) == project_skills


def test_resolve_skills_dir_falls_back_to_global_agent_home(monkeypatch, tmp_path):
    workdir = tmp_path / "project"
    agent_home = tmp_path / "global-agent"
    workdir.mkdir()
    monkeypatch.delenv(config.SKILLS_DIR_ENV, raising=False)
    monkeypatch.setenv(config.AGENT_HOME_ENV, str(agent_home))

    assert config.resolve_skills_dir(workdir) == agent_home / "skills"


def test_resolve_mcp_config_path_prefers_explicit_environment(monkeypatch, tmp_path):
    workdir = tmp_path / "project"
    explicit_path = tmp_path / "explicit.json"
    workdir.mkdir()
    monkeypatch.setenv(config.MCP_CONFIG_ENV, str(explicit_path))

    assert config.resolve_mcp_config_path(workdir) == explicit_path


def test_resolve_mcp_config_path_prefers_project_config(monkeypatch, tmp_path):
    workdir = tmp_path / "project"
    project_config = workdir / ".hello-agent" / "mcp_config.json"
    project_config.parent.mkdir(parents=True)
    project_config.write_text('{"servers": []}', encoding="utf-8")
    monkeypatch.delenv(config.MCP_CONFIG_ENV, raising=False)
    monkeypatch.setenv(config.AGENT_HOME_ENV, str(tmp_path / "global-agent"))

    assert config.resolve_mcp_config_path(workdir) == project_config


def test_resolve_mcp_config_path_falls_back_to_global_agent_home(
    monkeypatch, tmp_path
):
    workdir = tmp_path / "project"
    agent_home = tmp_path / "global-agent"
    workdir.mkdir()
    monkeypatch.delenv(config.MCP_CONFIG_ENV, raising=False)
    monkeypatch.setenv(config.AGENT_HOME_ENV, str(agent_home))

    assert config.resolve_mcp_config_path(workdir) == agent_home / "mcp_config.json"


def test_agent_workdir_prefers_environment(monkeypatch, tmp_path):
    monkeypatch.setenv(config.AGENT_WORKDIR_ENV, str(tmp_path))

    assert config.agent_workdir() == tmp_path.resolve()


def test_agent_workdir_defaults_to_current_directory(monkeypatch, tmp_path):
    monkeypatch.delenv(config.AGENT_WORKDIR_ENV, raising=False)
    monkeypatch.chdir(tmp_path)

    assert config.agent_workdir() == tmp_path.resolve()
