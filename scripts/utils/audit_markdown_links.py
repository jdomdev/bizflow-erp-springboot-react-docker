#!/usr/bin/env python3
"""Scan Markdown files for unresolved relative links."""

import argparse
import re
import sys
from pathlib import Path
from typing import Optional

SKIP_PREFIXES = (
    "http://",
    "https://",
    "mailto:",
    "tel:",
    "ftp://",
    "data:",
    "javascript:",
)
LINK_PATTERN = re.compile(r"(?<!!)[[][^\]]+[]]\(([^)]+)\)")


def find_links(markdown: str):
    """Yield link targets from Markdown inline links."""
    for match in LINK_PATTERN.finditer(markdown):
        yield match.group(1).strip()


def is_skipped(target: str) -> bool:
    if not target:
        return True
    lowered = target.lower()
    if lowered.startswith(SKIP_PREFIXES):
        return True
    if target.startswith("#"):
        return True
    return False


def resolve_target(source: Path, target: str, repo_root: Path) -> Optional[Path]:
    clean_target = target.split("#", 1)[0].split("?", 1)[0].strip()
    if not clean_target:
        return None
    candidate = (source.parent / clean_target).resolve()
    try:
        candidate.relative_to(repo_root)
    except ValueError:
        return None
    return candidate


def audit(paths: list[Path], repo_root: Path) -> list[tuple[Path, str]]:
    missing: list[tuple[Path, str]] = []
    for base in paths:
        if not base.exists():
            print(f"Warning: path {base} does not exist; skipping.", file=sys.stderr)
            continue
        for md_file in sorted(base.rglob("*.md")):
            try:
                text = md_file.read_text(encoding="utf-8", errors="replace")
            except OSError as exc:
                print(f"Warning: could not read {md_file}: {exc}", file=sys.stderr)
                continue
            for target in find_links(text):
                if is_skipped(target):
                    continue
                resolved = resolve_target(md_file, target, repo_root)
                if resolved is None:
                    continue
                if not resolved.exists():
                    relative_md = md_file.relative_to(repo_root)
                    missing.append((relative_md, target))
    return missing


def parse_args(repo_root: Path) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Check Markdown files for relative links pointing to missing files.",
    )
    parser.add_argument(
        "paths",
        nargs="*",
        default=["docs"],
        help="Directories or files to scan (default: docs).",
    )
    parser.add_argument(
        "--root",
        default=str(repo_root),
        help="Repository root directory (auto-detected).",
    )
    return parser.parse_args()


def main() -> int:
    script_dir = Path(__file__).resolve().parent
    repo_root = Path(script_dir, "../..").resolve()
    args = parse_args(repo_root)
    repo_root = Path(args.root).resolve()

    search_paths = []
    for raw_path in args.paths:
        path_obj = Path(raw_path)
        if not path_obj.is_absolute():
            path_obj = (repo_root / raw_path).resolve()
        search_paths.append(path_obj)

    missing = audit(search_paths, repo_root)

    if missing:
        print("Broken Markdown links detected:")
        for md_file, target in missing:
            print(f"  {md_file}: {target}")
        return 1

    print("No broken Markdown links found.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
