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

  def apply(setting: EmailSetting) {
    doPeriodically(setting, dataStore, 1 minute)
  }

  def doPeriodically(setting: EmailSetting, dataStore: DataStore, period: FiniteDuration) {
    val system = ActorSystem("doPeriodicallySystem")
    val periodicActor = system.actorOf(Props[CheckEmailActor], "doPeriodicallyActor")
    periodicActor ! Start(setting, dataStore, period, emailChecker)
  }
}

case class Start(setting: EmailSetting, dataStore: DataStore, period: FiniteDuration, emailChecker: EmailChecker)

class CheckEmailActor extends Actor {
  override def receive = {
    case Start(setting, dataStore, period, emailChecker) => {
      doIfAccountStillValid(setting, dataStore, period, emailChecker)
    }
  }

  def doIfAccountStillValid(setting: EmailSetting, dataStore: DataStore, period: FiniteDuration, emailChecker: EmailChecker) = {
    val resultFuture = dataStore.find(setting.ownerEmail)
    resultFuture.map {
      case Some(newSetting) if setting.nearlyEqual(newSetting) => {
        try {
          emailChecker.checkEmail(newSetting)
        } finally {
          val resultFuture = dataStore.find(setting.ownerEmail)
          resultFuture.onSuccess {
            case Some(result) => rescheduleCheck(result, dataStore, period, emailChecker)
          }
        }
      }
      case _ => Logger.debug("setting is gone, not running or rescheduling")
    }
  }

  def rescheduleCheck(setting: EmailSetting, dataStore: DataStore, period: FiniteDuration, emailChecker: EmailChecker) {
    val system = ActorSystem("doPeriodicallySystem")
    val scheduleActor = system.actorOf(Props[ScheduleCheckEmailActor], "schedulePeriodicallyActor")
    scheduleActor ! Start(setting, dataStore, period, emailChecker)
  }
}

class ScheduleCheckEmailActor extends Actor {
  override def receive: Actor.Receive = {
    case Start(action, dataStore, period, emailChecker) => {
      val system = ActorSystem("doPeriodicallySystem")
      val periodicActor = system.actorOf(Props[CheckEmailActor], "doPeriodicallyActor")
      system.scheduler.scheduleOnce(period, periodicActor, Start(action, dataStore, period, emailChecker))
    }
  }
}

