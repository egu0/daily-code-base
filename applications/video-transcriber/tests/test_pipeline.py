import json
from pathlib import Path

from src.fetchers.stub import StubFetcher
from src.pipeline import PipelineConfig, run_pipeline


def test_run_pipeline_writes_output_json(tmp_path: Path) -> None:
    captured: dict[str, Path] = {}

    def fake_transcribe(path: Path, run_paths) -> dict[str, object]:
        captured["media_path"] = path
        captured["transcript_json"] = run_paths.transcript_json
        return {
            "segments": [],
            "detected_language": None,
            "language_probability": None,
        }

    paths = run_pipeline(
        PipelineConfig(
            url="https://www.youtube.com/watch?v=abc",
            workdir=tmp_path,
            fetcher=StubFetcher(),
            transcribe_func=fake_transcribe,
        )
    )

    assert paths.transcript_json.exists()
    assert paths.media_info_json.exists()
    assert captured["media_path"].exists()
    assert captured["transcript_json"] == paths.transcript_json

    media_info = json.loads(paths.media_info_json.read_text(encoding="utf-8"))
    assert media_info["title"] == "Stub title for youtube"
