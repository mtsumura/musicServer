package services

import org.slf4j.LoggerFactory


object MessagePollingService {
  final val log = LoggerFactory.getLogger(this.getClass());
  
  def run(): Unit = {
    val consumer: ConsumeMessage = new MessageService()
    
    new Thread {
        override def run(): Unit = {
          log.info("Polling Service Up")
          consumer.streamConsumer()
        }
    }.start()
  }

  def main(args: Array[String]): Unit = {
    MessagePollingService.run()
  }
}
