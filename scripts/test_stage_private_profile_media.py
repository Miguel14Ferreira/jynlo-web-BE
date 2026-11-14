import csv
import hashlib
import importlib.util
import io
import tempfile
import unittest
from pathlib import Path

module_path=Path(__file__).with_name("stage_private_profile_media.py")
spec=importlib.util.spec_from_file_location("jynlo_media_stage",module_path)
stage=importlib.util.module_from_spec(spec)
spec.loader.exec_module(stage)


class FakeS3:
    def __init__(self, objects=None):
        self.objects=dict(objects or {})
        self.deleted=[]
    def get_object(self,Bucket,Key):
        data=self.objects[(Bucket,Key)]
        return {"ContentLength":len(data),"Body":io.BytesIO(data)}
    def put_object(self,Bucket,Key,Body,ContentType):
        self.objects[(Bucket,Key)]=Body
    def delete_object(self,Bucket,Key):
        self.deleted.append((Bucket,Key))
        self.objects.pop((Bucket,Key),None)


class StagePrivateMediaTests(unittest.TestCase):
    def setUp(self):
        self.row={"kind":"post","item_id":"123","owner_id":"42",
                  "slot":"image","storage_key":"posts/42/photo-abc.jpg",
                  "mime_type":"image/jpeg"}

    def test_validates_expected_owner_and_namespace(self):
        self.assertEqual(stage.validate_row(self.row)[2],"posts/42/photo-abc.jpg")
        for key in ["posts/43/photo-abc.jpg","posts/42/../private.jpg",
                    "https://public.example/posts/42/photo.jpg",
                    "stories/42/photo.jpg"]:
            with self.subTest(key=key):
                with self.assertRaises(ValueError):
                    stage.validate_row({**self.row,"storage_key":key})

    def test_dry_run_requires_no_r2_access(self):
        with tempfile.TemporaryDirectory() as directory:
            path=Path(directory)/"inventory.csv"
            with path.open("w",newline="") as file:
                writer=csv.DictWriter(file,fieldnames=list(stage.HEADER))
                writer.writeheader()
                writer.writerow(self.row)
            rows=stage.read_inventory(path)
            self.assertEqual(len(rows),1)
            self.assertEqual(rows[0]["authorized_url"],
                             "/api/profile-media/posts/123/image")

    def test_checksum_verifies_copy_without_deleting_original(self):
        content=b"jynlo-image-original"
        source=FakeS3({("public","posts/42/photo-abc.jpg"):content})
        destination=FakeS3()
        row=stage.read_inventory(self._csv(self.row))[0]
        result=stage.stage(row,source,destination,"public","private")
        self.assertTrue(result["verified"])
        self.assertEqual(result["sha256"],hashlib.sha256(content).hexdigest())
        self.assertIn(("public","posts/42/photo-abc.jpg"),source.objects)
        self.assertEqual(destination.objects[("private",result["private_key"])],content)
        self.assertEqual(source.deleted,[])

    def _csv(self,row):
        directory=tempfile.mkdtemp()
        self.addCleanup(lambda: __import__("shutil").rmtree(directory))
        path=Path(directory)/"inventory.csv"
        with path.open("w",newline="") as file:
            writer=csv.DictWriter(file,fieldnames=list(stage.HEADER))
            writer.writeheader()
            writer.writerow(row)
        return path


if __name__=="__main__":
    unittest.main()
