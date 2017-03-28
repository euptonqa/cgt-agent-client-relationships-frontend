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
import models.SubmissionModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class RelationshipConnectorSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  implicit val hc = HeaderCarrier()

  lazy val config: ApplicationConfig = app.injector.instanceOf[ApplicationConfig]

  def createMockHttp(successfulResponse: Boolean): WSHttp = {
    val mockHttp: WSHttp = mock[WSHttp]
    val httpResponse: HttpResponse = mock[HttpResponse]

    when(httpResponse.status)
      .thenReturn(if (successfulResponse) 204 else 500)

    when(mockHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(),
      ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(httpResponse))

    mockHttp
  }

  "RelationshipConnector .createRelationship" should {
    val submissionModel = mock[SubmissionModel]

    "return a SuccessfulRelationshipResponse on success" in {
      val mockHttp = createMockHttp(successfulResponse = true)
      lazy val connector = new RelationshipConnector(config, mockHttp)
      lazy val result = connector.createClientRelationship(submissionModel)

      await(result) shouldBe SuccessfulRelationshipResponse
    }

    "return a FailedRelationshipResponse on failure" in {
      val mockHttp = createMockHttp(successfulResponse = false)
      lazy val connector = new RelationshipConnector(config, mockHttp)
      lazy val result = connector.createClientRelationship(submissionModel)

      await(result) shouldBe FailedRelationshipResponse
    }
  }

}
