package services

import models.EmailSetting
import play.Logger
import java.util.{Date, Properties}
import javax.mail.{Part, Message, Folder, Session}
import javax.mail.search.{ComparisonTerm, ReceivedDateTerm}
import play.api.libs.ws.WS
import javax.inject.{Inject, Singleton, Named}
import play.api.libs.json.{JsString, JsValue, Writes, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import java.text.SimpleDateFormat
import java.io.InputStream

@Named
@Singleton
class EmailChecker {

  @Inject
  var configProvider: ConfigProvider = null

  def checkEmail(setting: EmailSetting, dataStore: DataStore) {
    Logger.debug("checking email")
    val props = new Properties()

    val session = Session.getDefaultInstance(props)

    val store = session.getStore("imaps")
    store.connect(setting.accountServerAddress, setting.accountUsername, setting.accountPassword)

    val inbox = store.getFolder("INBOX")
    inbox.open(Folder.READ_ONLY)

    importMatchingMessages(inbox, setting.latestCheckedReceivedDate, setting.ownerEmail)

    updateLatestReceivedDate(dataStore, setting, inbox)

    inbox.close(true)
    store.close()

  }

  def updateLatestReceivedDate(dataStore: DataStore, setting: EmailSetting, inbox: Folder) {
    inbox.getMessageCount match {
      case 0 =>
      case num =>
        val latestMessage = inbox.getMessage(num)
        val updatedSetting = setting.copy(latestCheckedReceivedDate = latestMessage.getReceivedDate)
        dataStore.saveEmailSettings(updatedSetting)
    }
  }

  def importMatchingMessages(inbox: Folder, sinceDate: Date, ownerEmail: String) {
    Logger.debug("searching for messages received later than " + sinceDate)
    val results = inbox.search(new ReceivedDateTerm(ComparisonTerm.GT, sinceDate))
    results.filter(
      //this filter is here because the search doesn't seem to exclude messages from the same day
      _.getReceivedDate.after(sinceDate)).foreach {
      message =>
        val requestHolder = WS.url(configProvider.config.getString(ConfigProvider.apiUrlKey) + "/emails/")
        val jsonMessage = Json.toJson(message)
        Logger.debug("sending " + jsonMessage)
        requestHolder.put(jsonMessage).foreach(_ => Logger.debug("put email"))
    }
  }

  implicit val messageWrites = new Writes[Message] {
    val dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS z")
    override def writes(message: Message): JsValue = {
      Json.obj(
        "from" ->
          message.getFrom.map(address => Json.obj("address" -> address.toString))
        ,
        "recipients" ->
          message.getAllRecipients.map(address => Json.obj("address" -> address.toString))
        ,
        "subject" -> message.getSubject,
        "content" -> writeImputStream(message.getInputStream) ,
        "receivedDate" -> dateFormat.format(message.getReceivedDate),
        "replyTo" -> message.getReplyTo.map(address => Json.obj("address" -> address.toString)),
        "sentDate" -> dateFormat.format(message.getSentDate)
      )
    }

    def writeImputStream(o: InputStream): JsValue = {
      val builder = new StringBuilder
      var i: Int = o.read
      while ( i != -1) {
        builder.append(i.asInstanceOf[Char])
        i = o.read
      }
      new JsString(builder.toString())
    }
  }
}

