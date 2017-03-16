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

import audit.Logging
import config.{ApplicationConfig, WSHttp}
import connectors.SubscriptionConnector
import models.{SubscriptionReference, UserFactsModel}
import org.mockito._
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfter
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class ClientServiceSpec extends UnitSpec with OneAppPerSuite with MockitoSugar {

  val mockWSHttp: WSHttp = mock[WSHttp]
  val mockLoggingUtils: Logging = mock[Logging]
  lazy val mockAppConfig: ApplicationConfig = app.injector.instanceOf[ApplicationConfig]
  implicit val hc = new HeaderCarrier(sessionId = Some(SessionId(s"session-${UUID.randomUUID}")))

  val userFactsModel = UserFactsModel("", "", "", None, "", None, "", "")

  def setupService(subscriptionResponse: Future[SubscriptionReference]): ClientService = {

    val mockConnector = mock[SubscriptionConnector]

    when(mockConnector.subscribeIndividualClient(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(subscriptionResponse))

    new ClientService(mockConnector)
  }

  "Calling .subscribeIndividualClient" should {

    "return the subscription reference when the connector returns a subscription reference" in {
      lazy val mockedService = setupService(Future.successful(SubscriptionReference("CGT123456789012")))
      lazy val response = mockedService.subscribeIndividualClient(userFactsModel)

      await(response) shouldBe SubscriptionReference("CGT123456789012")
    }

    "return an error when the connector returns one" in {
      lazy val mockedService = setupService(throw new Exception("Error message"))
      lazy val ex = intercept[Exception] {
        await(mockedService.subscribeIndividualClient(userFactsModel))
      }

      ex.getMessage shouldBe "Error message"
    }
  }



}
