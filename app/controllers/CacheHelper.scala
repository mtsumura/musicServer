package controllers

import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Singleton
import play.api.cache.SyncCacheApi
import play.api.libs.json.JsValue
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents

@Singleton
class CacheHelper @Inject() (cache: SyncCacheApi, val controllerComponents: ControllerComponents) extends BaseController {
  final val USE_CACHE = true
  final val log = LoggerFactory.getLogger(this.getClass());

  protected def getJsonObjectFromCache(key: String): Option[JsValue] = {
    try {
      if (this.USE_CACHE) cache.get[JsValue](key) else None
    } catch {
      case ex: Exception => {
        log.error(ex.getMessage)
        None
      }
    }
  }

  protected def setJsonObjectToCache(key: String, jsonSongList: JsValue) = {
    if (USE_CACHE) cache.set(key, jsonSongList)
  }

  protected def getBinaryFromCache(songId: String): Option[Array[Byte]] = {
    val cacheKey = songId
    val maybeSong: Option[Array[Byte]] = try {
      if (USE_CACHE) {
        cache.get[Array[Byte]](cacheKey)
      } else {
        None
      }
    } catch {
      case ex: Exception => {
        log.error(ex.getMessage)
        None
      }
    }

    maybeSong
  }

  protected def setBinaryToCache(songId: String, byteArray: Array[Byte]) = {
    if (USE_CACHE) cache.set(songId, byteArray)
  }
}
