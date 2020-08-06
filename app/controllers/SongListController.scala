package controllers

import javax.inject.Inject
import javax.inject.Singleton
import models.Song
import models.SongJson
import models.SongListJson
import play.api.cache.SyncCacheApi
import play.api.db.Database
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import play.api.mvc.Action
import play.api.mvc.Request
import services.SongListService
import services.SongService

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class SongListController @Inject() (cache: SyncCacheApi, controllerComponents: ControllerComponents)(db: Database)
  extends CacheHelper(cache, controllerComponents) {
  implicit val songWrites = Json.writes[SongJson]
  implicit val songListWrites = Json.writes[SongListJson]

  private final val SONG_KEY = "SONGS_V2"

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def list(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val testId = -1
    val songList = getSongs(SONG_KEY + testId) match {
      case Some(cachedSongList) =>
        log.info("cache Hit, reUse songList!")
        cachedSongList
      case None =>
        log.info("populate songList")
        val songService = new SongService(db)
        val songList = songService.getSongs()
        convertAndCacheSongs(songList, testId)
    }
    Ok(songList)
  }

  def listSongsBySongList(id: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val songList = getSongs(SONG_KEY + id) match {
      case Some(cachedSongList) =>
        log.info("cache Hit, reUse songList!")
        cachedSongList
      case None =>
        log.info("populate songList")
        val songService = new SongService(db)
        val songList = songService.getSongsBySongList(id)
        convertAndCacheSongs(songList, id)
    }

    Ok(songList)
  }

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val songService = new SongListService(db)
    val lists = songService.getLists()
    val jsonLists = lists.map(l => SongListJson(l.id, l.name, l.url))
    Ok(Json.toJson(jsonLists))
  }

  private def convertAndCacheSongs(songList: Array[Song], id: Int): JsValue = {
    val songListModel = songList.map(s => SongJson(s.id, s.artist, s.songTitle, s.url))
    val jsonSongs = Json.toJson(songListModel)
    setSongs(SONG_KEY + id, jsonSongs)
    jsonSongs
  }
}
