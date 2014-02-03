package controllers

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import utils.{Global, DIContext}
import javax.inject.{Inject, Named, Singleton}

@Named
@Singleton
class Colibri {
  def operation = "Default"
}

@Named
@Singleton
class Client {
  @Inject
  var coll: Colibri = null

  def doOperation = coll.operation
}

class TestSpec extends Specification with Mockito {

  "DIContext" should {
    "make test use different dependency injection context" in new DIContext(Global.ctx) {

      val coll: Colibri = mock[Colibri]
      coll.operation returns "Mock"

      ctx put(classOf[Colibri], coll)

      var client = Global.ctx.getBean(classOf[Client])

      //      client.doOperation must equalTo("Mock")
    }
  }
}

