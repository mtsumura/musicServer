package services.storage

import org.scalatest.FunSuite

class FileSystemServiceTest extends FunSuite {
 
  test("Basic File system read replacement") {
    val filePath = "public/source/Bon Jovi/Bad_Medicine.mp3"
    val byteArray = new FileSystemService().getObject(filePath)
    assert(byteArray != null)
  }
}