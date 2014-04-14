package services

import javax.inject.{Inject, Singleton, Named}
import scala.concurrent.ExecutionContext.Implicits.global


@Named
@Singleton
class ScheduleProvider {

  @Inject
  var emailCheckScheduler: EmailCheckScheduler = null

  @Inject
  var dataStore: DataStore = null

  def startEmailSchedule() {

    val emailSettings = dataStore.listEmailSettings

    emailSettings.map {
      _.foreach { settings =>
        emailCheckScheduler(settings)
      }
    }
  }
}
