import os
import pytest


@pytest.fixture(autouse=True)
def _allow_any_workspace_for_tests(monkeypatch):
    """Set workspace to root so tmp_path‑based tests pass the boundary check."""
    monkeypatch.setenv("APPLY_PATCH_WORKSPACE", "/")
