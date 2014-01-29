package controllers

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._


@RunWith(classOf[JUnitRunner])
class MainControllerSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "redirect to the login page" in new WithApplication {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(TEMPORARY_REDIRECT)
      redirectLocation(home) must beSome.which(_ == "/login")
    }

    "allow login" in new WithApplication {
      val email = "test@test.com"
      val home = route(FakeRequest(POST, "/login").withFormUrlEncodedBody(("email", email))).get

      status(home) must equalTo(TEMPORARY_REDIRECT)
      redirectLocation(home) must beSome.which(_ == "/")
      cookies(home).get("email") must beSome.which(_.value == email)
    }

    "fail invalid email login" in new WithApplication {
      val email = "test.test.com"
      val home = route(FakeRequest(POST, "/login").withFormUrlEncodedBody(("email", email))).get

      status(home) must equalTo(BAD_REQUEST)
      contentAsString(home) must contain("invalid email")
    }
  }
}
