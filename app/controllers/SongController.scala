package controllers

import java.io.ByteArrayInputStream
import java.io.File

import javax.inject.Inject
import javax.inject.Singleton
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.RangeResult
import play.api.mvc.Request
import play.api.mvc.Result
import services.DocumentService
import services.SongService

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class SongController @Inject() (cache: SyncCacheApi, controllerComponents: ControllerComponents)(db: Database)
  extends CacheHelper(cache, controllerComponents) {

  private val SONG_BINARY_KEY = "SONG_BINARY_"
  def get(songId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val rangeHeader = request.headers.get(RANGE)
    log.info("header:" + rangeHeader.toString())

    val contentType = Some("audio/mpeg")
    val byteArray = getSongBinary(SONG_BINARY_KEY + songId) match {
      case Some(byteArray) => byteArray
      case None => {
        val songService = new SongService(db)
        val song = songService.getSong(songId)
        val documentKey = song.albumTitle match {
          case Some(albumTitle) => s"source3/${song.artist}/${albumTitle}/${song.url}"
          case None             => s"source/${song.artist}/${song.url}"
        }

        log.info(songId + " Cache Miss, cache Song: " + documentKey)
        val ds = new DocumentService()
        val byteArray = ds.getObject(documentKey)

        //val byteArray = Files.readAllBytes(Paths.get(filePath))
        //val byteArray = new FileSystemService().getObject(filePath)

        log.info("byteArray size" + byteArray.length)
        setSongBinary(SONG_BINARY_KEY + songId, byteArray)
        byteArray
      }
    }

    getResult(byteArray, rangeHeader, songId, contentType)
  }
  def get(artistId: String, songName: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    log.info("artistId:" + artistId + " songName:" + songName)
    val cacheKey = (artistId + "-" + songName).replaceAll(" ", "_")
    log.info("cacheKey:" + cacheKey)

    val rangeHeader = request.headers.get(RANGE)
    log.info("header:" + rangeHeader.toString())
    val filePath = "public/source/" + artistId + "/" + songName
    val contentType = Some("audio/mpeg")
    val byteArray = getSongBinary(cacheKey) match {
      case Some(byteArray) => byteArray
      case None => {
        val file = new File(filePath)
        log.info(s"file: ${filePath} size: ${file.getTotalSpace}")
        log.info(cacheKey + " Cache Miss, cache Song: " + filePath)
        val ds = new DocumentService()
        val documentKey = s"source/${artistId}/${songName}"
        val byteArray = ds.getObject(documentKey)

        //val byteArray = Files.readAllBytes(Paths.get(filePath))
        //val byteArray = new FileSystemService().getObject(filePath)

        log.info("byteArray size" + byteArray.length)
        setSongBinary(cacheKey, byteArray)
        byteArray
      }
    }

    getResult(byteArray, rangeHeader, filePath, contentType)
  }

  private def getResult(byteArray: Array[Byte], rangeHeader: Option[String], filePath: String, contentType: Option[String]): Result = {
    val stream = new ByteArrayInputStream(byteArray)
    try {
      RangeResult.ofStream(byteArray.length, stream, rangeHeader, filePath, contentType)
    } finally {
      if (stream != null) {
        stream.close()
      }
    }
  }
}
