import json
from pathlib import Path

import pytest

from src.fetchers.local import (
    LocalFetcher,
    _extract_parse_video_title,
    _sanitize_url,
    extract_douyin_video_url,
    download_file_with_retries,
    build_douyin_download_command,
    build_douyin_inspect_command,
    build_parse_video_service_command,
    build_parse_video_share_api_url,
    extract_yt_dlp_title,
    build_yt_dlp_command,
    resolve_parse_video_binary,
)


# ---- _sanitize_url -------------------------------------------------------

def test_sanitize_url_removes_backslashes_from_query() -> None:
    assert _sanitize_url(
        "https://www.youtube.com/watch\\?v\\=446E-r0rXHI"
    ) == "https://www.youtube.com/watch?v=446E-r0rXHI"


def test_sanitize_url_preserves_valid_urls() -> None:
    assert _sanitize_url("https://www.youtube.com/watch?v=abc") == "https://www.youtube.com/watch?v=abc"


def test_sanitize_url_handles_no_backslashes() -> None:
    assert _sanitize_url("https://www.bilibili.com/video/BV1xx") == "https://www.bilibili.com/video/BV1xx"


# ---- build_yt_dlp_command -------------------------------------------------

def test_build_yt_dlp_command_sets_output_dir() -> None:
    command = build_yt_dlp_command(
        platform="youtube",
        url="https://www.youtube.com/watch?v=abc",
        output_dir="/tmp/run/media",
    )
    assert "--print-to-file" in command
    assert "%(title)s" in command
    assert "/tmp/run/media/.title.txt" in command
    assert command[:2] == ["yt-dlp", "--no-progress"]
    assert "--output" in command
    assert "/tmp/run/media/source.%(ext)s" in command
    assert "--output" in command
    assert "/tmp/run/media/source.%(ext)s" in command


def test_local_fetcher_injects_extra_yt_dlp_args() -> None:
    """LocalFetcher.extra_yt_dlp_args should be injected into the yt-dlp command."""
    fetcher = LocalFetcher()
    assert fetcher.extra_yt_dlp_args == (
        "--js-runtimes",
        "node",
        "--remote-components",
        "ejs:github",
    )


def test_build_yt_dlp_command_uses_no_playlist_for_bilibili() -> None:
    command = build_yt_dlp_command(
        platform="bilibili",
        url="https://www.bilibili.com/video/BV1xx",
        output_dir="/tmp/run/media",
    )
    assert "--no-playlist" in command


def test_build_douyin_inspect_command_uses_local_parse_video() -> None:
    command = build_douyin_inspect_command(
        "https://www.douyin.com/video/123",
        parse_video_bin="parse-video",
    )
    assert command[:4] == ["parse-video", "parse", "--format", "json"]


def test_build_douyin_download_command_uses_local_parse_video() -> None:
    command = build_douyin_download_command(
        url="https://www.douyin.com/video/123",
        output_dir="/tmp/run/media",
        parse_video_bin="parse-video",
    )
    assert command[:2] == ["parse-video", "parse"]
    assert "--download" in command
    assert "/tmp/run/media" in command


def test_build_parse_video_service_command_uses_port() -> None:
    command = build_parse_video_service_command(
        parse_video_bin="parse-video",
        port=19080,
    )
    assert command == ["parse-video", "-port", "19080"]


def test_build_parse_video_share_api_url_encodes_source_url() -> None:
    url = build_parse_video_share_api_url(19080, "https://www.douyin.com/video/123?a=1")
    assert url.startswith("http://127.0.0.1:19080/video/share/url/parse?url=")
    assert "https%3A%2F%2Fwww.douyin.com%2Fvideo%2F123%3Fa%3D1" in url


def test_extract_douyin_video_url_supports_flat_shape() -> None:
    payload = {"video_url": "https://example.com/flat.mp4"}
    assert extract_douyin_video_url(payload) == "https://example.com/flat.mp4"


def test_extract_douyin_video_url_supports_nested_data_shape() -> None:
    payload = {
        "code": 200,
        "msg": "ok",
        "data": {
            "video_url": "https://example.com/video.mp4",
        },
    }
    assert extract_douyin_video_url(payload) == "https://example.com/video.mp4"


def test_extract_douyin_video_url_returns_none_when_missing() -> None:
    assert extract_douyin_video_url({}) is None
    assert extract_douyin_video_url({"data": {}}) is None
    assert extract_douyin_video_url({"data": {"other": 1}}) is None


def test_extract_douyin_video_url_prefers_top_level_over_nested() -> None:
    payload = {
        "video_url": "https://example.com/top.mp4",
        "data": {"video_url": "https://example.com/nested.mp4"},
    }
    assert extract_douyin_video_url(payload) == "https://example.com/top.mp4"


