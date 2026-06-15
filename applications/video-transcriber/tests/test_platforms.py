import pytest

from src.platforms import detect_platform


def test_detect_platform_youtube() -> None:
    assert detect_platform("https://www.youtube.com/watch?v=abc") == "youtube"


def test_detect_platform_bilibili() -> None:
    assert detect_platform("https://www.bilibili.com/video/BV1xx") == "bilibili"


def test_detect_platform_douyin() -> None:
    assert detect_platform("https://www.douyin.com/video/123") == "douyin"


def test_detect_platform_youtube_short_url() -> None:
    assert detect_platform("https://youtu.be/abc123") == "youtube"


def test_detect_platform_bilibili_short_url() -> None:
    assert detect_platform("https://b23.tv/abc123") == "bilibili"


def test_detect_platform_case_insensitive() -> None:
    assert detect_platform("https://www.YouTube.COM/watch?v=abc") == "youtube"
    assert detect_platform("https://www.BiliBili.com/video/BV1xx") == "bilibili"
    assert detect_platform("https://www.DouYin.com/video/123") == "douyin"


def test_detect_platform_unsupported() -> None:
    with pytest.raises(ValueError):
        detect_platform("https://example.com/video/123")

