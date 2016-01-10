package mongonotifications

import reactivemongo.api.{MongoConnection, MongoDriver}

/**
  * Created by amol on 10/1/16.
  */
object MongoConnectionFactory {

  def getConnection(): MongoConnection = {
    val driver = new MongoDriver
    return driver.connection(List(Constants.dbHost))
  }


}
