#!/usr/bin/env python3
"""Copy-only, checksum-verified staging of legacy Jynlo profile media to private R2.

NO database writes, public-object deletes, cache purges or privacy toggles.
Dry-run is the default. Never print environment credentials.
Input CSV: kind,item_id,owner_id,slot,storage_key,mime_type
kind: post/story. slot: image,video,extra-0..extra-5 for posts; media for stories.
Run with --copy --confirm-copy-only after an audited inventory and backups.
"""
import argparse
import csv
import hashlib
import json
import os
import re
import sys
import uuid
from pathlib import Path

MAX_BYTES = 60 * 1024 * 1024
MIME_EXT = {
    "image/jpeg": ".jpg", "image/png": ".png", "image/webp": ".webp",
    "video/mp4": ".mp4", "video/webm": ".webm",
}
HEADER = {"kind", "item_id", "owner_id", "slot", "storage_key", "mime_type"}
R2_HOST = re.compile(r"^https://[a-z0-9-]+\\.r2\\.cloudflarestorage\\.com/?$", re.I)


def validate_row(row):
    kind = row["kind"]
    owner_id = int(row["owner_id"])
    item_id = int(row["item_id"])
    slot = row["slot"]
    key = row["storage_key"]
    mime = row["mime_type"].strip().lower()
    if kind not in {"post", "story"} or owner_id <= 0 or item_id <= 0:
        raise ValueError("Invalid media owner/item")
    if kind == "story" and slot != "media":
        raise ValueError("Story slot must be media")
    if kind == "post" and not re.fullmatch(r"image|video|extra-[0-5]", slot):
        raise ValueError("Invalid post media slot")
    if mime not in MIME_EXT:
        raise ValueError("Unsupported media MIME type")
    if mime.startswith("image/") and slot == "video":
        raise ValueError("Video slot has image MIME type")
    if mime.startswith("video/") and slot == "image":
        raise ValueError("Image slot has video MIME type")
    legacy = rf"^(?:posts|stories)/{owner_id}/[A-Za-z0-9_-]+\\.(?:jpg|jpeg|png|webp|mp4|webm)$"
    if not re.fullmatch(legacy, key):
        raise ValueError("Legacy key does not belong to the expected owner")
    prefix = "posts" if kind == "post" else "stories"
    if not key.startswith(prefix + "/"):
        raise ValueError("Unexpected legacy media namespace")
    route = (f"/api/profile-media/posts/{item_id}/{slot}" if kind == "post"
             else f"/api/profile-media/stories/{item_id}")
    return kind, owner_id, key, mime, route


def read_inventory(path):
    with path.open("r", newline="", encoding="utf-8-sig") as handle:
        reader = csv.DictReader(handle)
        if set(reader.fieldnames or []) != HEADER:
            raise ValueError("Input CSV columns do not match the documented schema")
        rows = []
        seen = set()
        for index, row in enumerate(reader, 2):
            kind, owner_id, key, mime, route = validate_row(row)
            identity = (kind, row["item_id"], row["slot"])
            if identity in seen:
                raise ValueError(f"Duplicate media slot at CSV line {index}")
            seen.add(identity)
            rows.append(dict(kind=kind, owner_id=owner_id, legacy_key=key,
                             mime_type=mime, authorized_url=route, slot=row["slot"],
                             item_id=int(row["item_id"])))
        return rows


def settings():
    endpoint = os.environ.get("JYNLO_R2_API_ENDPOINT", "")
    public_bucket = os.environ.get("JYNLO_PUBLIC_MEDIA_BUCKET", "")
    private_bucket = os.environ.get("JYNLO_PRIVATE_MEDIA_BUCKET", "")
    public_access = os.environ.get("JYNLO_PUBLIC_MEDIA_ACCESS_KEY", "")
    private_access = os.environ.get("JYNLO_PRIVATE_MEDIA_ACCESS_KEY", "")
    public_secret = os.environ.get("JYNLO_PUBLIC_MEDIA_SECRET_KEY", "")
    private_secret = os.environ.get("JYNLO_PRIVATE_MEDIA_SECRET_KEY", "")
    if (not R2_HOST.fullmatch(endpoint) or not public_bucket or not private_bucket
            or public_bucket == private_bucket or not all(
                [public_access, private_access, public_secret, private_secret])
            or public_access == private_access or public_secret == private_secret):
        raise ValueError("Dedicated public/private R2 configuration required")
    return endpoint.rstrip("/"), public_bucket, private_bucket, (
        public_access, public_secret), (private_access, private_secret)


