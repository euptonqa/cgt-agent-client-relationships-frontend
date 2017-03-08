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
import config.FrontendAuthConnector.WSHttp
import config.ApplicationConfig
import play.api.http.Status._
import config.Keys.GovernmentGateway._
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.http.logging.Authorization
import models.Client
import play.api.Logger
import common.Constants.Audit._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait GovernmentGatewayResponse
case class SuccessGovernmentGatewayResponse(clients: List[Client]) extends GovernmentGatewayResponse
case object FailedGovernmentGatewayResponse extends GovernmentGatewayResponse

@Singleton
class GovernmentGatewayConnector @Inject()(appConfig: ApplicationConfig, auditLogger: Logging) extends HttpErrorFunctions {

  lazy val serviceUrl: String = appConfig.baseUrl("government-gateway")
  val serviceContext: String = appConfig.governmentGatewayContextUrl
  val http: HttpPut with HttpGet with HttpPost = WSHttp

  val urlHeaderEnvironment: String = ""
  val urlHeaderAuthorization: String = ""

//  implicit val httpRds = new HttpReads[HttpResponse] {
//    def read(http: String, url: String, res: HttpResponse): HttpResponse = customGovernmentGatewayRead(http, url, res)
//  }

//  @inline
//  private def cGET[O](url: String)(implicit rds: HttpReads[O], hc: HeaderCarrier) = {
//    http.GET[O](url)(rds, hc = createHeaderCarrier(hc))
//  }
//
//  private def createHeaderCarrier(headerCarrier: HeaderCarrier): HeaderCarrier = {
//    headerCarrier.
//      withExtraHeaders("Environment" -> urlHeaderEnvironment).
//      copy(authorization = Some(Authorization(urlHeaderAuthorization)))
//  }

//  private[connectors] def customGovernmentGatewayRead(http: String, url: String, response: HttpResponse) = {
//    response.status match {
//      case BAD_REQUEST => response
//      case NOT_FOUND => response
//      case INTERNAL_SERVER_ERROR => throw new InternalServerException("Government Gateway responded with INTERNAL_SERVER_ERROR")
//      case BAD_GATEWAY => throw new BadGatewayException("Government Gateway returned an upstream error")
//      case _ => handleResponse(http, url)(response)
//    }
//  }

  def getExistingClients(arn: String)(implicit hc: HeaderCarrier): Future[GovernmentGatewayResponse] = {
    val getUrl = s"""$serviceUrl/$serviceContext/$arn/client-list/$clientServiceName/$assignedTo"""
    val auditMap: Map[String, String] = Map("ARN" -> arn, "Url" -> getUrl)
    val result = http.GET[HttpResponse](getUrl)
     result.map { response =>
      response.status match {
        case OK => SuccessGovernmentGatewayResponse(response.json.as[List[Client]])
        case BAD_REQUEST =>
          Logger.warn(s"Government Gateway returned a bad request with the request $getUrl with ${response.body}")
          auditLogger.audit(transactionGetClientList, auditMap, eventTypeFailure)
          FailedGovernmentGatewayResponse
      }
    }
  }
}
