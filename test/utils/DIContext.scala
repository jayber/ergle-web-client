package utils

import org.specs2.execute.AsResult
import scala.collection.mutable
import play.api.test.WithApplication
import org.springframework.context.ConfigurableApplicationContext

class DIContext(context: ConfigurableApplicationContext) extends WithApplication {

  val ctx = mutable.Map[Class[_], AnyRef]()

  override def around[T: AsResult](t: => T) = {
    //    context.
    println("start around")
    val result = super.around(t)
    println("end around")
    result
  }
}

