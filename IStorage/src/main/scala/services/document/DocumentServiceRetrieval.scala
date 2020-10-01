package services.document

trait DocumentServiceRetrieval {
  def getObject(key: String): Array[Byte]
}