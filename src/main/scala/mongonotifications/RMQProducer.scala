package mongonotifications


import com.rabbitmq.client.{MessageProperties, Channel, Connection, ConnectionFactory}
import play.api.libs.json.Json
import reactivemongo.bson._
import play.modules.reactivemongo.json.BSONFormats._

/**
  * Created by amol on 8/1/16.
  */
object RMQProducer {

  val queueName = "mongo_notifications"
  var connection: Connection = null

  def init() = {
    //UserName, Password, Ports can be maneged here.
    val factory: ConnectionFactory = new ConnectionFactory()
    factory.setHost("localhost")
    connection = factory.newConnection()
  }

  def send(doc: BSONDocument): Unit = {
    //will be called once on first call
    if(connection == null)
      init()

    val channel: Channel = connection.createChannel()
    val durable = true
    channel.queueDeclare(queueName, durable, false, false, null)
    val msg = Json.toJson(doc)
//    channel.basicPublish("", queueName, null, doc.toString().getBytes())
    println("Pushed to Queue " + queueName + " message: " + msg.toString())
    channel.close()
  }

  def end() = {
    if (connection != null)
      connection.close()
  }
}
