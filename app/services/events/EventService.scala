package services.events

import org.slf4j.LoggerFactory
import models.Event
import play.api.db.Databases
import com.typesafe.config.ConfigFactory

class EventService () {
  final val log = LoggerFactory.getLogger(this.getClass());

  def logEvent(event: Event) = {
    val config = ConfigFactory.load()
    Databases.withDatabase(
      driver =  config.getString("db.default.driver"),
      url = config.getString("db.default.url"),
      config = Map(
        "username" -> config.getString("db.default.username"),
        "password" -> config.getString("db.default.password"))) { database =>
        database.withConnection { conn =>
          try {
            val statement = conn.prepareStatement("INSERT INTO EVENTS(type, song_id) VALUES(?, ?)")
            statement.setString(1, event.event.toString())
            statement.setInt(2, event.songId.toInt)
            val result = statement.execute()
            if (!result) {
              log.error("event:" + event.event + " id:" + event.songId)
            }
          } finally {
            conn.close()
          }
        }
      }
  }
}