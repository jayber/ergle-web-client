import org.fluentlenium.core.domain.FluentWebElement
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._


@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {

  "Application" should {

    "enforce login" in new WithBrowser {

      browser.goTo("http://localhost:" + port)

      browser.pageSource must contain("enter email address")

    }
  }


  "allow email login" in new WithBrowser {

    browser.goTo("http://localhost:" + port)
    val emailField: FluentWebElement = browser.findFirst("#emailField")
    private val email = "test@value.com"
    emailField.text(email)
    val signUpButton: FluentWebElement = browser.findFirst("#submit")
    signUpButton.click()
    browser.getCookie("email").getValue must contain(email)
  }

  "allow email login after invalid attempt" in new WithBrowser {

    browser.goTo("http://localhost:" + port)
    val emailField: FluentWebElement = browser.findFirst("#emailField")
    emailField.text("test.value.com")
    val signUpButton: FluentWebElement = browser.findFirst("#submit")
    signUpButton.click()

    private val email = "test@value.com"
    val emailField2: FluentWebElement = browser.findFirst("#emailField")
    emailField2.text(email)
    val signUpButton2: FluentWebElement = browser.findFirst("#submit")
    signUpButton2.click()
    browser.getCookie("email").getValue must contain(email)
  }

}
