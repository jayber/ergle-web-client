package controllers

import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

object MainController extends Controller {

  def index = Action {
    TemporaryRedirect("/login")
  }

  def showLogin = Action {
    Ok(views.html.main("login")(views.html.login()))
  }

  val emailForm = Form(
    single(
      "email" -> email
    )
  )

  def login() = Action {
    /*implicit request =>

      emailForm.bindFromRequest().fold(
        formWithErrors => {
          BadRequest(views.html.index(page, views.html.product(), views.html.register()))
        },
        userData => {
          registrationService.registerEmail(userData)
          getOK(page, views.html.product(), views.html.registered())
        })*/
    TemporaryRedirect("/")
  }

}