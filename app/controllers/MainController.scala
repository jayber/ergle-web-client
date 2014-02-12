package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import javax.inject.{Singleton, Named, Inject}
import services.EntryService
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext


@Named
@Singleton
class MainController extends Controller {

  @Inject
  var entryService: EntryService = null

  val cookieName = "email"

  def index = Action.async {
    implicit request: Request[AnyContent] =>
      getEmail match {
        case Some(email) => indexWithEntries(email)
        case _ =>
          Future {
            Redirect("/login")
          }

      }
  }

  def getEmail(implicit request: Request[AnyContent]): Option[String] = {
    request.cookies.get(cookieName).map {
      _.value
    }
  }

  def indexWithEntries(email: String) = Future {
    Ok(views.html.main("ergle", views.html.index()))
  }

  def showLogin = Action {

    val emailForm = Form(
      single(
        "email" -> email
      )
    )
    Ok(views.html.main("login", views.html.login(emailForm, "")))
  }

  def login = Action {
    implicit request =>

      val emailForm = Form(
        single(
          "email" -> email
        )
      )

      emailForm.bindFromRequest().fold(
        formWithErrors => {
          BadRequest(views.html.main("login error", views.html.login(emailForm, "invalid email address")))
        },
        userData => {
          //          loginService.login(userData)
          Redirect("/").withCookies(Cookie(cookieName, userData, Some(60 * 60 * 24 * 365)))
        })
  }
}