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

package services

import java.util.UUID

import config.{ApplicationConfig, WSHttp}
import connectors.{FailedRelationshipResponse, RelationshipConnector, SuccessfulRelationshipResponse}
import models.SubmissionModel
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.JsValue
import uk.gov.hmrc.play.http._
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.{ExecutionContext, Future}

class RelationshipServiceSpec extends UnitSpec with OneAppPerSuite with MockitoSugar with BeforeAndAfter {

  val mockWSHttp: WSHttp = mock[WSHttp]
  lazy val mockAppConfig: ApplicationConfig = app.injector.instanceOf[ApplicationConfig]
  implicit val ec: ExecutionContext = mock[ExecutionContext]

  object RelationConnector extends RelationshipConnector(mockAppConfig, mockWSHttp) {
    override lazy val serviceUrl: String = "blah"
    override val createRelationship: String = "/relationship"
  }

  before {
    reset(mockWSHttp)
  }

  implicit val hc = new HeaderCarrier(sessionId = Some(SessionId(s"session-${UUID.randomUUID}")))
  val relationshipService = new RelationshipService(RelationConnector)

  "Calling .createRelationship" when {
    "a 204 is returned" in {
      when(mockWSHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(),
        ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(HttpResponse(responseStatus = 204)))

      await(RelationConnector.createClientRelationship(mock[SubmissionModel])) shouldBe SuccessfulRelationshipResponse
    }

    "a 500 is returned" in {

      when(mockWSHttp.POST[JsValue, HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any(),
        ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(HttpResponse(responseStatus = 500)))

      await(RelationConnector.createClientRelationship(mock[SubmissionModel])) shouldBe FailedRelationshipResponse
    }
  }

}
