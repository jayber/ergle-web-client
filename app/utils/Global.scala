package utils

import org.springframework.context.annotation.AnnotationConfigApplicationContext
import play.api.Application
import play.api.GlobalSettings
import services.ScheduleProvider

object Global extends GlobalSettings {

  var ctx: AnnotationConfigApplicationContext = null

  override def onStart(app: Application) {
    super.onStart(app)
    ctx = new AnnotationConfigApplicationContext()
    ctx.scan("controllers", "services")
    ctx.refresh()
    ctx.start()

    scheduleJobs()
  }

  override def onStop(app: Application) {
    super.onStop(app)
  }

  override def getControllerInstance[A](aClass: Class[A]): A = {
    ctx.getBean(aClass)
  }

  def scheduleJobs() {
    val scheduleProvider = ctx.getBean("scheduleProvider").asInstanceOf[ScheduleProvider]
    scheduleProvider.startEmailSchedule()
  }
}



