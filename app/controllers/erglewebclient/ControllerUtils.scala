package controllers.erglewebclient

import play.api.mvc.{AnyContent, Request}

trait ControllerUtils {

  val cookieName = "email"

  def getEmail(implicit request: Request[AnyContent]): Option[String] = {
    request.cookies.get(cookieName).map {
      _.value
    }
  }
}
