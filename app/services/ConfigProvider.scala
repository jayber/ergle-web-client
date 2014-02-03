package services

import javax.inject.{Named, Singleton}
import com.typesafe.config.{ConfigFactory, Config}


object ConfigProvider {
  val apiUrlKey: String = "api.url"
}

@Named
@Singleton
class ConfigProvider {
  def config: Config = {
    ConfigFactory.load()
  }
}
