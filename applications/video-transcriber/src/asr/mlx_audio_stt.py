from __future__ import annotations

import logging
from collections import Counter
from pathlib import Path
from typing import Any

from .base import AsrTranscriber
from ..audio import wav_duration

logger = logging.getLogger(__name__)


class MlxAudioSttTranscriber(AsrTranscriber):
    name = "mlx-audio-stt"

    def __init__(self, model_name: str = "mlx-community/Qwen3-ASR-0.6B-4bit") -> None:
        self.model_name = model_name
        self._model: Any | None = None

    def transcribe_file(self, audio_path: Path) -> dict[str, Any]:
        logger.info("Transcribing file: %s", audio_path)
        text, language = self._transcribe(audio_path)
        duration = wav_duration(audio_path)
        segments = [{"start": 0.0, "end": duration, "text": text}] if text else []
        logger.info("Transcription result: %d chars, language=%s", len(text), language)
        return {
            "segments": segments,
            "detected_language": language,
            "language_probability": None,
        }

    def transcribe_segments(self, audio_segments: list[Path]) -> dict[str, Any]:
        logger.info("Transcribing %d segments ...", len(audio_segments))
        combined_segments: list[dict[str, Any]] = []
        languages: list[str] = []
        offset_seconds = 0.0

        for i, audio_segment in enumerate(audio_segments):
            text, language = self._transcribe(audio_segment)
            if language:
                languages.append(language)
            duration = wav_duration(audio_segment)
            if text:
                combined_segments.append(
                    {
                        "start": offset_seconds,
                        "end": offset_seconds + duration,
                        "text": text,
                    }
                )
            offset_seconds += duration
            logger.debug("Segment %d/%d transcribed: %d chars", i + 1, len(audio_segments), len(text))

        language = Counter(languages).most_common(1)[0][0] if languages else None
        total_chars = sum(len(segment["text"]) for segment in combined_segments)
        logger.info("Segments transcription completed: %d chars, language=%s", total_chars, language)
        return {
            "segments": combined_segments,
            "detected_language": language,
            "language_probability": None,
        }

    def _transcribe(self, audio_path: Path) -> tuple[str, str | None]:
        try:
            from mlx_audio.stt.generate import generate_transcription
        except ImportError as exc:
            raise RuntimeError(
                "mlx-audio is required for --asr-backend=mlx-audio-stt. Install dependencies with `uv sync`."
            ) from exc

        model = self._load_model()
        output_path = audio_path.with_suffix(".mlx-audio-stt")
        transcription = generate_transcription(
            model=model,
            audio=str(audio_path),
            output_path=str(output_path),
            format="txt",
            verbose=False,
        )
        # Clean up temporary output file created by mlx-audio
        if output_path.exists():
            output_path.unlink()
        text = getattr(transcription, "text", str(transcription)).strip()
        language = _normalize_language(getattr(transcription, "language", None))
        return text, language

    def _load_model(self) -> Any:
        if self._model is None:
            logger.info("Loading mlx-audio-stt model: %s", self.model_name)
            try:
                from mlx_audio.stt.utils import load_model
            except ImportError as exc:
                raise RuntimeError(
                    "mlx-audio is required for --asr-backend=mlx-audio-stt. Install dependencies with `uv sync`."
                ) from exc
            self._model = load_model(self.model_name)
            logger.info("Model loaded successfully")
        return self._model


def _normalize_language(language: Any) -> str | None:
    if language is None:
        return None
    return str(language)
