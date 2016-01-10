package mongonotifications

import java.util.concurrent.{Executors, ExecutorService}

import com.rabbitmq.client._
import scala.collection.JavaConversions._

/**
  * Created by amol on 9/1/16.
  */

class RMQWorker extends Runnable{

  def run() = {
    val channel: Channel = RMQConnectionFactory.getChannel()
    val durable = true
    channel.queueDeclare(Constants.queueName, durable, false, false, null)

    val threadPool: ExecutorService = Executors.newCachedThreadPool()

    val consumer: QueueingConsumer = new QueueingConsumer(channel)
    //keeping auto-ack true by default
    channel.basicConsume(Constants.queueName, true, consumer)

    while(true){
      val delivery = consumer.nextDelivery()
      if(delivery != null){
        val body = delivery.getBody

        val runnable: Runnable = new Runnable {
          override def run(): Unit = {
            val message: String = new String(body, "UTF-8")
            println(" [x] Received '" + message + "'")
            SendNotification.processMessage(message)
          }
        }

        //handle messages concurrently using threadpool
        threadPool.submit(runnable)
      }
    }
  }

}
