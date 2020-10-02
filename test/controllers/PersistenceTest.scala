

package controllers

import org.scalatest.FunSuite
import services.document.DocumentServiceRetrieval
import play.api.db.Database
import services.songs.SongService
import org.scalamock.scalatest.MockFactory
import models.Song

class PersistenceTest extends FunSuite with MockFactory {

  class PersistenceHelperTestApp extends PersistenceHelper {
    override def retrieveBinaryFromPersistence(songId: String, db: Database, ds: DocumentServiceRetrieval): Array[Byte] = {
      return super.retrieveBinaryFromPersistence(songId, db, ds)
    }

    override def retrieveBinaryFromPersistence(artistId: String, songName: String, ds: DocumentServiceRetrieval): Array[Byte] = {
      return super.retrieveBinaryFromPersistence(artistId, songName, ds)
    }

    override def getSongService(db: Database): SongService = {
      val songService = mock[SongService]
      (songService.getSong _).expects("1").returning(Song(1, "artist", "songTitle", "url", None)).once()
      songService
    }
  }

  class DocumentServiceStub extends DocumentServiceRetrieval {
    override def getObject(key: String): Array[Byte] = {
      Array.emptyByteArray
    }
  }

  test("fetch song by artist and song name")({
    val test = new PersistenceHelperTestApp()
    val artistId = "1"
    val songName = "1"
    val ds: DocumentServiceRetrieval = new DocumentServiceStub()
    val byteArray = test.retrieveBinaryFromPersistence(artistId, songName, ds)
  })
  
  test("fetch song by songId")({
    val test = new PersistenceHelperTestApp()
    val songId = "1"
    val ds: DocumentServiceRetrieval = new DocumentServiceStub()
    val db:Database = mock[Database]
    
    val byteArray = test.retrieveBinaryFromPersistence(songId, db, ds)
  })
}
