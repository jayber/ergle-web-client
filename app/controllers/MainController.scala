package controllers

import play.api.mvc._

object MainController extends Controller {

  def index = Action {
    TemporaryRedirect("/login")
  }

  def login = Action {
    Ok(views.html.main("login")(views.html.login()))
  }

}