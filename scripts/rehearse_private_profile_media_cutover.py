#!/usr/bin/env python3
"""Generate a read-only transactional SQL rehearsal from verified R2 copy manifest.

This script never connects to a database, never changes R2 objects and ALWAYS
ends its SQL in ROLLBACK. A production cutover requires a separate reviewed
transaction after CDN and application readiness checks. Do not commit this
manifest or generated SQL: they contain owner IDs and storage keys.
"""
import argparse
import hashlib
import json
import re
from pathlib import Path

try:
    from stage_private_profile_media import validate_row, MIME_EXT
except ImportError:
    from scripts.stage_private_profile_media import validate_row, MIME_EXT

PRIVATE_KEY = re.compile(
    r"^private-profile-media/(posts|stories)/([1-9]\d*)/[a-zA-Z0-9_-]+\.(jpg|jpeg|png|webp|mp4|webm)$"
)
SHA256 = re.compile(r"^[a-f0-9]{64}$")


def literal(value):
    return "'" + str(value).replace("'", "''") + "'"


def validate_manifest(path: Path):
    rows = []
    seen_slots = set()
    seen_private_keys = set()
    with path.open(encoding="utf-8") as handle:
        for lineno, raw in enumerate(handle, 1):
            if not raw.strip():
                continue
            row = json.loads(raw)
            if not isinstance(row, dict):
                raise ValueError(f"Line {lineno}: expected object")
            needed = {"kind", "item_id", "owner_id", "slot", "legacy_key",
                      "mime_type", "authorized_url", "private_key",
                      "bytes", "sha256", "verified"}
            if not needed.issubset(row):
                raise ValueError(f"Line {lineno}: missing fields")
            source = {"kind": row["kind"], "item_id": str(row["item_id"]),
                      "owner_id": str(row["owner_id"]), "slot": row["slot"],
                      "storage_key": row["legacy_key"], "mime_type": row["mime_type"]}
            kind, owner, old, mime, route = validate_row(source)
            key = row["private_key"]
            matched = PRIVATE_KEY.fullmatch(key) if isinstance(key, str) else None
            bucket_type = "posts" if kind == "post" else "stories"
            if (not matched or matched.group(1) != bucket_type
                    or int(matched.group(2)) != owner):
                raise ValueError(f"Line {lineno}: private key belongs to wrong owner")
            if not isinstance(row["verified"], bool) or not row["verified"]:
                raise ValueError(f"Line {lineno}: missing copy integrity verification")
            if (type(row["bytes"]) is not int or not 0 < row["bytes"] <= 60 * 1024 * 1024
                    or not isinstance(row["sha256"], str) or not SHA256.fullmatch(row["sha256"])):
                raise ValueError(f"Line {lineno}: invalid copy digest or size")
            if row["authorized_url"] != route:
                raise ValueError(f"Line {lineno}: incorrect protected API route")
            expected_extension = MIME_EXT[mime]
            if (not key.endswith(expected_extension)
                    and not (mime == "image/jpeg" and key.endswith(".jpeg"))):
                raise ValueError(f"Line {lineno}: content type / extension mismatch")
            identity = (kind, int(row["item_id"]), row["slot"])
            if identity in seen_slots or key in seen_private_keys:
                raise ValueError(f"Line {lineno}: duplicate slot or private object")
            seen_slots.add(identity)
            seen_private_keys.add(key)
            rows.append({**row, "item_id": int(row["item_id"]),
                         "owner_id": owner, "legacy_key": old, "mime_type": mime})
    if not rows:
        raise ValueError("Empty copy manifest is not evidence of a completed migration")
    return rows


def update_statement(row):
    old, new, url = map(literal, (row["legacy_key"], row["private_key"], row["authorized_url"]))
    owner, item = row["owner_id"], row["item_id"]
    kind, slot = row["kind"], row["slot"]
    if kind == "story":
        return (f"UPDATE desporto24.social_story SET media_storage_key={new}, media_url={url} "
                f"WHERE id={item} AND author_id={owner} AND community_id IS NULL "
                f"AND media_storage_key={old}")
    if slot in ("image", "video"):
        return (f"UPDATE desporto24.social_post "
                f"SET {slot}_storage_key={new}, {slot}_url={url} "
                f"WHERE id={item} AND author_id={owner} AND activity_type IS NULL "
                f"AND {slot}_storage_key={old}")
    position = int(slot.removeprefix("extra-"))
    return (f"UPDATE desporto24.social_post_extra_media AS media "
            f"SET media_storage_key={new}, media_url={url} "
            f"FROM desporto24.social_post AS post "
            f"WHERE media.post_id=post.id AND media.position={position} "
            f"AND post.id={item} AND post.author_id={owner} "
            f"AND post.activity_type IS NULL AND media.media_storage_key={old}")


def rehearsal_sql(rows):
    statements = [
        "-- GENERATED REHEARSAL ONLY. Does not commit changes.",
        "-- Source: SHA-256 verified copy manifest. CDN purge is NOT verified here.",
        "-- Run only on an isolated restored staging database (UPDATE permissions required).",
        "BEGIN;",
        "SET LOCAL lock_timeout = '3s';",
        "SET LOCAL statement_timeout = '30s';",
    ]
    for idx, row in enumerate(rows, 1):
        sql = update_statement(row)
        statements.extend([
            f"-- Item {idx}: {row['kind']} {row['item_id']} / {row['slot']}",
            "DO $jynlo$",
            "DECLARE affected integer;",
            "BEGIN",
            f"  {sql};",
            "  GET DIAGNOSTICS affected = ROW_COUNT;",
            "  IF affected <> 1 THEN",
            "    RAISE EXCEPTION 'Jynlo media rehearsal: expected exactly one row, got %', affected;",
            "  END IF;",
            "END",
            "$jynlo$;",
        ])
    statements.extend(["-- Rehearsal ALWAYS reverts its changes.", "ROLLBACK;", ""])
    return "\n".join(statements)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--manifest", type=Path, required=True)
    parser.add_argument("--output", type=Path, required=True)
    args = parser.parse_args()
    rows = validate_manifest(args.manifest)
    if args.output.exists():
        parser.error("Refusing to overwrite an existing rehearsal")
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(rehearsal_sql(rows), encoding="utf-8")
    args.output.chmod(0o600)
    print(f"Prepared rollback-only SQL rehearsal for {len(rows)} verified files.")


if __name__ == "__main__":
    main()
