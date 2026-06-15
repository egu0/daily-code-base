from __future__ import annotations

from typing import Protocol

from ..models import FetchResult, RunPaths


class Fetcher(Protocol):
    def fetch(self, url: str, platform: str, run_paths: RunPaths) -> FetchResult:
        ...

