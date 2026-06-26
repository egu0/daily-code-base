import json
import os
import secrets
import signal
import subprocess
import threading
from datetime import datetime, timezone
from pathlib import Path


BASE_DIR = Path.home() / ".helloworld" / "processes"
MAX_OUTPUT_LINES = 10_000  # cap per log file


def _now_iso() -> str:
    return datetime.now(timezone.utc).isoformat()


def _format_duration(start_iso: str) -> str:
    try:
        start = datetime.fromisoformat(start_iso)
        delta = datetime.now(timezone.utc) - start
        total_secs = int(delta.total_seconds())
        if total_secs < 60:
            return f"{total_secs}s"
        if total_secs < 3600:
            return f"{total_secs // 60}m {total_secs % 60}s"
        hours = total_secs // 3600
        mins = (total_secs % 3600) // 60
        return f"{hours}h {mins}m"
    except (ValueError, TypeError):
        return "unknown"


class ProcessManager:
    def __init__(self) -> None:
        BASE_DIR.mkdir(parents=True, exist_ok=True)
        self._lock = threading.RLock()
        self._registry_path = BASE_DIR / "registry.json"
        self._processes: dict[str, dict] = {}
        self._popen_objects: dict[str, subprocess.Popen] = {}
        self._load_registry()
        self._clean_stale()

    # --- registry persistence ------------------------------------------------

    def _load_registry(self) -> None:
        if not self._registry_path.exists():
            self._processes = {}
            return
        try:
            data = json.loads(self._registry_path.read_text("utf-8"))
            self._processes = data.get("processes", {})
        except (OSError, json.JSONDecodeError):
            self._processes = {}

    def _save_registry(self) -> None:
        with self._lock:
            payload = {"processes": self._processes}
            self._registry_path.write_text(json.dumps(payload, indent=2), "utf-8")

    # --- stale detection -----------------------------------------------------

    def _is_pid_alive(self, pid: int) -> bool:
        try:
            os.kill(pid, 0)
            return True
        except (OSError, ProcessLookupError):
            return False

    def _clean_stale(self) -> None:
        with self._lock:
            changed = False
            for proc_id, info in list(self._processes.items()):
                if info.get("status") == "running":
                    pid = info.get("pid")
                    if pid is not None and not self._is_pid_alive(pid):
                        info["status"] = "dead"
                        info["exit_code"] = None
                        changed = True
            if changed:
                self._save_registry()

    # --- start ---------------------------------------------------------------

    def start(self, command: str, cwd: str | None = None) -> str:
        self._clean_stale()

        work_dir = os.path.expanduser(cwd) if cwd else os.getcwd()
        if not os.path.isdir(work_dir):
            return f"Error: cwd is not a directory: {work_dir}"

        proc_id = secrets.token_hex(4)  # 8-char hex
        proc_dir = BASE_DIR / proc_id
        proc_dir.mkdir(parents=True, exist_ok=True)
        stdout_path = proc_dir / "stdout.log"
        stderr_path = proc_dir / "stderr.log"

        try:
            popen = subprocess.Popen(
                command,
                shell=True,
                stdin=subprocess.PIPE,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                cwd=work_dir,
                text=True,
                bufsize=1,  # line buffered
                preexec_fn=os.setsid,  # process group for clean kill
            )
        except OSError as exc:
            return f"Error: unable to start process: {exc}"

        info = {
            "command": command,
            "cwd": work_dir,
            "pid": popen.pid,
            "start_time": _now_iso(),
            "status": "running",
            "exit_code": None,
        }

        with self._lock:
            self._processes[proc_id] = info
            self._popen_objects[proc_id] = popen
            self._save_registry()

        # spawn reader threads
        threading.Thread(
            target=self._capture_output,
            args=(popen.stdout, stdout_path),
            daemon=True,
        ).start()
        threading.Thread(
            target=self._capture_output,
            args=(popen.stderr, stderr_path),
            daemon=True,
        ).start()

        # monitor for exit
        threading.Thread(
            target=self._monitor_exit,
            args=(proc_id, popen),
            daemon=True,
        ).start()

        return (
            f"Process started: {proc_id}\n"
            f"  command: {command}\n"
            f"  cwd: {work_dir}\n"
            f"  pid: {popen.pid}"
        )

    # --- capture output threads ----------------------------------------------

    def _capture_output(self, stream, path: Path) -> None:
        line_count = 0
        try:
            with open(path, "w", buffering=1) as fh:
                for line in iter(stream.readline, ""):
                    if line_count >= MAX_OUTPUT_LINES:
                        fh.write("... [output truncated at 10,000 lines]\n")
                        break
                    fh.write(line)
                    line_count += 1
        except (OSError, ValueError):
            pass
        finally:
            try:
                stream.close()
            except OSError:
                pass

    # --- monitor exit --------------------------------------------------------

    def _monitor_exit(self, proc_id: str, popen: subprocess.Popen) -> None:
        exit_code = popen.wait()
        with self._lock:
            if proc_id in self._processes:
                info = self._processes[proc_id]
                if info.get("status") == "running":
                    info["status"] = "exited"
                    info["exit_code"] = exit_code
            self._popen_objects.pop(proc_id, None)
            self._save_registry()

    # --- stop ----------------------------------------------------------------

    def stop(self, process_id: str) -> str:
        with self._lock:
            info = self._processes.get(process_id)
            if info is None:
                return f"Error: no process with id '{process_id}'"

            if info.get("status") != "running":
                return f"Error: process '{process_id}' is not running (status: {info.get('status')})"

            popen = self._popen_objects.get(process_id)
            if popen is None:
                info["status"] = "dead"
                self._save_registry()
                return f"Error: process '{process_id}' has no active Popen handle (marked as dead)"

            pid = info["pid"]

        try:
            os.killpg(os.getpgid(popen.pid), signal.SIGTERM)
            try:
                popen.wait(timeout=5)
            except subprocess.TimeoutExpired:
                os.killpg(os.getpgid(popen.pid), signal.SIGKILL)
                popen.wait(timeout=3)
        except (OSError, ProcessLookupError):
            pass

        returncode = popen.returncode if popen.returncode is not None else -1

        with self._lock:
            if process_id in self._processes:
                self._processes[process_id]["status"] = "exited"
                self._processes[process_id]["exit_code"] = returncode
            self._save_registry()

        return f"Process {process_id} stopped (exit code: {returncode})"

    # --- status --------------------------------------------------------------

    def status(self, process_id: str) -> str:
        with self._lock:
            info = self._processes.get(process_id)
            if info is None:
                return f"Error: no process with id '{process_id}'"

            if info.get("status") == "running":
                pid = info.get("pid")
                if pid is not None and not self._is_pid_alive(pid):
                    info["status"] = "dead"
                    self._save_registry()

            runtime = _format_duration(info["start_time"])
            exit_code = info.get("exit_code")

            lines = [
                f"Process {process_id}:",
                f"  command: {info['command']}",
                f"  cwd: {info['cwd']}",
                f"  pid: {info['pid']}",
                f"  status: {info['status']}",
                f"  started: {info['start_time']}",
                f"  runtime: {runtime}",
            ]
            if exit_code is not None:
                lines.append(f"  exit_code: {exit_code}")

        return "\n".join(lines)

    # --- read_output ---------------------------------------------------------

    def read_output(self, process_id: str, stream: str = "both", lines: int = 100) -> str:
        if stream not in ("stdout", "stderr", "both"):
            return "Error: stream must be 'stdout', 'stderr', or 'both'"

        with self._lock:
            info = self._processes.get(process_id)
        if info is None:
            return f"Error: no process with id '{process_id}'"

        proc_dir = BASE_DIR / process_id
        parts: list[str] = []

        if stream in ("stdout", "both"):
            stdout_path = proc_dir / "stdout.log"
            parts.append(f"--- stdout (last {lines} lines) ---")
            if stdout_path.exists():
                parts.append(self._tail_file(stdout_path, lines) or "(empty)")
            else:
                parts.append("(no output yet)")

        if stream in ("stderr", "both"):
            stderr_path = proc_dir / "stderr.log"
            parts.append(f"--- stderr (last {lines} lines) ---")
            if stderr_path.exists():
                parts.append(self._tail_file(stderr_path, lines) or "(empty)")
            else:
                parts.append("(no output yet)")

        return "\n".join(parts)

    @staticmethod
    def _tail_file(path: Path, n: int) -> str:
        try:
            all_lines = path.read_text("utf-8").splitlines()
            selected = all_lines[-n:] if len(all_lines) > n else all_lines
            return "\n".join(selected)
        except OSError as exc:
            return f"(error reading file: {exc})"

    # --- send_input ----------------------------------------------------------

    def send_input(self, process_id: str, text: str) -> str:
        with self._lock:
            info = self._processes.get(process_id)
            if info is None:
                return f"Error: no process with id '{process_id}'"

            if info.get("status") != "running":
                return (
                    f"Error: process '{process_id}' is not running "
                    f"(status: {info.get('status')})"
                )

            popen = self._popen_objects.get(process_id)

        if popen is None or popen.stdin is None or popen.stdin.closed:
            return f"Error: stdin is not available for process '{process_id}'"

        try:
            popen.stdin.write(text.rstrip("\n") + "\n")
            popen.stdin.flush()
        except (BrokenPipeError, OSError) as exc:
            return f"Error: unable to write to process '{process_id}': {exc}"

        return f"Sent input to process {process_id}"

    # --- list_all ------------------------------------------------------------

    def list_all(self) -> str:
        with self._lock:
            if not self._processes:
                return "No background processes"

            items = list(self._processes.items())

        lines = [f"{len(items)} background process(es):"]
        for proc_id, info in sorted(items):
            status = info.get("status", "unknown")
            runtime = _format_duration(info.get("start_time", ""))
            lines.append(
                f"  {proc_id}  {status}  {info['command']}"
                f"  (pid {info['pid']}, started {runtime} ago)"
            )
        return "\n".join(lines)

    # --- close ---------------------------------------------------------------

    def close(self) -> None:
        """Release the lock. Does NOT kill processes."""
        pass  # lock is released when object goes out of scope; no explicit cleanup needed


# --- module-level singleton ------------------------------------------------

_process_manager: ProcessManager | None = None
_singleton_lock = threading.Lock()


def get_process_manager() -> ProcessManager:
    global _process_manager
    if _process_manager is None:
        with _singleton_lock:
            if _process_manager is None:
                _process_manager = ProcessManager()
    return _process_manager


def close_process_manager() -> None:
    global _process_manager
    with _singleton_lock:
        if _process_manager:
            _process_manager.close()
            _process_manager = None
