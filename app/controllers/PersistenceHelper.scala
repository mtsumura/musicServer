package controllers

import services.songs.SongService
import org.slf4j.LoggerFactory
import play.api.db.Database
import services.document.DocumentServiceRetrieval

trait PersistenceHelper {
  final val logger = LoggerFactory.getLogger(this.getClass());
  
  protected def getSongService(db: Database): SongService = {
    val songService = new SongService(db)
    songService
  }
  
  protected def retrieveBinaryFromPersistence(songId: String, db: Database, ds: DocumentServiceRetrieval): Array[Byte] = {
    val songService = getSongService(db)
    val song = songService.getSong(songId)
    val documentKey = song.albumTitle match {
      case Some(albumTitle) => s"source3/${song.artist}/${albumTitle}/${song.url}"
      case None             => s"source/${song.artist}/${song.url}"
    }

    logger.info(songId + " Cache Miss, cache Song: " + documentKey)
    val byteArray = ds.getObject(documentKey)

    //val byteArray = Files.readAllBytes(Paths.get(filePath))
    //val byteArray = new FileSystemService().getObject(filePath)

    logger.info("byteArray size" + byteArray.length)
    byteArray
  }

  protected def retrieveBinaryFromPersistence(artistId: String, songName: String, ds: DocumentServiceRetrieval): Array[Byte] = {
    val documentKey = s"source/${artistId}/${songName}"
    val byteArray = ds.getObject(documentKey)

    //val byteArray = Files.readAllBytes(Paths.get(filePath))
    //val byteArray = new FileSystemService().getObject(filePath)

    logger.info("byteArray size" + byteArray.length)
    byteArray
  }
}