from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path


@dataclass(slots=True)
class RunPaths:
    root: Path
    media_dir: Path
    audio_dir: Path
    segment_dir: Path
    data_dir: Path
    log_dir: Path
    media_info_json: Path
    transcript_json: Path
    fetch_log: Path
    run_log: Path
    run_started_at: str


@dataclass(slots=True)
class FetchResult:
    source_url: str
    media_path: Path
    title: str | None = None
