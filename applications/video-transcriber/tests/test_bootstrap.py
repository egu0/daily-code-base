from pathlib import Path


def test_project_tree_exists() -> None:
    assert Path("pyproject.toml").exists()
    assert Path("src").exists()

