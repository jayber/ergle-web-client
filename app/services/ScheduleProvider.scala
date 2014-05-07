package services

import javax.inject.{Inject, Singleton, Named}
import scala.concurrent.ExecutionContext.Implicits.global


@Named
@Singleton
class ScheduleProvider {

  @Inject
  var emailCheckScheduler: EmailCheckScheduler = null

  def startEmailSchedule() {
    emailCheckScheduler()
  }
}
