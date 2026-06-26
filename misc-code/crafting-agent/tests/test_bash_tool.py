import sys
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.tools import tool_schemas, tools_map
from helloworld.tools.bash.index import bash, schema


def test_bash_executes_simple_command():
    result = bash("echo hello")
    assert "Exit code: 0" in result
    assert "hello" in result


def test_bash_reports_non_zero_exit():
    result = bash("exit 42")
    assert "Exit code: 42" in result


def test_bash_captures_stderr():
    result = bash("echo error >&2")
    assert "stderr:" in result
    assert "error" in result


def test_bash_respects_cwd(tmp_path):
    result = bash("echo $PWD", cwd=str(tmp_path))
    assert str(tmp_path) in result


def test_bash_rejects_missing_cwd():
    result = bash("echo hi", cwd="/nonexistent/path/xyz")
    assert "Error:" in result
    assert "not a directory" in result


def test_bash_times_out():
    result = bash("sleep 10", timeout=1)
    assert "timed out" in result


def test_bash_rejects_empty_command():
    result = bash("")
    assert "Error:" in result


def test_bash_caps_timeout_at_max(tmp_path):
    # Should not raise — timeout is silently capped
    result = bash("echo ok", timeout=9999, cwd=str(tmp_path))
    assert "ok" in result


def test_bash_truncates_long_output():
    # Generate output larger than 10k chars
    result = bash("python3 -c \"print('x' * 15000)\"")
    assert "truncated" in result.lower()


def test_bash_tool_is_registered():
    assert schema["function"]["name"] == "bash"
    assert tools_map["bash"] is bash
    assert any(item["function"]["name"] == "bash" for item in tool_schemas)
