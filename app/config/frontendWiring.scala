/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package config

import javax.inject.{Inject, Singleton}

import uk.gov.hmrc.play.audit.http.config.LoadAuditingConfig
import uk.gov.hmrc.play.audit.http.connector.{AuditConnector => Auditing}
import uk.gov.hmrc.play.config.{AppName, RunMode, ServicesConfig}
import uk.gov.hmrc.play.frontend.auth.connectors.AuthConnector
import uk.gov.hmrc.play.http.ws.{WSDelete, WSGet, WSPost, WSPut}
import uk.gov.hmrc.http.cache.client.SessionCache

@Singleton
class FrontendAuditConnector @Inject()() extends Auditing with AppName {
  override lazy val auditingConfig = LoadAuditingConfig(s"auditing")
}

@Singleton
class FrontendAuthConnector @Inject()(override val http: WSHttp) extends AuthConnector with ServicesConfig {
  lazy val serviceUrl: String = baseUrl("auth")
}


object FrontendAuditConnector extends Auditing with AppName {
  override lazy val auditingConfig = LoadAuditingConfig(s"auditing")
}

object FrontendAuthConnector extends AuthConnector with ServicesConfig {
  lazy val http = WSHttp
  override val serviceUrl: String = baseUrl("auth")

  object WSHttp extends WSGet with WSPut with WSPost with WSDelete with AppName with RunMode {
    override val hooks = NoneRequired
  }
}

@Singleton
class AgentClientSessionCache @Inject()(override val http: WSHttp, appConfig: AppConfig) extends SessionCache with ServicesConfig with AppName {
  override lazy val domain = getConfString("cachable.session-cache.domain", throw new Exception(""))
  override lazy val baseUri = baseUrl("cachable.session-cache")
  override lazy val defaultSource = appName
}