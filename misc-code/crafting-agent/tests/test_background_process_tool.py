import sys
import time
from pathlib import Path

ROOT_DIR = Path(__file__).resolve().parents[1]
if str(ROOT_DIR) not in sys.path:
    sys.path.insert(0, str(ROOT_DIR))

from helloworld.tools import tool_schemas, tools_map
from helloworld.tools.background_process.index import background_process, schema
from helloworld.tools.background_process.manager import close_process_manager


def test_start_and_stop():
    result = background_process(action="start", command="sleep 60")
    assert "Process started:" in result
    proc_id = result.splitlines()[0].split(": ")[1].strip()

    status = background_process(action="status", process_id=proc_id)
    assert "status: running" in status

    stop = background_process(action="stop", process_id=proc_id)
    assert "stopped" in stop.lower()

    status2 = background_process(action="status", process_id=proc_id)
    assert "status: exited" in status2


def test_start_with_cwd(tmp_path):
    result = background_process(action="start", command="sleep 10", cwd=str(tmp_path))
    assert str(tmp_path) in result
    proc_id = result.splitlines()[0].split(": ")[1].strip()
    background_process(action="stop", process_id=proc_id)


def test_read_output():
    result = background_process(action="start", command="echo hello && echo world")
    proc_id = result.splitlines()[0].split(": ")[1].strip()
    time.sleep(0.5)  # let threads write output

    output = background_process(action="read_output", process_id=proc_id, lines=10)
    assert "hello" in output
    assert "world" in output


def test_read_output_by_stream():
    result = background_process(action="start", command="echo to-stdout && echo to-stderr >&2")
    proc_id = result.splitlines()[0].split(": ")[1].strip()
    time.sleep(0.5)

    out = background_process(action="read_output", process_id=proc_id, stream="stdout")
    assert "to-stdout" in out
    assert "to-stderr" not in out.replace("--- stderr", "")

    err = background_process(action="read_output", process_id=proc_id, stream="stderr")
    assert "to-stderr" in err


def test_send_input():
    result = background_process(action="start", command="cat")
    proc_id = result.splitlines()[0].split(": ")[1].strip()
    time.sleep(0.2)

    send = background_process(action="send_input", process_id=proc_id, input="test message")
    assert "Sent input" in send

    # signal EOF so cat exits
    from helloworld.tools.background_process.manager import get_process_manager
    mgr = get_process_manager()
    popen = mgr._popen_objects.get(proc_id)
    if popen and popen.stdin:
        popen.stdin.close()
    time.sleep(0.3)

    output = background_process(action="read_output", process_id=proc_id, stream="stdout")
    assert "test message" in output


def test_list():
    result = background_process(action="start", command="sleep 30")
    proc_id = result.splitlines()[0].split(": ")[1].strip()

    lst = background_process(action="list")
    assert proc_id in lst
    assert "sleep 30" in lst

    background_process(action="stop", process_id=proc_id)


def test_error_on_unknown_process():
    result = background_process(action="status", process_id="nonexist")
    assert "Error:" in result


def test_error_on_unknown_action():
    result = background_process(action="invalid")
    assert "Error:" in result
    assert "unknown action" in result


def test_error_on_missing_params():
    assert "Error:" in background_process(action="start")
    assert "Error:" in background_process(action="stop")
    assert "Error:" in background_process(action="status")
    assert "Error:" in background_process(action="read_output")
    assert "Error:" in background_process(action="send_input")
    assert "Error:" in background_process(action="send_input", process_id="x")


def test_tool_is_registered():
    assert schema["function"]["name"] == "background_process"
    assert tools_map["background_process"] is background_process
    assert any(
        item["function"]["name"] == "background_process" for item in tool_schemas
    )


def test_stale_detection():
    """Processes that have exited get marked as dead or exited."""
    result = background_process(action="start", command="true")  # exits immediately
    proc_id = result.splitlines()[0].split(": ")[1].strip()
    time.sleep(0.5)

    status = background_process(action="status", process_id=proc_id)
    # The process should have exited by now
    assert "running" not in status.split("status:")[1].strip() or "exit_code" in status


def teardown_module():
    """Clean up the process manager singleton between test runs."""
    close_process_manager()
