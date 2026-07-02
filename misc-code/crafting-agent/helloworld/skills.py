from dataclasses import dataclass
from pathlib import Path

from .config import default_skills_dir, resolve_skills_dir

SKILLS_DIR = default_skills_dir()
SKILL_FILE_NAME = "SKILL.md"


@dataclass(frozen=True)
class Skill:
    name: str
    description: str
    path: Path


def _strip_quotes(value: str) -> str:
    if len(value) >= 2 and value[0] == value[-1] and value[0] in {"'", '"'}:
        return value[1:-1]
    return value


def parse_skill_metadata(text: str) -> dict[str, str]:
    lines = text.splitlines()
    if not lines or lines[0].strip() != "---":
        return {}

    metadata: dict[str, str] = {}
    for line in lines[1:]:
        if line.strip() == "---":
            break
        key, separator, value = line.partition(":")
        if not separator:
            continue
        metadata[key.strip()] = _strip_quotes(value.strip())
    return metadata


def discover_skills(skills_dir: Path | str | None = None) -> list[Skill]:
    root = Path(skills_dir) if skills_dir is not None else resolve_skills_dir()
    if not root.exists() or not root.is_dir():
        return []

    discovered = []
    for skill_dir in sorted(root.iterdir(), key=lambda path: path.name):
        skill_file = skill_dir / SKILL_FILE_NAME
        if not skill_dir.is_dir() or not skill_file.is_file():
            continue
        try:
            content = skill_file.read_text(encoding="utf-8")
        except OSError:
            continue
        metadata = parse_skill_metadata(content)
        name = metadata.get("name")
        description = metadata.get("description")
        if not name or not description:
            continue
        discovered.append(
            Skill(
                name=name,
                description=description,
                path=skill_file.resolve(),
            )
        )
    return discovered


def read_skill(name: str, skills_dir: Path | str | None = None) -> dict[str, str]:
    for skill in discover_skills(skills_dir):
        if skill.name != name:
            continue
        return {
            "name": skill.name,
            "path": str(skill.path),
            "content": skill.path.read_text(encoding="utf-8"),
        }
    raise ValueError(f"unknown skill: {name}")


def format_skill_index(skills: list[Skill]) -> str:
    if not skills:
        return ""

    lines = [
        "You have access to skills.",
        "",
        (
            "A skill is a specialized instruction package. If the user request "
            "matches a skill description, read the full instructions before "
            "answering or editing code. Use the read_skill tool with the skill "
            "name. Do not assume skill contents from the summary."
        ),
        "",
        "Available skills:",
    ]
    for skill in skills:
        lines.extend(
            [
                f"- name: {skill.name}",
                f"  description: {skill.description}",
                f"  path: {skill.path}",
            ]
        )
    return "\n".join(lines)
