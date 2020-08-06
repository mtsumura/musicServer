package services

import java.nio.file.Files
import java.nio.file.Paths

class FileSystemService extends DocumentServiceRetrieval {
  
  def getObject(filePath: String) : Array[Byte] = {
       Files.readAllBytes(Paths.get(filePath))
  }
}
