# Video Transcriber

Download video media from supported platforms, extract audio, and transcribe using local ASR.

## Supported Platforms

- YouTube
- Bilibili
- Douyin

## Pipeline

1. Parse URL to detect platform
2. Download video media using `yt-dlp` (YouTube/Bilibili) or `parse-video` (Douyin)
3. Extract 16kHz mono WAV audio with ffmpeg
4. Transcribe via ASR backend (default: mlx-audio-stt). Audio longer than 1 minute is automatically segmented.
5. Write `data/media_info.json` and `data/transcript.json`

## Install

```bash
# 1. install binary 'yt-dlp' and add to your PATH
# https://github.com/yt-dlp/yt-dlp
brew install yt-dlp

# 2. install binary 'parse-video' and add to your PATH
# https://github.com/wujunwei928/parse-video
go install github.com/wujunwei928/parse-video@latest

# 3. uv
```

## Usage

```text
$ uv run python -m src.cli --help
usage: video-summarizer [-h] --url URL [--asr-backend {mlx-audio-stt,faster-whisper}]
                        [--segment-threshold-minutes SEGMENT_THRESHOLD_MINUTES] [--faster-whisper-model FASTER_WHISPER_MODEL]
                        [--mlx-audio-stt-model MLX_AUDIO_STT_MODEL]

Fetch supported video media and prepare transcript artifacts for downstream summarization.

options:
  -h, --help            show this help message and exit
  --url URL             Video URL
  --asr-backend {mlx-audio-stt,faster-whisper}
                        ASR backend (default: mlx-audio-stt)
  --segment-threshold-minutes SEGMENT_THRESHOLD_MINUTES
                        Segment media longer than this threshold before transcription (default: 1)
  --faster-whisper-model FASTER_WHISPER_MODEL
                        faster-whisper model name (default: small)
  --mlx-audio-stt-model MLX_AUDIO_STT_MODEL
                        mlx-audio-stt model name (default: mlx-community/Qwen3-ASR-0.6B-4bit)
```

Use [mlx-audio](https://github.com/Blaizzy/mlx-audio) backend (Apple Silicon Chips):

```bash
uv run python -m src.cli \
  --url "https://www.youtube.com/watch?v=dQw4w9WgXcQ" \
  --segment-threshold-minutes 1 \
  --asr-backend mlx-audio-stt \
  --mlx-audio-stt-model mlx-community/Qwen3-ASR-0.6B-4bit
```

> More [models](https://blaizzy.github.io/mlx-audio/models/stt/).

Switch to [faster-whisper](https://github.com/SYSTRAN/faster-whisper) backend:

```bash
uv run python -m src.cli \
  --url "https://www.youtube.com/watch?v=dQw4w9WgXcQ" \
  --asr-backend faster-whisper \
  --faster-whisper-model small
```

> More [models](https://github.com/SYSTRAN/faster-whisper/blob/v1.1.1/faster_whisper/transcribe.py#L604).
