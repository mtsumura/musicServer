package models

import play.api.libs.json._

// A simple scala program of enumeration 

object EventType extends Enumeration 
{ 
	type Main = Value 
	
	// Assigning values 
	val first = Value("play") 
	val second = Value("remove") 
	val third = Value("copy") 
	
	implicit val enumReads: Reads[EventType.Value] = EnumUtils.enumReads(EventType)

  implicit def enumWrites: Writes[EventType.Value] = EnumUtils.enumWrites
	
	// Main method 
	def main(args: Array[String]) 
	{ 
		println(s"Event Types = ${EventType.values}") 
	} 
} 

case class Event(songId: String, event: EventType.Value)

object EnumUtils {
  def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = 
    new Reads[E#Value] {
      def reads(json: JsValue): JsResult[E#Value] = json match {
        case JsString(s) => {
          try {
            JsSuccess(enum.withName(s))
          } catch {
            case _: NoSuchElementException =>
               JsError(s"Enumeration expected of type: ${enum.getClass}, but it does not appear to contain the value: ${s}")
          }
        }
        case _ => JsError("String value expected")
      }
  }

  implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = 
    new Writes[E#Value] {
      def writes(v: E#Value): JsValue = JsString(v.toString)
    }

  implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
    Format(enumReads(enum), enumWrites)
  }
}