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

import java.util.UUID

import audit.Logging
import config.ApplicationConfig
import models.{Client, IdentifierForDisplay}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.http.Status._
import play.api.libs.json.Json
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

class GovernmentGatewayConnectorSpec extends UnitSpec with OneAppPerSuite with MockitoSugar with BeforeAndAfter {

  val mockWSHttp: WSHttp = mock[WSHttp]
  val mockLoggingUtils: Logging = mock[Logging]
  lazy val mockAppConfig: ApplicationConfig = app.injector.instanceOf[ApplicationConfig]
  implicit val ec = mock[ExecutionContext]

  object TestGovernmentGatewayConnector extends GovernmentGatewayConnector(mockAppConfig, mockLoggingUtils) {
    override val http: HttpPut with HttpGet with HttpPost = mockWSHttp
    override val serviceContext: String = ""
    override lazy val serviceUrl: String = ""
  }

  before {
    reset(mockWSHttp)
  }

  "Calling .getExistingClients" when {

    implicit val hc = new HeaderCarrier(sessionId = Some(SessionId(s"session-${UUID.randomUUID}")))

    "an OK response is returned" when {

      "the client list is not empty" should {

        val identifier = IdentifierForDisplay("CGT ref", "CGT123456")
        val clients = List(Client("John Smith", List(identifier)))

        when(mockWSHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(HttpResponse(responseStatus = OK, responseJson = Some(Json.toJson(clients)))))

        val result = await(TestGovernmentGatewayConnector.getExistingClients("ARN"))

        "return a SuccessGovernmentGatewayResponse with a list of clients" in {
          result shouldEqual SuccessGovernmentGatewayResponse(clients)
        }
      }

      "the client list is empty" should {

        when(mockWSHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(HttpResponse(responseStatus = OK, responseJson = Some(Json.toJson(List.empty[Client])))))

        val result = await(TestGovernmentGatewayConnector.getExistingClients("ARN"))

        "return a SuccessGovernmentGatewayResponse with an empty list of clients" in {
          result shouldEqual SuccessGovernmentGatewayResponse(List.empty[Client])
        }
      }
    }

    "A BAD_REQUEST is returned" should {

      when(mockWSHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(HttpResponse(responseStatus = BAD_REQUEST, responseJson = Some(Json.obj("reason" -> "y")))))

      val result = await(TestGovernmentGatewayConnector.getExistingClients("ARN"))

      "return a FailedGovernmentGatewayResponse" in {
        result shouldEqual FailedGovernmentGatewayResponse
      }
    }

    "A INTERNAL_SERVER_ERROR is returned" should {

      when(mockWSHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(HttpResponse(responseStatus = INTERNAL_SERVER_ERROR, responseJson = Some(Json.obj("reason" -> "y")))))

      val result = await(TestGovernmentGatewayConnector.getExistingClients("ARN"))

      "return a FailedGovernmentGatewayResponse" in {
        result shouldEqual FailedGovernmentGatewayResponse
      }
    }
  }
}
