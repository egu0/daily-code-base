from __future__ import annotations

import json
import logging
import socket
import subprocess
import time
from dataclasses import dataclass
from pathlib import Path
from shutil import which
from urllib.parse import quote
from urllib.request import Request, urlopen

from ..models import FetchResult, RunPaths
from ..utils import write_json

logger = logging.getLogger(__name__)


def build_yt_dlp_command(platform: str, url: str, output_dir: str) -> list[str]:
    command = [
        "yt-dlp",
        "--no-progress",
        "--print-to-file",
        "%(title)s",
        f"{output_dir}/.title.txt",
        "--format",
        "bestaudio/best",
        "--output",
        f"{output_dir}/source.%(ext)s",
    ]
    if platform == "bilibili":
        command.append("--no-playlist")
    command.append(url)
    return command




def extract_yt_dlp_title(output: str) -> str | None:
    for line in output.splitlines():
        stripped = line.strip()
        if stripped:
            return stripped
    return None


def resolve_parse_video_binary() -> str:
    resolved = which("parse-video")
    if resolved:
        return resolved
    fallback = Path.home() / "go" / "bin" / "parse-video"
    if fallback.exists():
        return str(fallback)
    raise FileNotFoundError(
        "parse-video not found in PATH or ~/go/bin. Install it with `go install github.com/wujunwei928/parse-video@latest`."
    )


def build_douyin_inspect_command(url: str, parse_video_bin: str) -> list[str]:
    return [parse_video_bin, "parse", "--format", "json", url]


def build_douyin_download_command(url: str, output_dir: str, parse_video_bin: str) -> list[str]:
    return [
        parse_video_bin,
        "parse",
        "--download",
        "--output-dir",
        output_dir,
        url,
    ]


def build_parse_video_service_command(parse_video_bin: str, port: int) -> list[str]:
    return [parse_video_bin, "-port", str(port)]


def build_parse_video_share_api_url(port: int, source_url: str) -> str:
    return f"http://127.0.0.1:{port}/video/share/url/parse?url={quote(source_url, safe='')}"


def extract_douyin_video_url(payload: dict[str, object]) -> str | None:
    direct = payload.get("video_url")
    if isinstance(direct, str) and direct:
        return direct
    nested = payload.get("data")
    if isinstance(nested, dict):
        nested_url = nested.get("video_url")
        if isinstance(nested_url, str) and nested_url:
            return nested_url
    return None


def download_file_with_retries(
    url: str, target_path: Path, retries: int = 3,
    headers: dict[str, str] | None = None,
) -> None:
    last_error: Exception | None = None
    for attempt in range(1, retries + 1):
        try:
            target_path.parent.mkdir(parents=True, exist_ok=True)
            req = Request(url, headers=headers or {})
            with urlopen(req, timeout=30) as response, target_path.open("wb") as output_file:
                content_length_header = response.headers.get("Content-Length")
                expected_size = int(content_length_header) if content_length_header else None
                bytes_written = 0
                while True:
                    chunk = response.read(1024 * 1024)
                    if not chunk:
                        break
                    output_file.write(chunk)
                    bytes_written += len(chunk)
            if expected_size is not None and bytes_written != expected_size:
                raise OSError(
                    f"incomplete download: wrote {bytes_written} of expected {expected_size} bytes"
                )
            if target_path.exists() and target_path.stat().st_size > 0:
                logger.info("Download completed: %d bytes", bytes_written)
                return
            raise OSError("download produced an empty file")
        except Exception as exc:
            last_error = exc
            logger.warning("Download attempt %d/%d failed: %s", attempt, retries, exc)
            if target_path.exists():
                target_path.unlink()
            if attempt == retries:
                break
            time.sleep(1.0)
    raise RuntimeError(f"Failed to download media after {retries} attempts") from last_error


def _extract_first_warning(stderr: str) -> str | None:
    for line in stderr.splitlines():
        stripped = line.strip()
        if stripped.lower().startswith("warning:"):
            return stripped
    return None


def _extract_first_error(output: str) -> str | None:
    for line in output.splitlines():
        stripped = line.strip()
        if stripped.lower().startswith("error:") or stripped.lower().startswith("error "):
            return stripped
    return None


def _read_title_file(media_dir: str) -> str | None:
    title_path = Path(media_dir) / ".title.txt"
    if title_path.exists():
        content = title_path.read_text(encoding="utf-8").strip()
        title_path.unlink()
        if content:
            return content
    return None


def _sanitize_url(url: str) -> str:
    """Remove backslash characters from a URL.

    Backslash is not valid in URLs (RFC 3986).  It typically leaks in
    through shell escaping (e.g. zsh double-quotes preserve ``\\?`` and
    ``\\=`` literally).  Stripping all backslashes is a safe, minimal
    normalization that turns ``watch\\?v\\=id`` back into ``watch?v=id``.
    """
    sanitized = url.replace("\\", "")
    if sanitized != url:
        logger.info("Sanitized URL by removing backslashes")
    return sanitized


_BROWSER_UA = (
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
)


def _cdn_headers(platform: str, source_url: str) -> dict[str, str] | None:
    """Build headers needed to download from platform CDNs."""
    if platform == "bilibili":
        return {
            "Referer": "https://www.bilibili.com/",
            "User-Agent": _BROWSER_UA,
        }
    return None


def _extract_parse_video_title(payload: dict[str, object]) -> str | None:
    """Extract video title from parse-video JSON output."""
    for key in ("title", "name"):
        val = payload.get(key)
        if isinstance(val, str) and val:
            return val
    data = payload.get("data")
    if isinstance(data, dict):
        for key in ("title", "name"):
            val = data.get(key)  # type: ignore[union-attr]
            if isinstance(val, str) and val:
                return val
    return None


