package virtualcss

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

object VirtualCssController extends Controller {

  def virtualCssJs(cssPath: String) =
    Action.async {
      request =>
        val jsFuture = VirtualCss.jsForCss(getURLforPath(request, cssPath))
        jsFuture.map(jsTextAndStatements => Ok(views.html.jsTemplate(jsTextAndStatements._2, jsTextAndStatements._1)).as("text/javascript"))
    }

  def virtualCss(cssPath: String) = Action.async {
    request =>
      VirtualCss.cssForCss(getURLforPath(request, cssPath)).map(cssText => Ok(cssText).as("text/css"))
  }

  def getURLforPath(request: Request[AnyContent], cssPath: String): String = {
    "http://" + request.host + "/" + cssPath
  }
}
