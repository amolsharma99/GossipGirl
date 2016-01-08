package mongonotifications

import reactivemongo.api._
import reactivemongo.bson._
import play.api.libs.iteratee.Iteratee
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.api.collections.bson.BSONCollection

/**
  * Created by amol on 8/1/16.
  */

object TailedReader extends App {

  RMQWorker.start()

  val connection = getConnection()
  val db = connection("local")
  val collection = db.collection[BSONCollection]("oplog.rs")

  //Fetch the most recent record in oplog.rs collection in local DB.
  val sortLogic = BSONDocument("ts" -> -1)
  val lastRecord = collection.find(BSONDocument()).sort(sortLogic).one[BSONDocument]

  lastRecord.map( optionDoc => optionDoc.map(doc => {
    println(s"Last Document inserted: ${BSONDocument.pretty(doc)}")

    val lastTimestamp =  doc.get("ts") match {
      case Some(y) =>  y.asInstanceOf[BSONTimestamp]
      case None => new BSONTimestamp(0)
    }

    //inside map since can be executed only after lastTimeStamp is Initialised
    val query = BSONDocument("ts" -> BSONDocument("$gt" -> lastTimestamp.asInstanceOf[BSONValue]))
    //this is a tailable cursor
    val cursor = collection
      .find(query)
      .options(QueryOpts().tailable.awaitData)
      .cursor[BSONDocument]

    println("== open tailable cursor==")

    cursor.enumerate().apply(Iteratee.foreach { doc => {
      println(s"Document inserted: ${BSONDocument.pretty(doc)}")
      //Push this Doc to a Queue in RMQ
      RMQProducer.send(doc)
    }})

    //Finally close open connections
    RMQProducer.end()
    RMQWorker.end()
  }))

  def getConnection(): MongoConnection = {
    val driver = new MongoDriver
    val mongoUrl = "localhost"
    return driver.connection(List(mongoUrl))
  }

}
