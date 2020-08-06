package services

import models.Song
import java.sql.ResultSet
import play.api.db.Database
import javax.inject.Inject

class SongService @Inject() (db: Database) extends QueryHelper {

  private def getSong(rs: ResultSet): Song = {
    val artist = rs.getString(SongService.ARTIST_COLUMN)
    val songName = rs.getString(SongService.SONG_NAME_COLUMN)
    val url = rs.getString(SongService.URL_COLUMN)
    val id = rs.getInt(SongService.ID_COLUMN)
    val albumTitleResult = rs.getString(SongService.ALBUM_TITLE_COLUMN)
    val albumTitle = if (Option(albumTitleResult).isEmpty || albumTitleResult.isEmpty()) null else albumTitleResult
    Song(id, artist, songName, url, Option(albumTitle))
  }

  private def getColumnProjections: String = {
    s"${SongService.ARTIST_COLUMN}, ${SongService.ID_COLUMN}, ${SongService.SONG_NAME_COLUMN}, ${SongService.URL_COLUMN}, ${SongService.ALBUM_TITLE_COLUMN}"
  }

  def getSongs(): Array[Song] = {
    this.query[Song](
        db,
        s"select ${this.getColumnProjections} from songs join albums where songs.album_id = albums.album_id",
        getSong)
  }

  def getSongsBySongList(songListId: Int): Array[Song] = {
    this.query[Song](db,
        s"select ${this.getColumnProjections} from songs join albums join song_list_songs where songs.album_id = albums.album_id AND song_list_songs.song_id = songs.id AND song_list_songs.song_list_id = ${songListId}",
        getSong)
  }

  def getSong(id: String): Song = {
    this.query[Song](
        db,
        s"select ${this.getColumnProjections} from songs join albums where songs.album_id = albums.album_id AND songs.id='${id}'",
        getSong).headOption.getOrElse(Song(0, "", "", "", None))
  }
}

object SongService {
  final val ID_COLUMN: String = "id"
  final val ARTIST_COLUMN: String = "artist"
  final val SONG_NAME_COLUMN: String = "name"
  final val URL_COLUMN: String = "filename"
  final val ALBUM_TITLE_COLUMN: String = "albums.title"
}
