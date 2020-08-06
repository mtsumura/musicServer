package services

trait DocumentServiceRetrieval {
  def getObject(key: String): Array[Byte]
}