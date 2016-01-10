package mongonotifications

import play.api.libs.json.{JsValue, Json}

/**
  * Created by amol on 10/1/16.
  */

/**
  * Send Notification read from worker to client
  *
  * In oplog msg we are concerned with the following fields -
  * 1. op(operation type) - 'i; for insert, 'u' for update, 'd' for delete, 'c' for command
  * 2. ns(namespace affected) - will gives us the db/collection affected.
  * 3. o - change made
  * 4. o2 - present only for update operations, gives the update criteria.
  */

object SendNotification {

  def processMessage(message: String) = {
    //See if any client is interested in this and send to relevant clients.
    val json: JsValue = Json.parse(message)
    val op = json.\("op").toString().replaceAll("\"", "") //remove quotes from json string
    val ns = json.\("ns").toString().replaceAll("\"", "")
    val o = json.\("o").toString().replaceAll("\"", "")
    op match {
      case "i" => {
       println("insert operation: " + o)
      }
      case "d" => {
        println("delete operation: " + o)
      }
      case "u" => {
        println("update operation: " + o)
        val o2 = json.\("o2").toString().replaceAll("\"", "")
        println("update Criteria: " + o2)
      }
      case _ => {
        println("Not interested in this operation")
      }
    }
  }

}
