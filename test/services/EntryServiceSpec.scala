package services

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import org.specs2.matcher.ThrownExpectations
import play.api.libs.ws.WS.WSRequestHolder
import com.typesafe.config.Config
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.ws.Response
import ExecutionContext.Implicits.global

class EntryServiceSpec extends Specification with Mockito {

  "Entry service" should {
    "retrieve entries for email address" in new EntryService with Scope with ThrownExpectations {

      val email: String = "test@test.com"

      configProvider = mock[ConfigProvider]
      val config: Config = mock[Config]
      configProvider.config returns config
      config.getString(ConfigProvider.apiUrlKey) returns "testUrl"

      val requestHolder = mock[WSRequestHolder]
      requestHolder.withQueryString(("email", email)) returns requestHolder
      requestHolder.get() returns Future {
        new Response(null)
      }

      entries(email)

      there was one(requestHolder).get()

      override def url(url: String) = {
        requestHolder
      }
    }
  }
}
