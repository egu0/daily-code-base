# Tool Use in Reflective Research Agent

## Step 1: Generate Research Report with Tools

```python
    messages = [
        {
            "role": "system",
            "content": (
                "You are a research assistant that can search the web and arXiv to write detailed, "
                "accurate, and properly sourced research reports.\n\n"
                "🔍 Use tools when appropriate (e.g., to find scientific papers or web content).\n"
                "📚 Cite sources whenever relevant. Do NOT omit citations for brevity.\n"
                "🌐 When possible, include full URLs (arXiv links, web sources, etc.).\n"
                "✍️ Use an academic tone, organize output into clearly labeled sections, and include "
                "inline citations or footnotes as needed.\n"
                "🚫 Do not include placeholder text such as '(citation needed)' or '(citations omitted)'."
            )
        },
        {"role": "user", "content": "Radio observations of recurrent novae"}
    ]
```

## Step 2: Reflection and Rewrite

```python
    messages=[
        {"role": "system", "content": "You are an academic reviewer and editor."},
        {"role": "user", "content": (
                "First, provide a structured reflection (Strengths, Limitations, Suggestions, Opportunities) "
                "on the following report.\n\n"
                "Then, write a revised version of the report that incorporates your suggestions, "
                "improves clarity, and strengthens academic tone.\n\n"
                f"Report:\n{report}"
            )
        },
    ]
```

## Step 3: Convert Report to Html

```python
    system_prompt =
    messages=[
        {"role": "system", "content": "You convert plaintext reports into full clean HTML documents."},
        {"role": "user", "content": (
                "You are an expert technical writing assistant. "
                "Convert the following plaintext research report into a clean, structured HTML document. "
                "Include section headers, well-formatted paragraphs, inline links, and a clean readable layout. "
                "Ensure that all URLs are clickable and citation style is preserved.\n\n"
                "Respond ONLY with valid HTML (no explanation).\n\n"
                f"Report:\n{text_report}"
            )
        }
    ]
```
