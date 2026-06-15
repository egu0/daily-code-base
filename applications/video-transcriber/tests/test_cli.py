import pytest

from src.cli import build_parser, main
from src.fetchers.local import LocalFetcher


def test_cli_parser_accepts_url_and_asr_options() -> None:
    parser = build_parser()
    args = parser.parse_args(
        [
            "--url",
            "https://www.youtube.com/watch?v=abc",
            "--segment-threshold-minutes",
            "5",
            "--faster-whisper-model",
            "small",
        ]
    )

    assert args.url == "https://www.youtube.com/watch?v=abc"
    assert args.segment_threshold_minutes == 5
    assert args.faster_whisper_model == "small"


def test_cli_parser_defaults_to_mlx_audio_stt() -> None:
    parser = build_parser()
    args = parser.parse_args(["--url", "https://www.youtube.com/watch?v=abc"])

    assert args.asr_backend == "mlx-audio-stt"
    assert args.mlx_audio_stt_model == "mlx-community/Qwen3-ASR-0.6B-4bit"


def test_cli_has_no_workdir_argument() -> None:
    parser = build_parser()
    args = parser.parse_args(["--url", "https://www.youtube.com/watch?v=abc"])
    assert not hasattr(args, "workdir")


def test_cli_has_no_fetcher_argument() -> None:
    parser = build_parser()

    with pytest.raises(SystemExit):
        parser.parse_args(
            [
                "--url",
                "https://www.youtube.com/watch?v=abc",
                "--fetcher",
                "stub",
            ]
        )


def test_main_uses_local_fetcher(monkeypatch) -> None:
    captured_configs = []

    def fake_run_pipeline(config):
        captured_configs.append(config)
        return type("Paths", (), {"root": "/tmp/video-summarizer/run"})()

    monkeypatch.setattr(
        "sys.argv",
        ["video-summarizer", "--url", "https://www.youtube.com/watch?v=abc"],
    )
    monkeypatch.setattr("src.cli.run_pipeline", fake_run_pipeline)

    main()

    assert isinstance(captured_configs[0].fetcher, LocalFetcher)
