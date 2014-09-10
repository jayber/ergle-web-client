package controllers.erglewebclient

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._
import javax.inject.{Singleton, Named, Inject}
import play.api.templates.{Html, HtmlFormat}

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.Cookie
import scala.Some
import play.api.mvc.SimpleResult


@Named
@Singleton
class MainController extends Controller with ControllerUtils{

  def multipleTimelines(owners: String) = Action { implicit request =>

    getEmail match {
      case Some(email) =>
        renderWithNav(email, owners, request, false)
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
    renderWithNav(email, owner, request, showForm = true)
  }

  def renderWithNav(email: String, ownersStr: String, request: Request[AnyContent], showForm: Boolean): SimpleResult = {
    val owners = ownersStr.split('&')
    val zoom = request.getQueryString("zoom").getOrElse("details")
    def getContent: Html = {
      zoom match {
        case "details" => views.html.details(owners, request.rawQueryString)
        case _ => views.html.zoom(owners, request.rawQueryString)
      }
    }
    val content = showForm match {
      case true => views.html.withCreateForm(getContent, request.rawQueryString)
      case false => getContent
    }
    Ok(views.html.template("ergle - " + ownersStr, email, views.html.withNav(content, zoom)))
  }

  def showLogin = Action { request =>
    val emailForm = Form(
      single(
        "email" -> email
      )
    )
    Ok(views.html.template("login", "", views.html.withOutNav(views.html.login(emailForm, ""))))
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
          BadRequest(views.html.template("login error", "", views.html.withOutNav(views.html.login(emailForm, "invalid email address"))))
        },
        userData => {
          //          loginService.login(userData)
          Redirect("/").withCookies(Cookie(cookieName, userData, Some(60 * 60 * 24 * 365)))
        })
  }
}