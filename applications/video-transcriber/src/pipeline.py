from __future__ import annotations

import logging
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from typing import Callable

from .logging_setup import setup_logging
from .platforms import detect_platform
from .run_context import build_staging_run_paths, ensure_run_dirs, finalize_run_paths
from .transcriber import TranscriptionConfig, transcribe_media
from .utils import write_json

logger = logging.getLogger(__name__)


@dataclass(slots=True)
class PipelineConfig:
    url: str
    workdir: Path
    fetcher: object
    transcribe_func: Callable[[Path, object], dict[str, object]] = transcribe_media
    transcription_config: TranscriptionConfig | None = None


def run_pipeline(config: PipelineConfig):
    platform = detect_platform(config.url)
    run_started_at = datetime.now().strftime("%Y%m%d%H%M%S")
    paths = build_staging_run_paths(config.workdir, run_started_at)
    ensure_run_dirs(paths)
    setup_logging(paths.run_log)

    logger.info("Starting pipeline: url=%s, platform=%s, backend=%s",
                 config.url, platform,
                 getattr(config.transcription_config, 'backend', 'none'))

    logger.info("Fetching media from %s ...", platform)
    try:
        fetch_result = config.fetcher.fetch(config.url, platform, paths)
    except Exception:
        logger.exception("Media fetch failed")
        raise
    logger.info("Fetch completed: title=%s", fetch_result.title)

    media_relative_path = fetch_result.media_path.relative_to(paths.root)
    paths = finalize_run_paths(paths, config.workdir, fetch_result.title or "untitled")
    ensure_run_dirs(paths)
    fetch_result.media_path = paths.root / media_relative_path

    logger.info("Starting transcription ...")
    try:
        if config.transcription_config is None:
            transcript_payload = config.transcribe_func(fetch_result.media_path, paths)
        else:
            transcript_payload = config.transcribe_func(
                fetch_result.media_path,
                paths,
                config.transcription_config,
            )
    except Exception:
        logger.exception("Transcription failed")
        raise
    segment_count = len(transcript_payload.get("segments", []))
    logger.info("Transcription completed: %d segments", segment_count)

    write_json(
        paths.media_info_json,
        {
            "source_url": fetch_result.source_url,
            "title": fetch_result.title,
        },
    )
    write_json(paths.transcript_json, transcript_payload)
    logger.info("Run directory: %s", paths.root)
    return paths
