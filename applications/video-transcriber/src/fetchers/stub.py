from __future__ import annotations

import logging

from ..models import FetchResult, RunPaths
from ..utils import write_json

logger = logging.getLogger(__name__)


class StubFetcher:
    def fetch(self, url: str, platform: str, run_paths: RunPaths) -> FetchResult:
        logger.info("Stub fetch for platform=%s url=%s", platform, url)
        media_path = run_paths.media_dir / "source.txt"
        media_path.write_text(
            f"stub transcript seed for {platform}: {url}",
            encoding="utf-8",
        )
        title = f"Stub title for {platform}"
        write_json(run_paths.media_info_json, {"source_url": url, "title": title})
        run_paths.fetch_log.write_text("stub fetch completed\n", encoding="utf-8")
        return FetchResult(source_url=url, media_path=media_path, title=title)
