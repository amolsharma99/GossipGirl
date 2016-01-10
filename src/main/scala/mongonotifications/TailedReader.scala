package mongonotifications

import play.api.libs.iteratee.Iteratee
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by amol on 8/1/16.
  */

object TailedReader extends App {

  //Start worker
  new Thread(new RMQWorker).start()

  val connection = MongoConnectionFactory.getConnection()
  val db = connection(Constants.dbName)
  val collection = db.collection[BSONCollection](Constants.oplogColl)

  //Fetch the most recent record in oplog.rs collection in local DB.
  val sortLogic = BSONDocument("ts" -> -1)
  val lastRecord = collection.find(BSONDocument()).sort(sortLogic).one[BSONDocument]

  lastRecord.map( optionDoc => optionDoc.map(doc => {
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
      println(s"New Document in oplog.rs: ${BSONDocument.pretty(doc)}")
      //Push this Doc to a Queue in RMQ
      RMQProducer.send(doc)
    }})
  }))

}
