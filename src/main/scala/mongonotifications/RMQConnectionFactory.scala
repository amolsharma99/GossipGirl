package mongonotifications

import com.rabbitmq.client.{AlreadyClosedException, ConnectionFactory, Channel, Connection}

/**
  * Created by amol on 9/1/16.
  */
object RMQConnectionFactory {

  private var connection: Connection = initConnection()

  private def initConnection(): Connection = {
    //more things like username, password, timeout and other connection attributes can be added.
    val rmqHost: String = Constants.queueUrl
    val factory = new ConnectionFactory()
    factory.setHost(rmqHost)
    factory.newConnection()
  }

  /**
    * returns new Channel everytime,
    */
  def getChannel(): Channel = {
    try {
      connection.createChannel()
    } catch {
      case e: AlreadyClosedException => {
        //retry in connection closed due to any reason.
        resetConnection
        getChannel()
      }
    }
  }

  def resetConnection = {
    connection = initConnection()
  }

  def closeConnection = {
    connection.close()
  }

}
