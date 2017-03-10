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
import auth.AuthorisedActions
import config.{ApplicationConfig, WSHttp}
import connectors.{AuthorisationConnector, GovernmentGatewayConnector}
import models.{AuthorisationDataModel, Enrolment}
import play.api.inject.Injector
import services.{AgentService, AuthorisationService}
import data.MessageLookup
import data.TestUsers
import auth.{AuthenticatedAction, AuthorisedActions, CgtAgent}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mockito._
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import traits.ControllerSpecHelper
import uk.gov.hmrc.play.frontend.auth.AuthContext
import org.scalatest.BeforeAndAfter
import uk.gov.hmrc.play.http.{HttpGet, HttpPost, HttpPut}

import scala.concurrent.Future

class AgentControllerSpec extends ControllerSpecHelper with BeforeAndAfter {

  val injector: Injector = app.injector
  val appConfig: ApplicationConfig = injector.instanceOf[ApplicationConfig]
  val auditLogger: Logging = injector.instanceOf[Logging]
  val mockWSHttp: WSHttp = mock[WSHttp]

  before {
    reset(mockWSHttp)
  }

  object mockGovernmentGatewayConnector extends GovernmentGatewayConnector(appConfig, auditLogger) {
    override val http: HttpPut with HttpGet with HttpPost = mockWSHttp
    override val serviceContext: String = ""
    override lazy val serviceUrl: String = ""
  }

  object mockAgentService extends AgentService(mockGovernmentGatewayConnector) {

  }

  def mockAuthorisationService(enrolmentsResponse: Option[Seq[Enrolment]], authResponse: Option[AuthorisationDataModel]): Unit = {
    val mockConnector = mock[AuthorisationConnector]

    when(mockConnector.getAuthResponse()(ArgumentMatchers.any()))
      .thenReturn(Future.successful(authResponse))

    when(mockConnector.getEnrolmentsResponse(ArgumentMatchers.any())(ArgumentMatchers.any()))
      .thenReturn(Future.successful(enrolmentsResponse))

    new AuthorisationService(mockConnector)

  }


  private val testOnlyUnauthorisedLoginUri = "just-a-test"

  def setupController(correctAuthentication: Boolean = true,
                      authContext: AuthContext = TestUsers.strongUserAuthContext): AgentController = {

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
    val mockEnrolments = Some(Seq(mock[Enrolment]))

    mockAuthorisationService(mockEnrolments, Some(authModel))

    new AgentController(mockActions, mockAgentService, appConfig, messagesApi)
  }


  "Calling .declaration" when {

    "provided with a valid authorised user" should {
      lazy val controller = setupController()
      lazy val result = controller.makeDeclaration(FakeRequest())

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
      lazy val result = controller.makeDeclaration(FakeRequest())

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }
}
