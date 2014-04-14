package controllers.erglewebclient

import play.api.mvc.{Controller, Action}
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import javax.inject.{Inject, Singleton, Named}
import services.{EmailCheckScheduler, DataStore}
import play.api.data._
import play.api.data.Forms._
import models.EmailSetting

case class SettingsFromForm(serverAddress: String, userName: String, password: String)

@Named
@Singleton
class SettingsController extends Controller with ControllerUtils {

  @Inject
  var emailCheckScheduler: EmailCheckScheduler = null

  @Inject
  var dataStore: DataStore = null

  val emailSettingsForm = Form(
    mapping(
      "serverAddress" -> nonEmptyText,
      "userName" -> nonEmptyText,
      "password" -> nonEmptyText
    )(SettingsFromForm.apply)(SettingsFromForm.unapply)
  )

  def show = Action.async {
    implicit request =>
        dataStore.find(getEmail.get).map { settingOption =>
          val formSetting = settingOption match {
            case Some(setting) => SettingsFromForm(setting.accountServerAddress, setting.accountUsername, setting.accountPassword)
            case _ => SettingsFromForm("","","")
          }
        Ok(views.html.template("settings - ergle", getEmail.get, views.html.settings(emailSettingsForm.fill(formSetting))))
      }
  }

  def save = Action.async {
    implicit request =>
      Future {
        emailSettingsForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.template("settings - ergle", getEmail.get, views.html.settings(formWithErrors))),
          settings => {
            val emailSetting = EmailSetting(getEmail.get, settings.serverAddress, settings.userName, settings.password)
            dataStore.saveEmailSettings(emailSetting)
            emailCheckScheduler(emailSetting)
            Ok(views.html.template("settings - ergle", getEmail.get, views.html.settings(emailSettingsForm.fill(settings),"Settings updated")))
          }
        )
      }
  }
}
