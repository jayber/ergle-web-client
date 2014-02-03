package controllers

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Cookie
import org.specs2.mock.Mockito
import services.EntryService
import utils.Global
import play.api.libs.json.{JsString, JsValue}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import org.specs2.execute.AsResult
import org.specs2.matcher.ThrownExpectations


@RunWith(classOf[JUnitRunner])
class MainControllerSpec extends Specification with Mockito {

  "Application" should {

    "send 404 on a bad request" in new WithApplication {
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "redirect to the login page" in new WithApplication {
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(SEE_OTHER)
      redirectLocation(home) must beSome.which(_ == "/login")
    }

    "not redirect to the login page if already logged on" in new WithApplication {
      new FakeEntryService {

        val home = route(FakeRequest(GET, "/").withCookies(Cookie("email", "test@test.com"))).get
        status(home) must equalTo(OK)
      }
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

    "retrieve entries" in new WithApplication {

      val email = "test@test.com"
      val service = mock[EntryService]
      service.entries(email) returns (new MockEntryService).entries(email)

      new FakeEntryService(service) with ThrownExpectations {

        val home = route(FakeRequest(GET, "/").withCookies(Cookie("email", email))).get.map {
          result =>
            there was one(service).entries(email)
            result
        }

        status(home) must equalTo(OK)

      }
    }
  }
}

class MockEntryService extends EntryService {
  override def entries(email: String): Future[JsValue] = Future {
    JsString("")
  }
}

class FakeEntryService(service: EntryService = new MockEntryService) extends Around {
  def around[T: AsResult](t: => T) = {
    val mainController = Global.ctx.getBean(classOf[MainController])
    mainController.entryService = service

    AsResult(t)
  }
}