def test_download_file_with_retries_retries_once(
    monkeypatch: pytest.MonkeyPatch,
    tmp_path: Path,
) -> None:
    target = tmp_path / "file.bin"
    calls = {"count": 0}

    class FakeResponse:
        def __init__(self) -> None:
            self._done = False
            self.headers = {}

        def __enter__(self):
            return self

        def __exit__(self, exc_type, exc, tb):
            return None

        def read(self, size: int = -1) -> bytes:
            if self._done:
                return b""
            self._done = True
            return b"abc"

    def fake_urlopen(url: str, timeout: int):
        calls["count"] += 1
        if calls["count"] == 1:
            raise OSError("temporary failure")
        return FakeResponse()

    monkeypatch.setattr("src.fetchers.local.urlopen", fake_urlopen)

    download_file_with_retries("https://example.com/file.bin", target, retries=2)

    assert target.read_bytes() == b"abc"
    assert calls["count"] == 2


def test_download_file_with_retries_retries_on_truncated_response(
    monkeypatch: pytest.MonkeyPatch,
    tmp_path: Path,
) -> None:
    target = tmp_path / "file.bin"
    calls = {"count": 0}

    class FakeHeaders:
        def __init__(self, content_length: str | None) -> None:
            self._content_length = content_length

        def get(self, key: str) -> str | None:
            if key.lower() == "content-length":
                return self._content_length
            return None

    class FakeResponse:
        def __init__(self, chunks: list[bytes], content_length: str | None) -> None:
            self._chunks = chunks
            self.headers = FakeHeaders(content_length)

        def __enter__(self):
            return self

        def __exit__(self, exc_type, exc, tb):
            return None

        def read(self, size: int = -1) -> bytes:
            if not self._chunks:
                return b""
            return self._chunks.pop(0)

    def fake_urlopen(url: str, timeout: int):
        calls["count"] += 1
        if calls["count"] == 1:
            return FakeResponse([b"abc"], "10")
        return FakeResponse([b"abcdefghij"], "10")

    monkeypatch.setattr("src.fetchers.local.urlopen", fake_urlopen)

    download_file_with_retries("https://example.com/file.bin", target, retries=2)

    assert target.read_bytes() == b"abcdefghij"
    assert calls["count"] == 2


def test_extract_yt_dlp_title_reads_first_nonempty_line() -> None:
    output = "\nExample Title\n[youtube] log line\n"
    assert extract_yt_dlp_title(output) == "Example Title"


def test_resolve_parse_video_binary_uses_path(monkeypatch: pytest.MonkeyPatch) -> None:
    monkeypatch.setattr("src.fetchers.local.which", lambda name: "/usr/local/bin/parse-video")
    assert resolve_parse_video_binary() == "/usr/local/bin/parse-video"


def test_resolve_parse_video_binary_falls_back_to_go_bin(
    monkeypatch: pytest.MonkeyPatch,
    tmp_path: Path,
) -> None:
    fallback = tmp_path / "go" / "bin" / "parse-video"
    fallback.parent.mkdir(parents=True, exist_ok=True)
    fallback.write_text("", encoding="utf-8")

    monkeypatch.setattr("src.fetchers.local.which", lambda name: None)
    monkeypatch.setattr("src.fetchers.local.Path.home", lambda: tmp_path)

    assert resolve_parse_video_binary() == str(fallback)


def test_fetch_yt_dlp_injects_extra_args(
    monkeypatch: pytest.MonkeyPatch, tmp_path: Path,
) -> None:
    """The extra_yt_dlp_args should appear in the command passed to _run_command."""
    from src.run_context import build_run_paths, ensure_run_dirs

    paths = build_run_paths(
        workdir=tmp_path,
        run_started_at="20260522194500",
        source_title="test",
    )
    ensure_run_dirs(paths)

    captured = []

    def fake_run_command(cmd):
        captured.append(cmd)
        return type("Completed", (), {"returncode": 0, "stdout": "", "stderr": ""})()

    monkeypatch.setattr("src.fetchers.local._run_command", fake_run_command)
    # Prevent _find_media_path from running by creating a file
    (paths.media_dir / "source.mp4").write_text("fake", encoding="utf-8")

    fetcher = LocalFetcher()
    fetcher._fetch_yt_dlp("https://www.youtube.com/watch?v=abc", "youtube", paths)

    assert len(captured) == 1
    cmd = captured[0]
    assert cmd[0] == "yt-dlp"
    assert "--js-runtimes" in cmd
    assert "node" in cmd
    assert "--remote-components" in cmd
    assert "ejs:github" in cmd


def test_fetch_yt_dlp_raises_on_empty_media_dir(
    monkeypatch: pytest.MonkeyPatch, tmp_path: Path,
) -> None:
    """When yt-dlp exits 0 but downloads nothing, the error should mention
    the real problem instead of a bare 'No media artifact found'."""
    from src.run_context import build_run_paths, ensure_run_dirs

    paths = build_run_paths(
        workdir=tmp_path,
        run_started_at="20260522194500",
        source_title="test",
    )
    ensure_run_dirs(paths)

    def fake_run_command(_command):
        return type("Completed", (), {"returncode": 0, "stdout": "", "stderr": ""})()

    monkeypatch.setattr("src.fetchers.local._run_command", fake_run_command)

    fetcher = LocalFetcher()
    with pytest.raises(RuntimeError, match="did not download any media file"):
        fetcher._fetch_yt_dlp("https://www.youtube.com/watch?v=abc", "youtube", paths)


