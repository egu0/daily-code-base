from __future__ import annotations

from pathlib import Path
from uuid import uuid4

from .models import RunPaths
from .utils import sanitize_for_path


def _build_paths_from_name(workdir: Path, run_name: str) -> RunPaths:
    root = workdir.expanduser().resolve() / run_name
    media_dir = root / "media"
    audio_dir = root / "audio"
    segment_dir = audio_dir / "segments"
    data_dir = root / "data"
    log_dir = root / "logs"
    return RunPaths(
        root=root,
        media_dir=media_dir,
        audio_dir=audio_dir,
        segment_dir=segment_dir,
        data_dir=data_dir,
        log_dir=log_dir,
        media_info_json=data_dir / "media_info.json",
        transcript_json=data_dir / "transcript.json",
        fetch_log=log_dir / "fetch.log",
        run_log=log_dir / "run.log",
        run_started_at=run_name[:14],
    )


def _resolve_available_name(workdir: Path, base_name: str) -> str:
    root = workdir.expanduser().resolve()
    candidate = base_name
    index = 2
    while (root / candidate).exists():
        candidate = f"{base_name}-{index}"
        index += 1
    return candidate


def build_run_paths(workdir: Path, run_started_at: str, source_title: str) -> RunPaths:
    base_name = f"{run_started_at}-{sanitize_for_path(source_title[:10])}"
    return _build_paths_from_name(workdir, _resolve_available_name(workdir, base_name))


def ensure_run_dirs(paths: RunPaths) -> None:
    for path in (
        paths.root,
        paths.media_dir,
        paths.audio_dir,
        paths.segment_dir,
        paths.data_dir,
        paths.log_dir,
    ):
        path.mkdir(parents=True, exist_ok=True)


def build_staging_run_paths(workdir: Path, run_started_at: str) -> RunPaths:
    staging_name = f"{run_started_at}-__staging__-{uuid4().hex[:8]}"
    return _build_paths_from_name(workdir, staging_name)


def finalize_run_paths(paths: RunPaths, workdir: Path, source_title: str) -> RunPaths:
    final_paths = build_run_paths(workdir, paths.run_started_at, source_title)
    if paths.root == final_paths.root:
        return paths
    paths.root.parent.mkdir(parents=True, exist_ok=True)
    paths.root.rename(final_paths.root)
    return final_paths


def load_run_paths(root: Path) -> RunPaths:
    resolved_root = root.expanduser().resolve()
    return _build_paths_from_name(resolved_root.parent, resolved_root.name)
