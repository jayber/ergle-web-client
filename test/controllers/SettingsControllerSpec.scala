package controllers

import org.specs2.mutable.Specification
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._
import play.api.mvc.Cookie
import org.scalatest.FlatSpec
import org.scalatest.mock.MockitoSugar
import controllers.erglewebclient.SettingsController
import services.DataStore
import org.mockito.Mockito._
import models.EmailSetting
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

class SettingsControllerSpec extends Specification {

  "Settings" should {

    "be accessible" in new WithApplication {
      val email = "test@test.com"

      val settings = route(FakeRequest(GET, "/settings").withCookies(Cookie("email", email))).get

      status(settings) must equalTo(OK)

      contentAsString(settings) must contain("Update settings")
    }

    "be validated" in new WithApplication {
      val email = "test@test.com"

      val settings = route(FakeRequest(POST, "/settings").withCookies(Cookie("email", email))).get

      status(settings) must equalTo(BAD_REQUEST)
    }

    "be saved" in new WithApplication {
      val email = "test@test.com"

      val settings = route(FakeRequest(POST, "/settings").withCookies(Cookie("email", email)).withFormUrlEncodedBody(
        ("serverAddress","serverAddress"),("userName","userName"),("password","password")
      )).get

      status(settings) must equalTo(OK)

      contentAsString(settings) must contain("Settings updated")
    }
  }
}

class SaveSettingSpec extends FlatSpec with MockitoSugar {

  "Settings" should "persist changes to the DataStore" in {
    val email = "test@test.com"

    val request = FakeRequest(POST, "/settings").withCookies(Cookie("email", email)).withFormUrlEncodedBody(
      ("serverAddress","serverAddress"),("userName","userName"),("password","password")
    )
    val dataStore = mock[DataStore]
    val controller = new SettingsController
    controller.dataStore = dataStore

    val result = controller.save(request)

    status(result) // waits for async action to execute

    verify(dataStore).saveEmailSettings(EmailSetting(email, "serverAddress", "userName", "password"))

  }
}