# ---- _extract_parse_video_title ------------------------------------------

def test_extract_parse_video_title_from_top_level_title() -> None:
    assert _extract_parse_video_title({"title": "My Video"}) == "My Video"


def test_extract_parse_video_title_from_nested_data() -> None:
    assert (
        _extract_parse_video_title({"data": {"title": "Nested Title"}})
        == "Nested Title"
    )


def test_extract_parse_video_title_prefers_top_level() -> None:
    assert (
        _extract_parse_video_title({"title": "Top", "data": {"title": "Nested"}})
        == "Top"
    )


def test_extract_parse_video_title_returns_none_when_missing() -> None:
    assert _extract_parse_video_title({}) is None
    assert _extract_parse_video_title({"other": 1}) is None


def test_extract_parse_video_title_skips_empty_string() -> None:
    assert _extract_parse_video_title({"title": ""}) is None


# ---- _fetch_via_parse_video (douyin / bilibili) --------------------------

def test_fetch_via_parse_video_returns_fetch_result(
    monkeypatch: pytest.MonkeyPatch, tmp_path: Path,
) -> None:
    """Service-based parse-video should start server, call API, download, stop."""
    from src.run_context import build_run_paths, ensure_run_dirs

    paths = build_run_paths(
        workdir=tmp_path,
        run_started_at="20260522194500",
        source_title="test",
    )
    ensure_run_dirs(paths)

    # Create a fake media file so _find_media_path isn't needed
    # (download_file_with_retries writes to the target path)
    api_response = json.dumps({
        "code": 200,
        "data": {
            "title": "Bilibili Video",
            "video_url": "https://cdn.example.com/video.mp4",
        },
    })

    class FakeProcess:
        def terminate(self):
            pass
        def wait(self, timeout=None):
            pass
        def kill(self):
            pass

    monkeypatch.setattr("subprocess.Popen", lambda *a, **kw: FakeProcess())
    monkeypatch.setattr("src.fetchers.local._wait_for_service", lambda port, timeout_seconds: None)
    monkeypatch.setattr("src.fetchers.local._find_free_port", lambda: 19080)
    monkeypatch.setattr(
        "src.fetchers.local.resolve_parse_video_binary",
        lambda: "/usr/local/bin/parse-video",
    )

    # Mock urlopen to return the API response
    class FakeResponse:
        def __enter__(self):
            return self
        def __exit__(self, *a):
            pass
        def read(self):
            return api_response.encode("utf-8")

    monkeypatch.setattr("src.fetchers.local.urlopen", lambda url, timeout: FakeResponse())

    # Mock download_file_with_retries to actually create the file
    def fake_download(url, target, retries=3, headers=None):
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_text("fake media content", encoding="utf-8")
    monkeypatch.setattr("src.fetchers.local.download_file_with_retries", fake_download)

    fetcher = LocalFetcher()
    result = fetcher._fetch_via_parse_video(
        "https://www.bilibili.com/video/BV1xx", "bilibili", paths,
    )

    assert result.source_url == "https://www.bilibili.com/video/BV1xx"
    assert result.title == "Bilibili Video"
    assert result.media_path.exists()


def test_fetch_via_parse_video_raises_when_no_video_url(
    monkeypatch: pytest.MonkeyPatch, tmp_path: Path,
) -> None:
    from src.run_context import build_run_paths, ensure_run_dirs

    paths = build_run_paths(
        workdir=tmp_path,
        run_started_at="20260522194500",
        source_title="test",
    )
    ensure_run_dirs(paths)

    class FakeProcess:
        def terminate(self):
            pass
        def wait(self, timeout=None):
            pass
        def kill(self):
            pass

    monkeypatch.setattr("subprocess.Popen", lambda *a, **kw: FakeProcess())
    monkeypatch.setattr("src.fetchers.local._wait_for_service", lambda port, timeout_seconds: None)
    monkeypatch.setattr("src.fetchers.local._find_free_port", lambda: 19080)
    monkeypatch.setattr(
        "src.fetchers.local.resolve_parse_video_binary",
        lambda: "/usr/local/bin/parse-video",
    )

    class FakeResponse:
        def __enter__(self):
            return self
        def __exit__(self, *a):
            pass
        def read(self):
            return b'{"code": 500, "msg": "error"}'

    monkeypatch.setattr("src.fetchers.local.urlopen", lambda url, timeout: FakeResponse())

    fetcher = LocalFetcher()
    with pytest.raises(RuntimeError, match="missing video_url"):
        fetcher._fetch_via_parse_video(
            "https://www.bilibili.com/video/BV1xx", "bilibili", paths,
        )
