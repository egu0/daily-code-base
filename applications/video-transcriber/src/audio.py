from __future__ import annotations

import logging
from pathlib import Path
import wave
import subprocess

logger = logging.getLogger(__name__)


def target_audio_path(media_path: Path) -> Path:
    return media_path.with_suffix(".wav")

def wav_duration(wav_path: Path) -> float:
    with wave.open(str(wav_path), "rb") as wf:
        return wf.getnframes() / wf.getframerate()



def convert_to_transcription_audio(media_path: Path, output_dir: Path) -> Path:
    output_dir.mkdir(parents=True, exist_ok=True)
    audio_path = output_dir / "source.wav"
    logger.info("Converting audio to 16kHz mono WAV ...")
    _run_ffmpeg(
        [
            "ffmpeg",
            "-y",
            "-i",
            str(media_path),
            "-vn",
            "-ac",
            "1",
            "-ar",
            "16000",
            str(audio_path),
        ]
    )
    logger.info("Audio conversion completed: %s", audio_path)
    return audio_path


def segment_audio(audio_path: Path, segment_dir: Path, segment_minutes: int) -> list[Path]:
    segment_dir.mkdir(parents=True, exist_ok=True)
    output_pattern = segment_dir / "part-%03d.wav"
    logger.info("Segmenting audio into %d-minute chunks ...", segment_minutes)
    _run_ffmpeg(
        [
            "ffmpeg",
            "-y",
            "-i",
            str(audio_path),
            "-f",
            "segment",
            "-segment_time",
            str(segment_minutes * 60),
            "-c",
            "copy",
            str(output_pattern),
        ]
    )
    segments = sorted(segment_dir.glob("part-*.wav"))
    logger.info("Audio segmented into %d parts", len(segments))
    return segments


def media_duration_seconds(media_path: Path) -> float:
    completed = subprocess.run(
        [
            "ffprobe",
            "-v",
            "error",
            "-show_entries",
            "format=duration",
            "-of",
            "default=noprint_wrappers=1:nokey=1",
            str(media_path),
        ],
        check=False,
        capture_output=True,
        text=True,
    )
    if completed.returncode != 0:
        raise RuntimeError(
            "ffprobe command failed:\n"
            f"stdout:\n{completed.stdout}\n"
            f"stderr:\n{completed.stderr}"
        )
    duration = float(completed.stdout.strip())
    logger.info("Media duration: %.1f seconds", duration)
    return duration


def _run_ffmpeg(command: list[str]) -> None:
    completed = subprocess.run(
        command,
        check=False,
        capture_output=True,
        text=True,
    )
    if completed.returncode != 0:
        logger.error("ffmpeg command failed: %s", " ".join(command))
        raise RuntimeError(
            "ffmpeg command failed:\n"
            f"stdout:\n{completed.stdout}\n"
            f"stderr:\n{completed.stderr}"
        )
