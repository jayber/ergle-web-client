package services

import play.Logger
import java.util.{Date, Properties}
import javax.mail.search.{ComparisonTerm, ReceivedDateTerm}
import play.api.libs.ws.WS
import javax.inject.{Inject, Singleton, Named}
import play.api.libs.json.{JsValue, Writes, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import java.text.SimpleDateFormat
import java.io.ByteArrayOutputStream
import models.EmailSetting
import scala.util.{Failure, Success}
import javax.mail.{Message, Folder, Session}

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

    importMatchingMessages(inbox, setting)

    inbox.close(true)
    store.close()

  }

  def updateLatestReceivedDate(setting: EmailSetting, receivedDate: Date) {
    val updatedSetting = setting.copy(latestCheckedReceivedDate = receivedDate)
    dataStore.saveEmailSettings(updatedSetting)
  }

  def importMatchingMessages(inbox: Folder, setting: EmailSetting) {
    val sinceDate = setting.latestCheckedReceivedDate
    Logger.debug("searching for messages received later than " + sinceDate)
    val results = inbox.search(new ReceivedDateTerm(ComparisonTerm.GT, sinceDate))
    results.filter(message => {
      //this filter is here because the search doesn't seem to exclude messages from the same day
      Logger.debug(s"filtering message: ${message.getSubject} - ${message.getReceivedDate} - after? ${message.getReceivedDate.after(sinceDate)}")
      message.getReceivedDate.after(sinceDate)
    }).foreach {
      saveEmail(_, setting)
    }
  }

  def saveEmail(message: Message, setting: EmailSetting) = {
    val requestHolder = WS.url(configProvider.config.getString(ConfigProvider.apiUrlKey) + "/emails/")
    Logger.debug(s"sending message: ${message.getSubject} - ${message.getReceivedDate}")
    val jsonMessage = Json.toJson(MessageContainer(message, setting.ownerEmail))
    Logger.debug("sending " + jsonMessage)
    requestHolder.put(jsonMessage).onComplete {
      case Success(response) =>
        response.status match {
          case 200 => Logger.debug("put email success")
            updateLatestReceivedDate(setting, message.getReceivedDate)
          case 413 => Logger.debug(s"${response.status} ${response.statusText}. Do not resend. ${message.getSubject}, ${setting.ownerEmail} ")
            updateLatestReceivedDate(setting, message.getReceivedDate)
          case _ => Logger.error(s"put email error: ${response.status} ${response.statusText} \n ${response.body}")
        }
      case Failure(t) =>
        Logger.error("message sending failed", t)
    }
  }

  case class MessageContainer(message: Message, owner: String)

  implicit val messageWrites = new Writes[MessageContainer] {
    override def writes(messageContainer: MessageContainer): JsValue = {
      Json.obj(
        "owner" -> messageContainer.owner,
        "content" -> parseMessageStream(messageContainer.message)
      )
    }

    def parseMessageStream(message: Message) = {
      val out = new ByteArrayOutputStream()
      message.writeTo(out)
      out.toString("UTF-8")
    }

  }
}

