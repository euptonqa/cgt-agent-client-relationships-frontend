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

package controllers

import audit.Logging
import auth.{CgtAgent, _}
import config.WSHttp
import connectors.AuthorisationConnector
import data.{MessageLookup, TestUsers}
import models.{AuthorisationDataModel, Enrolment}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.BeforeAndAfter
import play.api.inject.Injector
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import services.AuthorisationService
import traits.ControllerSpecHelper
import uk.gov.hmrc.play.frontend.auth.AuthContext

import scala.concurrent.Future

class ClientControllerSpec extends ControllerSpecHelper with BeforeAndAfter{

  lazy val injector: Injector = app.injector
  lazy val auditLogger: Logging = injector.instanceOf[Logging]
  lazy val mockWSHttp: WSHttp = mock[WSHttp]

  before {
    reset(mockWSHttp)
  }

  def mockAuthorisationService(): AuthorisationService = {
    val mockConnector = mock[AuthorisationConnector]

    val mockAuthResponse = Some(mock[AuthorisationDataModel])

    when(mockConnector.getAuthResponse()(ArgumentMatchers.any()))
      .thenReturn(Future.successful(mockAuthResponse))

    when(mockConnector.getEnrolmentsResponse(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some(Seq(mock[Enrolment]))))

    new AuthorisationService(mockConnector)

  }

  private val testOnlyUnauthorisedLoginUri = "just-a-test"

  def setupController(correctAuthentication: Boolean = true,
                      authContext: AuthContext = TestUsers.strongUserAuthContext): ClientController = {

    val mockActions = mock[AuthorisedActions]

    if (correctAuthentication) {
      when(mockActions.authorisedAgentAction(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedAction](0)
            val agent = CgtAgent(authContext)
            Action.async(action(agent))
          }
        })
    }
    else {
      when(mockActions.authorisedAgentAction(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(testOnlyUnauthorisedLoginUri)))
    }

    val authModel = mock[AuthorisationDataModel]
    when(authModel.uri).thenReturn("")

    mockAuthorisationService()

    new ClientController(mockActions, config, messagesApi)
  }

  "Calling .enterIndividualCorrespondenceDetails" when {

    "provided with a valid authorised user" should {

      lazy val controller = setupController()
      lazy val result = controller.enterIndividualCorrespondenceDetails(FakeRequest())

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the confirmPermission view" in {
        lazy val doc = Jsoup.parse(bodyOf(result))

        doc.title() shouldBe MessageLookup.ConfirmPermission.title
      }
    }

    "provided with an invalid unauthorised user" should {

      lazy val controller = setupController(correctAuthentication = false)
      lazy val result = controller.enterIndividualCorrespondenceDetails(FakeRequest())

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }

}
