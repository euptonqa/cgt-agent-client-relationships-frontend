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

package connectors

import javax.inject.{Inject, Singleton}

import audit.Logging
import common.Constants.Audit._
import config.ApplicationConfig
import config.FrontendAuthConnector.WSHttp
import config.Keys.GovernmentGateway._
import models.Client
import play.api.Logger
import play.api.http.Status._
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.http._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait GovernmentGatewayResponse
case class SuccessGovernmentGatewayResponse(clients: Seq[Client]) extends GovernmentGatewayResponse
case object FailedGovernmentGatewayResponse extends GovernmentGatewayResponse

@Singleton
class GovernmentGatewayConnector @Inject()(appConfig: ApplicationConfig, auditLogger: Logging) extends HttpErrorFunctions {

  lazy val serviceUrl: String = appConfig.baseUrl("government-gateway")
  lazy val serviceContext: String = appConfig.governmentGatewayContextUrl
  val http: HttpPut with HttpGet with HttpPost = WSHttp

  val urlHeaderEnvironment: String = ""
  val urlHeaderAuthorization: String = ""

  def getExistingClients(serviceName: String, authContext: AuthContext)(implicit hc: HeaderCarrier): Future[GovernmentGatewayResponse] = {
    val agentCode = authContext.principal.accounts.agent.map(_.agentCode.value).getOrElse("")
    val getUrl = s"""$serviceUrl$serviceContext/$agentCode/client-list/$serviceName/$assignedTo"""
    val auditMap: Map[String, String] = Map("Agent Code" -> agentCode, "Url" -> getUrl)
    val result = http.GET[HttpResponse](getUrl)
     result.map { response =>
      response.status match {
        case OK =>
          Logger.info(s"Government Gateway returned an OK with the request $getUrl")
          auditLogger.audit(transactionGetClientList, auditMap, eventTypeSuccess)
          SuccessGovernmentGatewayResponse(response.json.as[Seq[Client]])
        case NO_CONTENT =>
          Logger.info(s"Government Gateway returned a NO_CONTENT with the request $getUrl")
          auditLogger.audit(transactionGetClientList, auditMap, eventTypeSuccess)
          SuccessGovernmentGatewayResponse(Seq(): Seq[Client])
        case BAD_REQUEST =>
          Logger.warn(s"Government Gateway returned a bad request with the request $getUrl with ${response.body}")
          auditLogger.audit(transactionGetClientList, auditMap, eventTypeFailure)
          FailedGovernmentGatewayResponse
        case INTERNAL_SERVER_ERROR =>
          Logger.warn(s"Government Gateway returned an internal server error with the request $getUrl with ${response.body}")
          auditLogger.audit(transactionGetClientList, auditMap, eventTypeFailure)
          FailedGovernmentGatewayResponse
        case ex =>
          Logger.warn(s"Government Gateway returned an unexpected $ex error with the request $getUrl with ${response.body}")
          auditLogger.audit(transactionGetClientList, auditMap, eventTypeFailure)
          FailedGovernmentGatewayResponse
      }
    }
  }
}
