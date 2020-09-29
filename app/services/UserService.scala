package services

import play.api.db.Database
import models.User
import javax.inject.Inject
import java.sql.ResultSet

import scala.reflect.ClassTag

class UserService @Inject() (db: Database) extends QueryHelper {

  //TODO: use prepared statements
  def getUsers(): Array[User] = {
    def getUser(rs: ResultSet): User = {
      val user = rs.getString(UserService.USER_NAME_COLUMN)
      val id = rs.getInt(UserService.ID_COLUMN)
      User(id, user)
    }

    this.query[User](db, s"select ${UserService.USER_NAME_COLUMN}, ${UserService.ID_COLUMN} from users", getUser)
  }

  def getUser(id: Int): User = {
    def getUser(rs: ResultSet): User = {
      val user = rs.getString(UserService.USER_NAME_COLUMN)
      val id = rs.getInt(UserService.ID_COLUMN)
      User(id, user)
    }

    this.query[User](
      db,
      s"select ${UserService.USER_NAME_COLUMN}, ${UserService.ID_COLUMN} from users where ${UserService.ID_COLUMN}=${id}",
      getUser).headOption match {
        case Some(u) => u
        case None    => throw new Exception
      }
  }
}

object UserService {
  final val USER_NAME_COLUMN: String = "user_name"
  final val ID_COLUMN: String = "id"
}
