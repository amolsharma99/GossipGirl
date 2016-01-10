package mongonotifications


import com.rabbitmq.client.{MessageProperties, Channel, Connection, ConnectionFactory}
import play.api.libs.json.Json
import reactivemongo.bson._
import play.modules.reactivemongo.json.BSONFormats._

/**
  * Created by amol on 8/1/16.
  */
object RMQProducer {

  def send(doc: BSONDocument): Unit = {
    val channel: Channel = RMQConnectionFactory.getChannel()
    val durable = true
    channel.queueDeclare(Constants.queueName, durable, false, false, null)
    val msg = Json.toJson(doc)
    channel.basicPublish("", Constants.queueName, null, msg.toString().getBytes())
    channel.close()
  }

}
