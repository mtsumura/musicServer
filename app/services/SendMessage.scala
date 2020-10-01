package services

trait SendMessage {
  def addMessageToTopic(msg: String)
}