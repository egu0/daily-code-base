from pathlib import Path

from src.fetchers.stub import StubFetcher
from src.run_context import build_run_paths, ensure_run_dirs


def test_stub_fetcher_writes_media_info_and_media_file(tmp_path: Path) -> None:
    paths = build_run_paths(
        workdir=tmp_path,
        run_started_at="20260522-194500",
        source_title="stub title",
    )
    ensure_run_dirs(paths)
    fetcher = StubFetcher()

    result = fetcher.fetch("https://example.com/watch?v=1", "youtube", paths)

    assert result.source_url == "https://example.com/watch?v=1"
    assert result.title == "Stub title for youtube"
    assert paths.media_info_json.exists()
    assert result.media_path.exists()