def clients(config):
    try:
        import boto3
        from botocore.config import Config
    except ImportError as e:
        raise RuntimeError("Install boto3 for --copy mode") from e
    endpoint, public_bucket, private_bucket, public_creds, private_creds = config
    def client(creds):
        return boto3.client(
            "s3", endpoint_url=endpoint, region_name="auto",
            aws_access_key_id=creds[0], aws_secret_access_key=creds[1],
            config=Config(retries={"max_attempts": 2}))
    return client(public_creds), client(private_creds), public_bucket, private_bucket


def stage(row, public, private, source_bucket, destination_bucket):
    reply = public.get_object(Bucket=source_bucket, Key=row["legacy_key"])
    count = int(reply["ContentLength"])
    if not 0 < count <= MAX_BYTES:
        raise ValueError("Unsupported legacy object size")
    data = reply["Body"].read(MAX_BYTES + 1)
    if len(data) != count or len(data) > MAX_BYTES:
        raise ValueError("Legacy object length mismatch")
    extension = MIME_EXT[row["mime_type"]]
    namespace = "posts" if row["kind"] == "post" else "stories"
    new_key = f"private-profile-media/{namespace}/{row['owner_id']}/{uuid.uuid4().hex}{extension}"
    checksum = hashlib.sha256(data).hexdigest()
    private.put_object(Bucket=destination_bucket, Key=new_key, Body=data,
                       ContentType=row["mime_type"])
    try:
        verified = private.get_object(Bucket=destination_bucket, Key=new_key)
        copied = verified["Body"].read(MAX_BYTES + 1)
        if len(copied) != len(data) or hashlib.sha256(copied).hexdigest() != checksum:
            raise ValueError("Private copy failed SHA-256 verification")
    except Exception:
        private.delete_object(Bucket=destination_bucket, Key=new_key)
        raise
    return {**row, "private_key": new_key, "bytes": len(data),
            "sha256": checksum, "verified": True}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--inventory", required=True, type=Path)
    parser.add_argument("--manifest", type=Path, help="Private local output manifest for verified copies")
    parser.add_argument("--copy", action="store_true", help="Copy verified bytes to a separate private bucket")
    parser.add_argument("--confirm-copy-only", action="store_true",
                        help="Acknowledge that --copy does not migrate SQL or delete public objects")
    args = parser.parse_args()
    if args.copy and (not args.confirm_copy_only or not args.manifest):
        parser.error("--copy requires --confirm-copy-only and --manifest")
    rows = read_inventory(args.inventory)
    print(f"Validated {len(rows)} inventory records; mode={'COPY_ONLY' if args.copy else 'DRY_RUN'}")
    if not args.copy:
        return
    public, private, source_bucket, destination_bucket = clients(settings())
    args.manifest.parent.mkdir(parents=True, exist_ok=True)
    fd = os.open(args.manifest, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
    try:
        with os.fdopen(fd, "w", encoding="utf-8") as handle:
            for row in rows:
                manifest_entry = stage(row, public, private, source_bucket, destination_bucket)
                handle.write(json.dumps(manifest_entry, ensure_ascii=False) + "\\n")
                handle.flush()
                os.fsync(handle.fileno())
    except Exception:
        print("STOP: incomplete manifest; do not update database or delete public objects",
              file=sys.stderr)
        raise
    print("Private copies checksum-verified. SQL and CDN cutover NOT performed.")


if __name__ == "__main__":
    main()
