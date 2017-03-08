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

import audit.Logging
import config.ApplicationConfig
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import play.api.http.Status._
import traits.ControllerSpecHelper
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.ExecutionContext

class GovernmentGatewayConnectorSpec extends ControllerSpecHelper with BeforeAndAfter {

  val mockWSHttp: WSHttp = mock[WSHttp]
  val mockLoggingUtils: Logging = mock[Logging]
  val mockAppConfig: ApplicationConfig = mock[ApplicationConfig]
  implicit val hc = mock[HeaderCarrier]
  implicit val ec = mock[ExecutionContext]

  object GovernmentGatewayConnector extends GovernmentGatewayConnector(mockAppConfig, mockLoggingUtils) {
    override val http: HttpPut with HttpGet with HttpPost = mockWSHttp
  }

  before {
    reset(mockWSHttp)
  }

  "httpRds" should {

    "return the http response when a OK status code is read from the http response" in {
      val response = HttpResponse(OK)
      GovernmentGatewayConnector.httpRds.read("http://", "testUrl", response) shouldBe response
    }

    "return a not found exception when it reads a NOT_FOUND status code from the http response" in {
      intercept[NotFoundException] {
        GovernmentGatewayConnector.httpRds.read("http://", "testUrl", HttpResponse(NOT_FOUND))
      }
    }
  }

//  "Calling .getExistingClients" should {
//
//    "when "
//  }

  "Calling .customGovernmentGatewayRead" should {

    "return the HttpResponse on a bad request" in {
      val response = HttpResponse(BAD_REQUEST)
      await(GovernmentGatewayConnector.customGovernmentGatewayRead("", "", response)) shouldBe response
    }

    "throw a NotFoundException" in {
      val response = HttpResponse(NOT_FOUND)
      val ex = intercept[NotFoundException] {
        await(GovernmentGatewayConnector.customGovernmentGatewayRead("", "", response))
      }
      ex.getMessage shouldBe "Government Gateway returned a Not Found status"
    }

    "throw an InternalServerException" in {
      val response = HttpResponse(INTERNAL_SERVER_ERROR)
      val ex = intercept[InternalServerException] {
        await(GovernmentGatewayConnector.customGovernmentGatewayRead("", "", response))
      }
      ex.getMessage shouldBe "Government Gateway returned an internal server error"
    }

    "throw an BadGatewayException" in {
      val response = HttpResponse(BAD_GATEWAY)
      val ex = intercept[BadGatewayException] {
        await(GovernmentGatewayConnector.customGovernmentGatewayRead("", "", response))
      }
      ex.getMessage shouldBe "Government Gateway returned an upstream error"
    }

    "return an Upstream4xxResponse when an uncaught 4xx Http response status is found" in {
      val response = HttpResponse(METHOD_NOT_ALLOWED)
      val ex = intercept[Upstream4xxResponse] {
        await(GovernmentGatewayConnector.customGovernmentGatewayRead("http://", "testUrl", response))
      }
      ex.getMessage shouldBe "http:// of 'testUrl' returned 405. Response body: 'null'"
    }

    "return an Upstream5xxResponse when an uncaught 5xx Http response status is found" in {
      val response = HttpResponse(HTTP_VERSION_NOT_SUPPORTED)
      val ex = intercept[Upstream5xxResponse] {
        await(GovernmentGatewayConnector.customGovernmentGatewayRead("http://", "testUrl", response))
      }
      ex.getMessage shouldBe "http:// of 'testUrl' returned 505. Response body: 'null'"
    }
  }
}
