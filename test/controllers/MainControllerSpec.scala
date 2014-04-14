package controllers

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Cookie
import org.specs2.mock.Mockito


@RunWith(classOf[JUnitRunner])
class MainControllerSpec extends Specification with Mockito {

  "Application" should {

    "redirect to the login page" in new WithApplication {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(SEE_OTHER)
      redirectLocation(home) must beSome.which(_ == "/login")
    }

    "not redirect to the login page if already logged on" in new WithApplication {

      val email = "test@test.com"
      val home = route(FakeRequest(GET, "/"+email).withCookies(Cookie("email", email))).get

      status(home) must equalTo(OK)

    }

    "allow login" in new WithApplication {
      val email = "test@test.com"
      val home = route(FakeRequest(POST, "/login").withFormUrlEncodedBody(("email", email))).get

      status(home) must equalTo(SEE_OTHER)
      redirectLocation(home) must beSome.which(_ == "/")
      cookies(home).get("email") must beSome.which(_.value == email)
    }

    "fail invalid email login" in new WithApplication {
      val email = "test.test.com"
      val home = route(FakeRequest(POST, "/login").withFormUrlEncodedBody(("email", email))).get

      status(home) must equalTo(BAD_REQUEST)
      contentAsString(home) must contain("invalid email address")
    }
  }
}

