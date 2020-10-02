

package services.storage

import org.scalatest._

class DocumentServiceTest extends FunSuite {
  
  test("Basic S3 Conectivity") {
    val service = new DocumentService()
    val byteArray = service.getObject("source3/Feist/The Reminder/09 1 2 3 4.m4a")
    assert(byteArray != null)
  }
}