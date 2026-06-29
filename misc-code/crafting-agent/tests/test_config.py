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


def test_agent_workdir_prefers_environment(monkeypatch, tmp_path):
    monkeypatch.setenv(config.AGENT_WORKDIR_ENV, str(tmp_path))

    assert config.agent_workdir() == tmp_path.resolve()


def test_agent_workdir_defaults_to_current_directory(monkeypatch, tmp_path):
    monkeypatch.delenv(config.AGENT_WORKDIR_ENV, raising=False)
    monkeypatch.chdir(tmp_path)

    assert config.agent_workdir() == tmp_path.resolve()
