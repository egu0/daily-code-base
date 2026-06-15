from __future__ import annotations

import argparse
import logging
from pathlib import Path

from .fetchers.local import LocalFetcher
from .pipeline import PipelineConfig, run_pipeline
from .transcriber import TranscriptionConfig

logger = logging.getLogger(__name__)


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        prog="video-summarizer",
        description="Fetch supported video media and prepare transcript artifacts for downstream summarization.",
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument("--url", required=True, help="Video URL")
    parser.add_argument(
        "--asr-backend",
        choices=("mlx-audio-stt", "faster-whisper"),
        default="mlx-audio-stt",
        help="ASR backend",
    )
    parser.add_argument(
        "--segment-threshold-minutes",
        type=int,
        default=1,
        help="Segment media longer than this threshold before transcription",
    )
    parser.add_argument(
        "--faster-whisper-model", default="small", help="faster-whisper model name"
    )
    parser.add_argument(
        "--mlx-audio-stt-model",
        default="mlx-community/Qwen3-ASR-0.6B-4bit",
        help="mlx-audio-stt model name",
    )
    return parser


def main() -> None:
    parser = build_parser()
    args = parser.parse_args()

    paths = run_pipeline(
        PipelineConfig(
            url=args.url,
            workdir=Path("data"),
            fetcher=LocalFetcher(),
            transcription_config=TranscriptionConfig(
                backend=args.asr_backend,
                segment_threshold_minutes=args.segment_threshold_minutes,
                faster_whisper_model_name=args.faster_whisper_model,
                mlx_audio_stt_model_name=args.mlx_audio_stt_model,
            ),
        )
    )
    logger.info("Run directory: %s", paths.root)


if __name__ == "__main__":
    try:
        main()
    except Exception as exc:
        logger.exception("Pipeline failed")
        raise SystemExit(1) from exc
