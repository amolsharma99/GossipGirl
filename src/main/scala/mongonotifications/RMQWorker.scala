package mongonotifications

import com.rabbitmq.client._

/**
  * Created by amol on 9/1/16.
  */

object RMQWorker {

  val queueName = "mongo_notifications"
  var connection: Connection = null

  def init() = {
    //UserName, Password, Ports can be maneged here.
    val factory: ConnectionFactory = new ConnectionFactory()
    factory.setHost("localhost")
    connection = factory.newConnection()
  }

  def start() = {
    val channel: Channel = connection.createChannel()
    val durable = true
    channel.queueDeclare(queueName, durable, false, false, null)

    //Implemented consumer as callback in form of an object.
    val consumer: Consumer = new DefaultConsumer(channel) {
      @Override
      def handleDelivery(consumerTag: String, envelope: Envelope, properties: AMQP.BasicProperties,
                         body: Array[java.lang.Byte]) = {
        val message: String =  new String(body, "UTF-8")
        println(" [x] Received '" + message + "'")
        //Process this and send to clients
      }
    }

    channel.basicConsume(queueName, true, consumer);
  }

  def end() = {
    if (connection != null)
      connection.close()
  }

}
