from __future__ import annotations

import logging
from collections import Counter
from pathlib import Path
from typing import Any

from .base import AsrTranscriber
from ..audio import wav_duration

logger = logging.getLogger(__name__)


class FasterWhisperTranscriber(AsrTranscriber):
    name = "faster-whisper"

    def __init__(self, model_name: str = "base") -> None:
        self.model_name = model_name
        self._model = None

    def transcribe_file(self, audio_path: Path) -> dict[str, Any]:
        logger.info("Transcribing file: %s", audio_path)
        model = self._load_model()
        segments, info = model.transcribe(str(audio_path))
        items = [
            {
                "start": segment.start,
                "end": segment.end,
                "text": segment.text.strip(),
            }
            for segment in segments
            if segment.text.strip()
        ]
        logger.info("Transcription result: %d chars, language=%s, probability=%.2f",
                     sum(len(item["text"]) for item in items),
                     info.language, info.language_probability)
        return {
            "segments": items,
            "detected_language": info.language,
            "language_probability": info.language_probability,
        }

    def transcribe_segments(self, audio_segments: list[Path]) -> dict[str, Any]:
        logger.info("Transcribing %d segments ...", len(audio_segments))
        model = self._load_model()
        combined: list[dict[str, Any]] = []
        languages: list[str] = []
        probabilities: list[float] = []
        offset = 0.0

        for i, audio_segment in enumerate(audio_segments):
            segments, info = model.transcribe(str(audio_segment))
            if info.language:
                languages.append(info.language)
            if info.language_probability is not None:
                probabilities.append(info.language_probability)
            for segment in segments:
                text = segment.text.strip()
                if not text:
                    continue
                combined.append(
                    {
                        "start": segment.start + offset,
                        "end": segment.end + offset,
                        "text": text,
                    }
                )
            offset += wav_duration(audio_segment)
            logger.debug("Segment %d/%d transcribed", i + 1, len(audio_segments))

        language = Counter(languages).most_common(1)[0][0] if languages else None
        probability = sum(probabilities) / len(probabilities) if probabilities else None
        total_chars = sum(len(item["text"]) for item in combined)
        logger.info("Segments transcription completed: %d chars, language=%s, probability=%.2f",
                     total_chars, language, probability if probability else 0)
        return {
            "segments": combined,
            "detected_language": language,
            "language_probability": probability,
        }

    def _load_model(self):
        if self._model is None:
            logger.info("Loading faster-whisper model: %s", self.model_name)
            from faster_whisper import WhisperModel

            self._model = WhisperModel(self.model_name)
            logger.info("Model loaded successfully")
        return self._model

