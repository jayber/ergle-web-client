package services

import javax.inject.{Inject, Singleton, Named}
import akka.actor.{Actor, Props, ActorSystem}
import play.Logger
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import models.EmailSetting


@Named
@Singleton
class EmailCheckScheduler {

  @Inject
  var dataStore: DataStore = null
  @Inject
  var emailChecker: EmailChecker = null

  def apply() {
    val period = 1 minute
    val system = ActorSystem("actorSystem")
    val checkEmailActor = system.actorOf(Props[CheckEmailActor], "checkEmailActor")
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
            checkAccountEmail(setting, dataStore, emailChecker)
          }
      }
    }
  }

  def checkAccountEmail(setting: EmailSetting, dataStore: DataStore, emailChecker: EmailChecker) = {
    val system = ActorSystem("actorSystem")
    val accountCheckEmailActor = system.actorOf(Props[AccountCheckEmailActor], "accountCheckEmailActor")
    accountCheckEmailActor ! Check(setting, dataStore, emailChecker)
  }
}

class AccountCheckEmailActor extends Actor {
  override def receive: Actor.Receive = {
    case Check(setting, dataStore, emailChecker) => {
      emailChecker.checkEmail(setting)
    }
  }
}

