import pytest
from pathlib import Path

from src.transcriber import (
    TranscriptionConfig,
    build_asr_transcriber,
    build_transcript_payload,
    transcribe_media,
)


def test_build_transcript_payload_from_text() -> None:
    payload = build_transcript_payload(
        segments=[{"start": 0.0, "end": 1.0, "text": "hello world"}],
    )
    assert len(payload["segments"]) == 1
    assert payload["segments"][0]["text"] == "hello world"


def test_transcribe_media_uses_segments_for_long_audio(
    monkeypatch,
    tmp_path: Path,
) -> None:
    calls: list[str] = []

    class FakeTranscriber:
        def transcribe_file(self, audio_path: Path) -> dict[str, object]:
            calls.append("file")
            return build_transcript_payload(
                segments=[{"start": 0.0, "end": 1.0, "text": "short transcript"}],
            )

        def transcribe_segments(self, audio_segments: list[Path]) -> dict[str, object]:
            calls.append("segments")
            return build_transcript_payload(
                segments=[{"start": 0.0, "end": 1.0, "text": "long transcript"}],
            )

    monkeypatch.setattr("src.transcriber.build_asr_transcriber", lambda config: FakeTranscriber())
    monkeypatch.setattr("src.transcriber.convert_to_transcription_audio", lambda media_path, output_dir: output_dir / "source.wav")
    monkeypatch.setattr("src.transcriber.media_duration_seconds", lambda path: 181.0)
    monkeypatch.setattr(
        "src.transcriber.segment_audio",
        lambda audio_path, segment_dir, segment_minutes: [segment_dir / "part-001.wav"],
    )

    payload = transcribe_media(
        tmp_path / "source.mp4",
        run_paths=type("RunPaths", (), {"audio_dir": tmp_path / "audio", "segment_dir": tmp_path / "audio" / "segments"})(),
        config=TranscriptionConfig(segment_threshold_minutes=3),
    )

    assert len(payload["segments"]) == 1
    assert payload["segments"][0]["text"] == "long transcript"
    assert calls == ["segments"]


def test_transcription_config_defaults_to_mlx_audio_stt() -> None:
    config = TranscriptionConfig()
    assert config.backend == "mlx-audio-stt"
    assert config.mlx_audio_stt_model_name == "mlx-community/Qwen3-ASR-0.6B-4bit"


def test_transcribe_media_reads_txt_file_directly(tmp_path: Path) -> None:
    txt_file = tmp_path / "source.txt"
    txt_file.write_text("hello world", encoding="utf-8")

    payload = transcribe_media(
        txt_file,
        run_paths=type("RunPaths", (), {"audio_dir": tmp_path / "audio"})(),
    )

    assert len(payload["segments"]) == 1
    assert payload["segments"][0]["text"] == "hello world"
    assert payload["segments"][0]["start"] == 0.0
    assert payload["segments"][0]["end"] == 0.0


def test_transcribe_media_short_audio_uses_transcribe_file(
    monkeypatch, tmp_path: Path,
) -> None:
    """Audio under the segment threshold should call transcribe_file, not segments."""
    calls: list[str] = []

    class FakeTranscriber:
        def transcribe_file(self, audio_path: Path):
            calls.append("file")
            return build_transcript_payload(
                segments=[{"start": 0.0, "end": 10.0, "text": "short"}],
            )

        def transcribe_segments(self, audio_segments):
            calls.append("segments")
            return build_transcript_payload()

    monkeypatch.setattr(
        "src.transcriber.build_asr_transcriber", lambda config: FakeTranscriber()
    )
    monkeypatch.setattr(
        "src.transcriber.convert_to_transcription_audio",
        lambda media_path, output_dir: output_dir / "source.wav",
    )
    monkeypatch.setattr(
        "src.transcriber.media_duration_seconds", lambda path: 30.0  # 30s < 60s threshold
    )

    payload = transcribe_media(
        tmp_path / "source.mp4",
        run_paths=type(
            "RunPaths",
            (),
            {"audio_dir": tmp_path / "audio", "segment_dir": tmp_path / "audio" / "segments"},
        )(),
        config=TranscriptionConfig(segment_threshold_minutes=1),
    )

    assert payload["segments"][0]["text"] == "short"
    assert calls == ["file"]


# ---- build_asr_transcriber -----------------------------------------------

def test_build_asr_transcriber_creates_mlx_backend() -> None:
    config = TranscriptionConfig(backend="mlx-audio-stt")
    t = build_asr_transcriber(config)
    assert t.name == "mlx-audio-stt"


def test_build_asr_transcriber_creates_faster_whisper_backend() -> None:
    config = TranscriptionConfig(backend="faster-whisper")
    t = build_asr_transcriber(config)
    assert t.name == "faster-whisper"


def test_build_asr_transcriber_passes_model_name_to_backend() -> None:
    config = TranscriptionConfig(
        backend="faster-whisper",
        faster_whisper_model_name="large-v3",
    )
    t = build_asr_transcriber(config)
    assert t.model_name == "large-v3"


def test_build_asr_transcriber_rejects_unsupported_backend() -> None:
    config = TranscriptionConfig(backend="nonexistent")
    with pytest.raises(ValueError, match="Unsupported ASR backend"):
        build_asr_transcriber(config)
