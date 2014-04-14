package services

import javax.inject.{Singleton, Named}
import models.EmailSetting
import reactivemongo.api.MongoDriver
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONHandler
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson._
import java.util.Date


@Named
@Singleton
class DataStore {

  val driver = new MongoDriver
  val connection = driver.connection(List("localhost"))
  val db = connection("ergle")
  val collection = db[BSONCollection]("imapEmailSettings")

  val mapBSONDocumentToEmailSetting: (BSONDocument) => EmailSetting = document => EmailSetting(document.get("ownerEmail").get.asInstanceOf[BSONString].value,
    document.get("accountServerAddress").get.asInstanceOf[BSONString].value,
    document.get("accountUsername").get.asInstanceOf[BSONString].value,
    document.get("accountPassword").get.asInstanceOf[BSONString].value,
    new Date(document.get("latestCheckedReceivedDate").map(_.asInstanceOf[BSONDateTime].value).getOrElse(System.currentTimeMillis())))

  def find(ownerEmail: String) = {
    collection.find(BSONDocument(
      "ownerEmail" -> ownerEmail
    )).cursor.headOption.map {
      result => result.map(mapBSONDocumentToEmailSetting)
    }
  }

  def listEmailSettings = {
    collection.find(BSONDocument()).cursor.collect[Array]().map(values => values.map(mapBSONDocumentToEmailSetting))
  }

  /* todo: all these values should be encrypted in a real system */
  def saveEmailSettings(emailSettings: EmailSetting) {
    collection.update(BSONDocument(
      "ownerEmail" -> emailSettings.ownerEmail
    ), BSONDocument(
      "ownerEmail" -> emailSettings.ownerEmail,
      "accountServerAddress" -> emailSettings.accountServerAddress,
      "accountUsername" -> emailSettings.accountUsername,
      "accountPassword" -> emailSettings.accountPassword,
      "latestCheckedReceivedDate" -> BSONDateTime(emailSettings.latestCheckedReceivedDate.getTime)
    ), upsert = true)
  }
}