@dataclass(slots=True)
class LocalFetcher:
    extra_yt_dlp_args: tuple[str, ...] = (
        "--js-runtimes",
        "node",
        "--remote-components",
        "ejs:github",
    )

    def fetch(self, url: str, platform: str, run_paths: RunPaths) -> FetchResult:
        if platform in ("douyin", "bilibili"):
            return self._fetch_via_parse_video(url, platform, run_paths)
        return self._fetch_yt_dlp(url, platform, run_paths)

    def _fetch_yt_dlp(self, url: str, platform: str, run_paths: RunPaths) -> FetchResult:
        url = _sanitize_url(url)
        command = build_yt_dlp_command(platform, url, str(run_paths.media_dir))
        command[1:1] = list(self.extra_yt_dlp_args)
        logger.info("Downloading media with yt-dlp ...")
        completed = _run_command(command)
        title = _read_title_file(str(run_paths.media_dir)) if completed.returncode == 0 else None
        run_paths.fetch_log.write_text(completed.stdout + "\n" + completed.stderr, encoding="utf-8")
        if completed.returncode != 0:
            logger.error("yt-dlp download failed (returncode=%d)", completed.returncode)
            detail = _extract_first_error(completed.stderr) or _extract_first_error(completed.stdout)
            raise RuntimeError(
                f"yt-dlp failed with exit code {completed.returncode}."
                f"{' ' + detail if detail else ''} "
                f"See log: {run_paths.fetch_log}"
            )

        if title is None and completed.returncode == 0:
            logger.warning(
                "yt-dlp produced no title line. The URL may be malformed or the video unavailable. "
                "stdout=%r stderr=%r",
                completed.stdout,
                completed.stderr,
            )

        try:
            media_path = _find_media_path(run_paths.media_dir)
        except RuntimeError:
            logger.error("yt-dlp completed but no media file was downloaded")
            detail = _extract_first_warning(completed.stderr)
            raise RuntimeError(
                "yt-dlp exited successfully but did not download any media file. "
                "The URL may be incorrect or the video may be unavailable."
                f"{' ' + detail if detail else ''} "
                f"See log: {run_paths.fetch_log}"
            ) from None
        write_json(run_paths.media_info_json, {"source_url": url, "title": title})
        return FetchResult(source_url=url, media_path=media_path, title=title)

    def _fetch_via_parse_video(
        self, url: str, platform: str, run_paths: RunPaths,
    ) -> FetchResult:
        """Download video via parse-video service (douyin, bilibili, ...).

        parse-video runs as an HTTP server that exposes a
        ``/video/share/url/parse`` endpoint.  We start the server, call the
        API to resolve the CDN video URL, download it, then shut down the
        server.
        """
        url = _sanitize_url(url)
        parse_video_bin = resolve_parse_video_binary()
        port = _find_free_port()
        logger.info("Starting parse-video service on port %d ...", port)
        service_process = subprocess.Popen(
            build_parse_video_service_command(parse_video_bin, port),
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
        )
        try:
            _wait_for_service(port, timeout_seconds=10.0)
            api_url = build_parse_video_share_api_url(port, url)
            logger.info("Calling parse-video API for %s ...", platform)
            with urlopen(api_url, timeout=20) as response:
                payload = json.loads(response.read().decode("utf-8"))
            video_url = extract_douyin_video_url(payload)
            if not video_url:
                raise RuntimeError(
                    f"parse-video ({platform}) response missing video_url: "
                    f"{json.dumps(payload, ensure_ascii=False)!r}"
                )

            media_path = run_paths.media_dir / "source.mp4"
            logger.info("Downloading video from CDN ...")
            dl_headers = _cdn_headers(platform, url)
            download_file_with_retries(video_url, media_path, headers=dl_headers)

            title = _extract_parse_video_title(payload)

            run_paths.fetch_log.write_text(
                json.dumps(payload, ensure_ascii=False, indent=2) + "\n",
                encoding="utf-8",
            )
            write_json(run_paths.media_info_json, {"source_url": url, "title": title})
        finally:
            service_process.terminate()
            try:
                service_process.wait(timeout=3)
            except subprocess.TimeoutExpired:
                service_process.kill()
                service_process.wait(timeout=3)
            logger.info("parse-video service stopped")

        return FetchResult(source_url=url, media_path=media_path, title=title)


def _find_media_path(media_dir: Path, preferred_suffixes: tuple[str, ...] = ()) -> Path:
    candidates = sorted(path for path in media_dir.iterdir() if path.is_file())
    if not candidates:
        raise RuntimeError(f"No media artifact found in {media_dir}")
    if preferred_suffixes:
        preferred = [path for path in candidates if path.suffix.lower() in preferred_suffixes]
        if preferred:
            return preferred[0]
    return candidates[0]


def _run_command(command: list[str]) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        command,
        check=False,
        capture_output=True,
        text=True,
    )


def _find_free_port() -> int:
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
        sock.bind(("127.0.0.1", 0))
        return int(sock.getsockname()[1])


def _wait_for_service(port: int, timeout_seconds: float) -> None:
    deadline = time.time() + timeout_seconds
    while time.time() < deadline:
        try:
            with socket.create_connection(("127.0.0.1", port), timeout=0.5):
                return
        except OSError:
            time.sleep(0.1)
    raise RuntimeError(f"parse-video service did not start on port {port}")
