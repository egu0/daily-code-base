import json

from ...skills import read_skill as load_skill


def read_skill(name):
    try:
        return json.dumps(load_skill(name), ensure_ascii=False)
    except ValueError as exc:
        return f"Error: {exc}"
    except OSError as exc:
        return f"Error: unable to read skill '{name}': {exc}"


schema = {
    "type": "function",
    "function": {
        "name": "read_skill",
        "description": (
            "Read the full SKILL.md instructions for an available skill by name. "
            "Call this before acting when a user request matches a skill summary "
            "from the system prompt."
        ),
        "parameters": {
            "type": "object",
            "properties": {
                "name": {
                    "type": "string",
                    "description": "Skill name from the available skills list.",
                },
            },
            "required": ["name"],
        },
    },
}

func = read_skill
