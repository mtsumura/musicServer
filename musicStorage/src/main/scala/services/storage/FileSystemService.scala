package services.storage

import java.nio.file.Files
import java.nio.file.Paths
import services.document.DocumentServiceRetrieval

class FileSystemService extends DocumentServiceRetrieval {
  
  def getObject(filePath: String) : Array[Byte] = {
       Files.readAllBytes(Paths.get(filePath))
  }
}
