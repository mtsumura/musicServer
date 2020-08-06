package models

import play.api.libs.json.JsValue
import scala.io.Source
import play.api.libs.json.Json

object Songs {
  private val source: String = Source.fromFile("app/models/rockSongs.json").getLines.mkString
  def json: JsValue = {
    Json.parse(source)
  }
}
case class Song(id: Int, artist: String, songTitle: String, url: String, albumTitle: Option[String])
case class SongJson(id: Int, artist: String, songTitle: String, url: String)
