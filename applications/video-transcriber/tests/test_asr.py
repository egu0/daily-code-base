from __future__ import annotations

import wave
from pathlib import Path

from src.asr.faster_whisper import FasterWhisperTranscriber
from src.asr.mlx_audio_stt import MlxAudioSttTranscriber, _normalize_language


# ---- _normalize_language --------------------------------------------------

def test_normalize_language_returns_none_for_none() -> None:
    assert _normalize_language(None) is None


def test_normalize_language_returns_string_for_string() -> None:
    assert _normalize_language("zh") == "zh"


def test_normalize_language_casts_non_string_via_str() -> None:
    assert _normalize_language(42) == "42"


def test_normalize_language_handles_empty_string() -> None:
    assert _normalize_language("") == ""


# ---- FasterWhisperTranscriber --------------------------------------------

def test_faster_whisper_transcriber_has_correct_name() -> None:
    t = FasterWhisperTranscriber()
    assert t.name == "faster-whisper"


def test_faster_whisper_transcriber_stores_model_name() -> None:
    t = FasterWhisperTranscriber(model_name="medium")
    assert t.model_name == "medium"
    assert t._model is None  # lazy-loaded


def test_faster_whisper_transcriber_segment_offset_uses_wav_duration(
    monkeypatch, tmp_path: Path,
) -> None:
    """The recent refactor switched segment offset from media_duration_seconds
    (ffprobe) to wav_duration (wave header).  Verify the offset is accumulated
    from wav_duration, not from a stale mock of media_duration_seconds."""
    # Create two WAV segments with known durations
    seg1 = tmp_path / "seg1.wav"
    seg2 = tmp_path / "seg2.wav"
    for seg_path, num_frames in [(seg1, 16000), (seg2, 32000)]:
        with wave.open(str(seg_path), "wb") as wf:
            wf.setnchannels(1)
            wf.setsampwidth(2)
            wf.setframerate(16000)
            wf.writeframes(b"\x00\x00" * num_frames)
    # seg1 = 1.0s, seg2 = 2.0s

    class FakeSegment:
        start = 0.0
        end = 1.0
        text = "hello"

    class FakeInfo:
        language = "en"
        language_probability = 0.99

    class FakeModel:
        def transcribe(self, audio_path):
            return [FakeSegment], FakeInfo()

    transcriber = FasterWhisperTranscriber()
    transcriber._model = FakeModel()

    result = transcriber.transcribe_segments([seg1, seg2])

    assert len(result["segments"]) == 2
    # First segment: offset 0.0
    assert result["segments"][0]["start"] == 0.0
    assert result["segments"][0]["end"] == 1.0
    # Second segment: offset = wav_duration(seg1) = 1.0
    assert result["segments"][1]["start"] == 1.0
    assert result["segments"][1]["end"] == 2.0


# ---- MlxAudioSttTranscriber ----------------------------------------------

def test_mlx_audio_stt_transcriber_has_correct_name() -> None:
    t = MlxAudioSttTranscriber()
    assert t.name == "mlx-audio-stt"


def test_mlx_audio_stt_transcriber_stores_model_name() -> None:
    t = MlxAudioSttTranscriber(model_name="custom-model")
    assert t.model_name == "custom-model"
    assert t._model is None  # lazy-loaded
