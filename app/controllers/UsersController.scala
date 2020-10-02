package controllers

import javax.inject.Inject
import javax.inject.Singleton
import play.api.mvc.BaseController
import services.users.UserService
import play.api.db.Database
import org.slf4j.LoggerFactory
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.Action
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import models.UserJson

@Singleton
class UsersController @Inject() (val controllerComponents: ControllerComponents, db: Database) extends BaseController {

  final val log = LoggerFactory.getLogger(this.getClass());
  implicit val userWrites = Json.writes[UserJson]
  
  private val userService = new UserService(db)
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def list(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    log.info("UsersController list")
    
    val users = userService.getUsers()
    val usersJson: Array[UserJson] = users.map(user => UserJson(user.id, user.name, None))
    val jsonPayLoad = Json.toJson(usersJson)
    log.info("UsersConroller list payload:" + jsonPayLoad.toString())
    Ok(jsonPayLoad)
  }

  def getUser(id: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    val user = userService.getUser(id)
    val jsonPayLoad = Json.toJson(UserJson(user.id, user.name, None))
    log.info("UsersConroller getUser payload:" + jsonPayLoad.toString())

    Ok(jsonPayLoad)
  }
}
