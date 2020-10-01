package services.message

trait SendMessage {
  def addMessageToTopic(msg: String)
}