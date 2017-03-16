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

import config.{ApplicationConfig, WSHttp}
import models.{SubscriptionReference, UserFactsModel}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class SubscriptionConnectorSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  implicit val hc = HeaderCarrier()

  lazy val config: ApplicationConfig = app.injector.instanceOf[ApplicationConfig]

  def cgtSubscriptionResponse(cgtRef: String): JsValue = Json.toJson(SubscriptionReference(cgtRef))

  def createMockHttp(successfulResponse: Boolean, json: JsValue): WSHttp = {
    val mockHttp: WSHttp = mock[WSHttp]
    val httpResponse: HttpResponse = mock[HttpResponse]

    when(httpResponse.status)
      .thenReturn(if (successfulResponse) 200 else 500)

    when(httpResponse.json)
        .thenReturn(json)

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(),
      ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(httpResponse))

    mockHttp
  }

  "SubscriptionConnector .subscribeIndividualClient" should {
    val model = UserFactsModel("", "", "", None, "", None, "", "")

    "return a successful future with a subscription reference on a success" in {
      val json = cgtSubscriptionResponse("CGT123456789")
      lazy val mockHttp = createMockHttp(successfulResponse = true, json)
      lazy val connector = new SubscriptionConnector(mockHttp, config)
      lazy val result = connector.subscribeIndividualClient(model)

      await(result) shouldBe SubscriptionReference("CGT123456789")
    }

    "return a failed future with a Json formatting error with invalid json" in {
      val json = Json.toJson("")
      lazy val mockHttp = createMockHttp(successfulResponse = true, json)
      lazy val connector = new SubscriptionConnector(mockHttp, config)
      lazy val result = connector.subscribeIndividualClient(model)
      lazy val exception = intercept[Exception] {
        await(result)
      }

      exception.getMessage shouldBe "JsResultException(errors:List((/cgtRef,List(ValidationError(List(error.path.missing),WrappedArray())))))"
    }

    "return a failed future with a response error with an invalid response" in {
      val json = cgtSubscriptionResponse("CGT123456789")
      lazy val mockHttp = createMockHttp(successfulResponse = false, json)
      lazy val connector = new SubscriptionConnector(mockHttp, config)
      lazy val result = connector.subscribeIndividualClient(model)
      lazy val exception = intercept[Exception] {
        await(result)
      }

      exception.getMessage shouldBe "Invalid response from Subscription Service"
    }
  }
}
