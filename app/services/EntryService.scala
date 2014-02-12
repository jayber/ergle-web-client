package services

import javax.inject.{Inject, Named, Singleton}
import play.api.libs.ws.WS
import play.api.libs.concurrent.Execution.Implicits.defaultContext

@Named()
@Singleton
class EntryService {

  @Inject
  var configProvider: ConfigProvider = null

  def entries(email: String) = {

    val apiUrl = configProvider.config.getString(ConfigProvider.apiUrlKey)

    val requestHolder = url(apiUrl)

    val requestFuture = requestHolder.withQueryString(("email", email)).get()

    requestFuture.map {
      _.json
    }
  }

  def url(url: String) = {
    WS.url(url)
  }
}
