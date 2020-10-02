package controllers

import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.Request
import services.message.MessageService
import services.message.SendMessage

@Singleton
class EventController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {

  final val log = LoggerFactory.getLogger(this.getClass());
  
  private val msgService: SendMessage = new MessageService()
  private final val recordEvent = false
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def record: Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    if(recordEvent) {
      log.info("EventController event:" + json)
      msgService.addMessageToTopic(json.toString())
      Ok("event added")
    }
    else {
      log.warn("EventController event ignored:" + json)
      Ok("event ignored")
    }
  }
}
