from __future__ import annotations

from abc import ABC, abstractmethod
from pathlib import Path
from typing import Any


class AsrTranscriber(ABC):
    name: str

    @abstractmethod
    def transcribe_file(self, audio_path: Path) -> dict[str, Any]:
        raise NotImplementedError

    @abstractmethod
    def transcribe_segments(self, audio_segments: list[Path]) -> dict[str, Any]:
        raise NotImplementedError

