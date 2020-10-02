package controllers

import java.io.ByteArrayInputStream

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
import services.document.DocumentServiceRetrieval

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class SongController @Inject() (cache: SyncCacheApi, controllerComponents: ControllerComponents, ds: DocumentServiceRetrieval)(db: Database)
  extends CacheHelper(cache, controllerComponents) with PersistenceHelper {

  private val SONG_BINARY_KEY = "SONG_BINARY_"
  def get(songId: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val byteArray = getBinaryFromCache(SONG_BINARY_KEY + songId) match {
      case Some(byteArray) => byteArray
      case None => {
        val byteArray = retrieveBinaryFromPersistence(songId, db, ds)
        setBinaryToCache(SONG_BINARY_KEY + songId, byteArray)
        byteArray
      }
    }

    val rangeHeader = request.headers.get(RANGE)
    log.info("header:" + rangeHeader.toString())
    val contentType = Some("audio/mpeg")
    getResult(byteArray, rangeHeader, songId, contentType)
  }

  def get(artistId: String, songName: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    log.info("artistId:" + artistId + " songName:" + songName)
    val cacheKey = (artistId + "-" + songName).replaceAll(" ", "_")
    log.info("cacheKey:" + cacheKey)

    val filePath = "public/source/" + artistId + "/" + songName
    val byteArray = getBinaryFromCache(cacheKey) match {
      case Some(byteArray) => byteArray
      case None => {
        log.info(cacheKey + " Cache Miss, cache Song: " + filePath)
        val byteArray = retrieveBinaryFromPersistence(artistId, songName, ds)
        setBinaryToCache(cacheKey, byteArray)
        byteArray
      }
    }

    val contentType = Some("audio/mpeg")
    val rangeHeader = request.headers.get(RANGE)
    log.info("header:" + rangeHeader.toString())
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
