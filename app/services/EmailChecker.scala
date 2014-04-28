package services

import models.EmailSetting
import play.Logger
import java.util.{Date, Properties}
import javax.mail._
import javax.mail.search.{ComparisonTerm, ReceivedDateTerm}
import play.api.libs.ws.WS
import javax.inject.{Inject, Singleton, Named}
import play.api.libs.json.{JsString, JsValue, Writes, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import java.text.SimpleDateFormat
import java.io.{ByteArrayOutputStream, InputStream}
import play.api.libs.json.JsString
import models.EmailSetting
import scala.util.{Failure, Success}

@Named
@Singleton
class EmailChecker {

  @Inject
  var configProvider: ConfigProvider = null

  @Inject
  var dataStore: DataStore = null

  def checkEmail(setting: EmailSetting) {
    Logger.debug("checking email")
    val props = new Properties()

    val session = Session.getDefaultInstance(props)

    val store = session.getStore("imaps")
    store.connect(setting.accountServerAddress, setting.accountUsername, setting.accountPassword)

    val inbox = store.getFolder("INBOX")
    inbox.open(Folder.READ_ONLY)

    importMatchingMessages(inbox, setting, session)

    inbox.close(true)
    store.close()

  }

  def updateLatestReceivedDate(setting: EmailSetting, receivedDate: Date) {
    val updatedSetting = setting.copy(latestCheckedReceivedDate = receivedDate)
    dataStore.saveEmailSettings(updatedSetting)
  }

  def importMatchingMessages(inbox: Folder, setting: EmailSetting, session: Session) {
    val sinceDate = setting.latestCheckedReceivedDate
    val ownerEmail = setting.ownerEmail
    Logger.debug("searching for messages received later than " + sinceDate)
    val results = inbox.search(new ReceivedDateTerm(ComparisonTerm.GT, sinceDate))
    results.filter(message => {
      //this filter is here because the search doesn't seem to exclude messages from the same day
      Logger.debug(s"filtering message: ${message.getSubject} - ${message.getReceivedDate} - after? ${message.getReceivedDate.after(sinceDate)}")
      message.getReceivedDate.after(sinceDate)
    }).foreach {
      message =>
        val requestHolder = WS.url(configProvider.config.getString(ConfigProvider.apiUrlKey) + "/emails/").withRequestTimeout(1000 * 10)
        Logger.debug(s"sending message: ${message.getSubject} - ${message.getReceivedDate}")
        val jsonMessage = Json.toJson(MessageContainer(message, ownerEmail))
        Logger.debug("sending " + jsonMessage)
        requestHolder.put(jsonMessage).onComplete {
          case Success(response) =>
            response.status match {
              case 200 => Logger.debug("put email success")
                updateLatestReceivedDate(setting, message.getReceivedDate)
              case _ => Logger.error(s"put email error: ${response.status} ${response.statusText} \n ${response.body}")
            }
          case Failure(t) =>
            Logger.error("message sending failed", t)
        }
    }
  }

  case class MessageContainer(message: Message, owner: String)

  implicit val messageWrites = new Writes[MessageContainer] {
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

    override def writes(messageContainer: MessageContainer): JsValue = {

      Json.obj(
        "owner" -> messageContainer.owner,
        "from" ->
          messageContainer.message.getFrom.map(address => address.toString)
        ,
        "recipients" ->
          messageContainer.message.getAllRecipients.map(address => address.toString)
        ,
        "subject" -> messageContainer.message.getSubject,
        "content" -> writeInputStream(messageContainer.message.getInputStream),
        "receivedDate" -> dateFormat.format(messageContainer.message.getReceivedDate),
        "replyTo" -> messageContainer.message.getReplyTo.map(address => address.toString),
        "sentDate" -> dateFormat.format(messageContainer.message.getSentDate)
      )
    }


    def writeInputStream(o: InputStream): JsValue = {
      val builder = new StringBuilder
      var i: Int = o.read
      while (i != -1) {
        builder.append(i.asInstanceOf[Char])
        i = o.read
      }
      new JsString(builder.toString())
    }
  }
}

