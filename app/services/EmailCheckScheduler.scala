package services

import javax.inject.{Inject, Singleton, Named}
import akka.actor.{ActorRef, Actor, Props, ActorSystem}
import play.Logger
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import models.EmailSetting
import scala.concurrent.Future


@Named
@Singleton
class EmailCheckScheduler {

  @Inject
  var dataStore: DataStore = null
  @Inject
  var emailChecker: EmailChecker = null

  var checkEmailActor: ActorRef = null

  def apply() {
    val period = 1 minute
    val system = ActorSystem("actorSystem")
    if (checkEmailActor == null) {
      checkEmailActor = system.actorOf(Props[CheckEmailActor], "checkEmailActor")
    }
    system.scheduler.schedule(period, period, checkEmailActor, Start(dataStore, emailChecker))
  }
}

case class Start(dataStore: DataStore, emailChecker: EmailChecker)
case class Check(setting: EmailSetting, dataStore: DataStore, emailChecker: EmailChecker)

class CheckEmailActor extends Actor {

  override def receive = {
    case Start(dataStore, emailChecker) => {
      dataStore.listEmailSettings.onSuccess {
        case emailSettings =>
          for (setting <- emailSettings) {
            Future {
              emailChecker.checkEmail(setting)
            }
          }
      }
    }
  }
}


