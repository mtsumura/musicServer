package services

import org.apache.kafka.clients.producer.Producer
import java.util.Properties
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import models.Event
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.kstream.Printed
import java.util.concurrent.CountDownLatch
import org.apache.kafka.streams.kstream.Consumed
import play.api.libs.json.JsValue
import play.api.libs.json.Json

class MessageService extends SendMessage with ConsumeMessage{

  final val log = LoggerFactory.getLogger(this.getClass());
  implicit val eventWrites = Json.reads[Event]
  
  final val HOST_IP = ""
  final val HOST_PORT = "9092"
  final val TOPIC_NAME = "quickstart-events"
  final val PARTITION_KEY = "ONE"

  def addMessageToTopic(msg: String) = {
    val props = new Properties()
    props.put("bootstrap.servers", s"${HOST_IP}:${HOST_PORT}")
    props.put("acks", "all");
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

    val producer = new KafkaProducer[String, String](props)
    try {
      val record = new ProducerRecord(TOPIC_NAME, PARTITION_KEY, msg)
      val f = producer.send(record);
    }
    catch {
      case e: Exception => log.error(e.toString())
    }
    finally {
      producer.close();  
    }
  }
  
  def parse(s: String): Option[JsValue] = {
    try {
      val jsonObj = Json.parse(s)
      return Some(jsonObj)
    } catch {
      case e: Exception =>
        return None
    }
  }
  
  def streamConsumer() = {
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-consumer")
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, s"${HOST_IP}:${HOST_PORT}")
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    
    val builder = new StreamsBuilder()
//    val stream = builder.stream(TOPIC_NAME)
//    val sysOut = Printed.toSysOut().withLabel("consumerStream")
//    stream.print(sysOut)
    
    val stringSerde = Serdes.String()
    val entries = builder.stream(TOPIC_NAME, Consumed.`with`(stringSerde, stringSerde))
    entries.foreach((key, value) => {
      parse(value) match {
        case Some(s) => {
          System.out.println(s)
          val event = s.as[Event]
          System.out.println("event:" + event.event + " songId:" + event.songId)
          val eventService = new EventService()
          eventService.logEvent(event)
        }
        case None => //ignore
      }
    })

    val topology = builder.build()
    System.out.println(topology.describe())
    val streams = new KafkaStreams(topology, props)
    val latch = new CountDownLatch(1)
    Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
      override def run() {
        streams.close()
        latch.countDown()
      }
    });
    
    try {
      streams.start
      latch.await()
    } catch {
        case e: Exception =>
          System.out.print(e.getMessage)
    }
  }
}

object MessageService {
  def main(args: Array[String]): Unit = {
    val m = new MessageService();
    m.addMessageToTopic("abc");
    
    m.streamConsumer()
  }
}