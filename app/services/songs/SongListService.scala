package services.songs

import java.sql.ResultSet
import play.api.db.Database
import javax.inject.Inject
import models.SongList
import services.query.QueryHelper

class SongListService @Inject() (db: Database) extends QueryHelper {

  private def getSongList(rs: ResultSet): SongList = {
    val name = rs.getString(SongListService.NAME_COLUMN)
    val id = rs.getInt(SongListService.ID_COLUMN)
    val song_list_url = rs.getString(SongListService.URL_COLUMN)
    SongList(id, name, song_list_url)
  }

  private def getColumnProjections: String = {
    s"${SongListService.ID_COLUMN}, ${SongListService.NAME_COLUMN}, ${SongListService.URL_COLUMN}"
  }

  def getLists(): Array[SongList] = {
    this.query[SongList](db, s"select ${this.getColumnProjections} from song_lists", getSongList)
  }
}

object SongListService {
  final val ID_COLUMN: String = "id"
  final val NAME_COLUMN: String = "name"
  final val URL_COLUMN: String = "song_list_url"
}
