from pathlib import Path


def get_home_dir():
    return str(Path.home())


schema = {
    "type": "function",
    "function": {
        "name": "get_home_dir",
        "description": "Get the current user's home directory path",
        "parameters": {
            "type": "object",
            "properties": {},
            "required": [],
        },
    },
}

func = get_home_dir
