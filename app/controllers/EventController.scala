package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.BaseController
import services.UserService
import play.api.db.Database
import org.slf4j.LoggerFactory
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Action
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import models.UserJson
import models.Event
import services.MessageService
import services.SendMessage

@Singleton
class EventController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {

  implicit val eventWrites = Json.reads[Event]
  
  final val log = LoggerFactory.getLogger(this.getClass());
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def record: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    log.info("EventController event:" + json)
    val event = json.as[Event]
    println(event)
        
    val msgService: SendMessage = new MessageService()
    msgService.addMessageToTopic(json.toString())
    
    log.info("EventConroller event end")
    Ok("")
  }
}
