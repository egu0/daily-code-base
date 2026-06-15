import wave
from pathlib import Path

from src.audio import convert_to_transcription_audio, target_audio_path, wav_duration


def test_target_audio_path_is_wav(tmp_path: Path) -> None:
    assert target_audio_path(tmp_path / "source.mp4").suffix == ".wav"


def test_convert_to_transcription_audio_writes_wav(tmp_path: Path) -> None:
    source = tmp_path / "source.wav"
    with wave.open(str(source), "wb") as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(16000)
        wav_file.writeframes(b"\x00\x00" * 1600)

    converted = convert_to_transcription_audio(source, tmp_path / "audio")

    assert converted.exists()
    assert converted.suffix == ".wav"


def test_wav_duration_returns_correct_seconds(tmp_path: Path) -> None:
    source = tmp_path / "test.wav"
    sample_rate = 16000
    num_frames = 8000
    with wave.open(str(source), "wb") as wav_file:
        wav_file.setnchannels(1)
        wav_file.setsampwidth(2)
        wav_file.setframerate(sample_rate)
        wav_file.writeframes(b"\x00\x00" * num_frames)

    duration = wav_duration(source)
    assert duration == 0.5
