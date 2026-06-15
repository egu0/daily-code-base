from __future__ import annotations

import logging
from dataclasses import dataclass
from pathlib import Path
from typing import Any

from .asr import FasterWhisperTranscriber, MlxAudioSttTranscriber
from .audio import convert_to_transcription_audio, media_duration_seconds, segment_audio

logger = logging.getLogger(__name__)


@dataclass(slots=True)
class TranscriptionConfig:
    backend: str = "mlx-audio-stt"
    segment_threshold_minutes: int = 1
    faster_whisper_model_name: str = "small"
    mlx_audio_stt_model_name: str = "mlx-community/Qwen3-ASR-0.6B-4bit"


def build_transcript_payload(
    *, segments: list[dict[str, Any]] | None = None
) -> dict[str, Any]:
    return {
        "segments": segments or [],
        "detected_language": None,
        "language_probability": None,
    }


def build_asr_transcriber(config: TranscriptionConfig):
    if config.backend == MlxAudioSttTranscriber.name:
        logger.info(
            "Using ASR backend: mlx-audio-stt (model=%s)",
            config.mlx_audio_stt_model_name,
        )
        return MlxAudioSttTranscriber(model_name=config.mlx_audio_stt_model_name)
    if config.backend == FasterWhisperTranscriber.name:
        logger.info(
            "Using ASR backend: faster-whisper (model=%s)",
            config.faster_whisper_model_name,
        )
        return FasterWhisperTranscriber(model_name=config.faster_whisper_model_name)
    raise ValueError(f"Unsupported ASR backend: {config.backend}")


def transcribe_media(
    media_path: Path,
    run_paths,
    config: TranscriptionConfig | None = None,
) -> dict[str, Any]:
    if media_path.suffix == ".txt":
        logger.info("Reading transcription from text file: %s", media_path)
        content = media_path.read_text(encoding="utf-8")
        return build_transcript_payload(
            segments=[{"start": 0.0, "end": 0.0, "text": content}],
        )

    active_config = config or TranscriptionConfig()
    audio_path = convert_to_transcription_audio(media_path, run_paths.audio_dir)
    duration_seconds = media_duration_seconds(audio_path)
    transcriber = build_asr_transcriber(active_config)
    threshold_seconds = active_config.segment_threshold_minutes * 60

    if duration_seconds > threshold_seconds:
        logger.info(
            "Audio duration (%.1fs) exceeds threshold (%ds), segmenting ...",
            duration_seconds,
            threshold_seconds,
        )
        segments = segment_audio(
            audio_path,
            run_paths.segment_dir,
            active_config.segment_threshold_minutes,
        )
        return transcriber.transcribe_segments(segments)
    return transcriber.transcribe_file(audio_path)
