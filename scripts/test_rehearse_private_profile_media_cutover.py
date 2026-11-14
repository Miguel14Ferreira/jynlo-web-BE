import hashlib
import json
import tempfile
import unittest
from pathlib import Path
from scripts.rehearse_private_profile_media_cutover import validate_manifest, rehearsal_sql

def valid(kind="post", slot="image", item=101, owner=42):
    ext = ".jpg" if kind == "post" else ".mp4"
    mime = "image/jpeg" if kind == "post" else "video/mp4"
    folder = "posts" if kind == "post" else "stories"
    old = f"{folder}/{owner}/legacy-file{ext}"
    private = f"private-profile-media/{folder}/{owner}/new-file{ext}"
    route = (f"/api/profile-media/posts/{item}/{slot}" if kind == "post"
             else f"/api/profile-media/stories/{item}")
    return dict(kind=kind, item_id=item, owner_id=owner, slot=slot,
                legacy_key=old, private_key=private,
                authorized_url=route, mime_type=mime, bytes=100,
                sha256=hashlib.sha256(b"some-data").hexdigest(), verified=True)

class ManifestCutoverTests(unittest.TestCase):
    def setUp(self):
        self.directory=tempfile.TemporaryDirectory()
        self.addCleanup(self.directory.cleanup)
        self.file=Path(self.directory.name)/"manifest.jsonl"

    def parse(self, *rows):
        self.file.write_text("".join(json.dumps(row)+"\n" for row in rows))
        return validate_manifest(self.file)

    def test_rollback_only_rehearsal_guards_every_object(self):
        rows=self.parse(valid(),valid("story","media",102))
        sql=rehearsal_sql(rows)
        self.assertIn("BEGIN;",sql)
        self.assertIn("ROLLBACK;",sql)
        self.assertNotIn("COMMIT;",sql)
        self.assertEqual(sql.count("GET DIAGNOSTICS affected = ROW_COUNT"),2)
        self.assertIn("author_id=42",sql)
        self.assertIn("community_id IS NULL",sql)
        self.assertIn("image_storage_key='posts/42/legacy-file.jpg'",sql)

    def test_albums_include_owner_join_and_exact_position(self):
        row=valid(slot="extra-3")
        rows=self.parse(row)
        sql=rehearsal_sql(rows)
        self.assertIn("social_post_extra_media AS media",sql)
        self.assertIn("media.position=3",sql)
        self.assertIn("post.author_id=42",sql)

    def test_rejects_wrong_owner_or_mismatched_route(self):
        wrong=valid()
        wrong["private_key"]="private-profile-media/posts/7/new-file.jpg"
        with self.assertRaises(ValueError): self.parse(wrong)
        wrong=valid()
        wrong["authorized_url"]="/api/profile-media/posts/101/video"
        with self.assertRaises(ValueError): self.parse(wrong)

    def test_rejects_duplicate_slots_and_unverified_copies(self):
        with self.assertRaises(ValueError): self.parse(valid(),valid())
        row=valid();row["verified"]=False
        with self.assertRaises(ValueError): self.parse(row)

    def test_rejects_missing_or_unexpected_manifest_content(self):
        with self.assertRaises(ValueError): self.parse()
        row=valid();row["sha256"]="invalid"
        with self.assertRaises(ValueError): self.parse(row)
        row=valid();row["bytes"]=0
        with self.assertRaises(ValueError): self.parse(row)

if __name__=="__main__":
    unittest.main()
