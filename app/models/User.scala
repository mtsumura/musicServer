package models

//Internal User object that is abstracted to the outside world
case class User(id: Int, name: String)

//External User object that is part of the User REST API
case class UserJson(id: Int, name: String, profileUrl: Option[String])

