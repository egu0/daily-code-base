# Agentic AI - Andrew Ng

- [course](https://www.deeplearning.ai/courses/agentic-ai)
- [slides & code](https://github.com/nhatnam2609/agentic_ai_andrew)

quickstart:

```bash
export TAVILY_API_KEY=''
export OPENAI_API_KEY=''
export ANTHROPIC_API_KEY=''

uv run --with jupyter jupyter lab
```

## ugl_2 of Module 3

start backend:

```bash
cd m3/ugl_2/email_server
uv run uvicorn email_service:app --timeout-keep-alive 1200
```
