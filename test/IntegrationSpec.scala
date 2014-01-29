import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

  "Application" should {

    "enforce login" in new WithBrowser {

      browser.goTo("http://localhost:" + port)

      browser.pageSource must contain("enter email address")
    }
  }
}
