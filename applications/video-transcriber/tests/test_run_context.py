from pathlib import Path

from src.run_context import build_run_paths, build_staging_run_paths, ensure_run_dirs, load_run_paths


def test_build_run_paths_creates_expected_filenames(tmp_path: Path) -> None:
    paths = build_run_paths(
        workdir=tmp_path,
        run_started_at="20260522194500",
        source_title="Agent Skills到底是什么，一个动画彻底搞懂！",
    )
    assert paths.media_info_json.name == "media_info.json"
    assert paths.transcript_json.name == "transcript.json"


def test_ensure_run_dirs_creates_directories(tmp_path: Path) -> None:
    paths = build_run_paths(
        workdir=tmp_path,
        run_started_at="20260522194500",
        source_title="test title",
    )
    ensure_run_dirs(paths)
    assert paths.media_dir.exists()
    assert paths.data_dir.exists()


def test_build_run_paths_uses_timestamp_and_title_in_root(tmp_path: Path) -> None:
    paths = build_run_paths(
        workdir=tmp_path,
        run_started_at="20260522194500",
        source_title="A/B:C*D?",
    )
    assert paths.root.name == "20260522194500-A-B-C-D"


def test_load_run_paths_rehydrates_expected_locations(tmp_path: Path) -> None:
    paths = build_run_paths(
        workdir=tmp_path,
        run_started_at="20260522194500",
        source_title="Example title",
    )
    loaded = load_run_paths(paths.root)
    assert loaded.media_info_json == paths.media_info_json
    assert loaded.transcript_json == paths.transcript_json
    assert loaded.run_started_at == "20260522194500"


def test_build_staging_run_paths_uses_uuid_without_polluting_final_name(tmp_path: Path) -> None:
    paths = build_staging_run_paths(tmp_path, "20260522194500")
    assert paths.root.name.startswith("20260522194500-__staging__-")
    assert len(paths.root.name.split("-")[-1]) == 8


def test_build_run_paths_uses_numeric_suffix_on_conflict(tmp_path: Path) -> None:
    first = build_run_paths(tmp_path, "20260522194500", "ExampleX")
    first.root.mkdir(parents=True)
    second = build_run_paths(tmp_path, "20260522194500", "ExampleX")
    assert first.root.name == "20260522194500-ExampleX"
    assert second.root.name == "20260522194500-ExampleX-2"
