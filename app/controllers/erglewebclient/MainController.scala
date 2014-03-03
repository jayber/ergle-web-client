package controllers.erglewebclient

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

  def index(owner: String) = Action.async {
    def ownerMatch(email: String): Future[SimpleResult] = {
      owner match {
        case "" =>
          Future {
            Redirect("/" + email)
          }
        case _ => indexWithEntries(email, owner)
      }
    }

    implicit request: Request[AnyContent] =>
      getEmail match {
        case Some(email) => ownerMatch(email)
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

  def indexWithEntries(email: String, owner: String): Future[SimpleResult] = Future {
    Ok(views.html.template("ergle", email, views.html.index(owner)))
  }

  def showLogin = Action {

    val emailForm = Form(
      single(
        "email" -> email
      )
    )
    Ok(views.html.template("login", "", views.html.login(emailForm, "")))
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
          BadRequest(views.html.template("login error", "", views.html.login(emailForm, "invalid email address")))
        },
        userData => {
          //          loginService.login(userData)
          Redirect("/").withCookies(Cookie(cookieName, userData, Some(60 * 60 * 24 * 365)))
        })
  }
}