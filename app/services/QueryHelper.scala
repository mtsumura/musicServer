package services

import scala.reflect.ClassTag
import java.sql.ResultSet
import play.api.db.Database
import org.slf4j.LoggerFactory

trait QueryHelper {

  final val log = LoggerFactory.getLogger(this.getClass());

  private def getResults[T: ClassTag](r: ResultSet, a: Array[T], f: ResultSet => T): Array[T] = {
    if (!r.next()) {
      a
    } else {
      val t = f(r)
      getResults(r, a ++ Array[T](t), f)
    }
  }

  def query[T: ClassTag](db: Database, sql: String, f: ResultSet => T): Array[T] = {
    db.withConnection { conn =>
      try {
        val statement = conn.createStatement()
        log.info("sql:" + sql)
        val resultSet = statement.executeQuery(sql)
        getResults(resultSet, Array.empty[T], f)
      } finally {
        conn.close()
      }
    }
  }
}
