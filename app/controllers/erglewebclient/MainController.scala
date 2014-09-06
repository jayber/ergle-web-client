package controllers.erglewebclient

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import javax.inject.{Singleton, Named, Inject}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Cookie
import scala.Some
import play.api.mvc.SimpleResult


@Named
@Singleton
class MainController extends Controller with ControllerUtils{

  def multipleTimelines(owners: String) = Action { implicit request =>

    val ownerEmails = owners.split('&')
    getEmail match {
      case Some(email) =>
        val zoom = request.getQueryString("zoom").getOrElse("details")
        Ok(views.html.template("ergle - "+owners, email, views.html.multiple(ownerEmails, request.rawQueryString), zoom))
      case _ =>
          Redirect("/login")

    }
  }

  def index(owner: String) = Action.async {
    def ownerMatch(email: String, request: Request[AnyContent]): Future[SimpleResult] = {
      owner match {
        case "" =>
          Future {
            Redirect("/" + email)
          }
        case _ => indexWithEntries(email, owner, request)
      }
    }

    implicit request: Request[AnyContent] =>
      getEmail match {
        case Some(email) => ownerMatch(email, request)
        case _ =>
          Future {
            Redirect("/login")
          }
      }
  }

  def indexWithEntries(email: String, owner: String, request: Request[AnyContent]): Future[SimpleResult] = Future {
    val zoom = request.getQueryString("zoom").getOrElse("details")
    println("zoom="+zoom)
    Ok(views.html.template("ergle - "+owner, email, views.html.main(owner, request.rawQueryString), zoom))
  }

  def showLogin = Action { request =>

    val emailForm = Form(
      single(
        "email" -> email
      )
    )

    val zoom = request.getQueryString("zoom").getOrElse("details")
    Ok(views.html.template("login", "", views.html.login(emailForm, ""), zoom))
  }

  def login = Action {
    implicit request =>

      val emailForm = Form(
        single(
          "email" -> email
        )
      )
      val zoom = request.getQueryString("zoom").getOrElse("details")

      emailForm.bindFromRequest().fold(
        formWithErrors => {
          BadRequest(views.html.template("login error", "", views.html.login(emailForm, "invalid email address"), zoom))
        },
        userData => {
          //          loginService.login(userData)
          Redirect("/").withCookies(Cookie(cookieName, userData, Some(60 * 60 * 24 * 365)))
        })
  }
}