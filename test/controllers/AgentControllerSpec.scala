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
import config.WSHttp
import connectors._
import models._
import play.api.inject.Injector
import services.{AgentService, AuthorisationService}
import data.MessageLookup
import data.TestUsers
import auth.{AuthenticatedAction, AuthorisedActions, CgtAgent}
import common.Keys.{KeystoreKeys => Keys}
import common.Keys.GovernmentGateway._
import forms.SelectedClientForm
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers._
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mockito._
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import traits.ControllerSpecHelper
import uk.gov.hmrc.play.frontend.auth.AuthContext
import org.scalatest.BeforeAndAfter
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.test.Helpers.{OK => _, _}
import uk.gov.hmrc.play.http.HttpResponse

import scala.concurrent.Future


class AgentControllerSpec extends ControllerSpecHelper with BeforeAndAfter {

  lazy val injector: Injector = app.injector
  lazy val auditLogger: Logging = injector.instanceOf[Logging]
  lazy val mockWSHttp: WSHttp = mock[WSHttp]
  val selectedClientForm: SelectedClientForm = app.injector.instanceOf[SelectedClientForm]

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
                      authContext: AuthContext = TestUsers.strongUserAuthContext,
                      redirection: Option[CallbackUrlModel] = Some(CallbackUrlModel(url = "callback-url")),
                      agentService: AgentService): AgentController = {

    val mockActions = mock[AuthorisedActions]

    if (correctAuthentication) {
      when(mockActions.authorisedAgentAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenAnswer(new Answer[Action[AnyContent]] {

          override def answer(invocation: InvocationOnMock): Action[AnyContent] = {
            val action = invocation.getArgument[AuthenticatedAction](1)
            val agent = CgtAgent(authContext)
            Action.async(action(agent))
          }
        })
    }
    else {
      when(mockActions.authorisedAgentAction(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(Action.async(Results.Redirect(testOnlyUnauthorisedLoginUri)))
    }

    val authModel = mock[AuthorisationDataModel]
    when(authModel.uri).thenReturn("")

    val sessionService = mock[KeystoreConnector]

    when(sessionService.fetchAndGetFormData[CallbackUrlModel](ArgumentMatchers.eq(Keys.callbackUrl))(any(), any()))
      .thenReturn(Future.successful(redirection))

    mockAuthorisationService()

    new AgentController(mockActions, agentService, sessionService, config, messagesApi, selectedClientForm)
  }


  val identifier = IdentifierForDisplay("CGT ref", "CGT123456")
  val clients = List(Client("John Smith", List(identifier)))

  "Calling .declaration" when {

    "provided with a valid authorised user" should {
      lazy val ggConnector = mock[GovernmentGatewayConnector]

      when(ggConnector.getExistingClients(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(SuccessGovernmentGatewayResponse(clients))

      val agentService = new AgentService(ggConnector)
      lazy val controller = setupController(agentService = agentService)
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
      lazy val ggConnector = mock[GovernmentGatewayConnector]

      when(ggConnector.getExistingClients(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(SuccessGovernmentGatewayResponse(clients))

      val agentService = new AgentService(ggConnector)
      lazy val controller = setupController(correctAuthentication = false, agentService = agentService)
      lazy val result = controller.makeDeclaration(FakeRequest())

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }

  "Calling .selectClient" when {

    "provided with a valid authorised user and form and successful loads from keystore" should {
      lazy val ggConnector = mock[GovernmentGatewayConnector]

      when(ggConnector.getExistingClients(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(SuccessGovernmentGatewayResponse(clients))

      val agentService = new AgentService(ggConnector)
      lazy val controller = setupController(agentService = agentService)
      lazy val result = controller.selectClient(FakeRequest("POST", "").withFormUrlEncodedBody("friendlyName" -> "John Smith", "cgtRef" -> "CGT12345678"))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the iForm" in {
        redirectLocation(result).get.toString shouldBe "callback-url"
      }
    }

    "provided with a valid authorised user and form and no model is returned from keystore" should {
      lazy val ggConnector = mock[GovernmentGatewayConnector]

      when(ggConnector.getExistingClients(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(SuccessGovernmentGatewayResponse(clients))

      val agentService = new AgentService(ggConnector)
      lazy val controller = setupController(redirection = None, agentService = agentService)
      lazy val result = controller.selectClient(FakeRequest("POST", "").withFormUrlEncodedBody("friendlyName" -> "John Smith", "cgtRef" -> "CGT12345678"))

      "return an exception" in {
        lazy val ex = intercept[Exception] {
          await(result)
        }
        ex.getMessage shouldBe "No callback url has been found"
      }
    }

    "provided with an invalid unauthorised user" should {
      lazy val ggConnector = mock[GovernmentGatewayConnector]

      when(ggConnector.getExistingClients(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(SuccessGovernmentGatewayResponse(clients))

      val agentService = new AgentService(ggConnector)
      lazy val controller = setupController(correctAuthentication = false, agentService = agentService)
      lazy val result = controller.makeDeclaration(FakeRequest())

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }

  "Calling .showClientList" when {

    "provided with a valid authorised user" when {

      "a successGovernmentGatewayResponse is obtained" should {

        lazy val ggConnector = mock[GovernmentGatewayConnector]

        when(ggConnector.getExistingClients(ArgumentMatchers.eq(clientServiceNameOrganisation), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(SuccessGovernmentGatewayResponse(clients))

        when(ggConnector.getExistingClients(ArgumentMatchers.eq(clientServiceNameIndividual), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(SuccessGovernmentGatewayResponse(clients))

        lazy val agentService = new AgentService(ggConnector)

        when(mockWSHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(HttpResponse(responseStatus = OK, responseJson = Some(Json.toJson(clients)))))

        lazy val controller = setupController(correctAuthentication = true, agentService = agentService)
        lazy val result = controller.showClientList("/context/test")(FakeRequest())
        lazy val doc = Jsoup.parse(bodyOf(result))

        "return a status of 200" in {
          status(result) shouldBe 200
        }

        "load the client list view" in {

          doc.title() shouldBe MessageLookup.ClientList.title
        }

        "have an entry for John Smith in the clients table" in {
          val nameField = doc.select("tr:nth-of-type(2)").select("td:nth-of-type(1)")
          nameField.text shouldEqual "John Smith"
        }
      }

      "a FailedGovernmentGatewayResponse is obtained" should {
        lazy val ggConnector = mock[GovernmentGatewayConnector]

        when(ggConnector.getExistingClients(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(FailedGovernmentGatewayResponse)

        lazy val agentService = new AgentService(ggConnector)

        when(mockWSHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
          .thenReturn(Future.successful(HttpResponse(responseStatus = 500, responseJson = Some(Json.toJson(clients)))))

        lazy val controller = setupController(correctAuthentication = true, agentService = agentService)
        lazy val result = controller.showClientList("/context/test")(FakeRequest())

        lazy val exception = intercept[Exception] {
          await(result)
        }

        "return an exception" in {
          exception.isInstanceOf[Exception] shouldBe true
        }

        "return a message of 'Failed to retrieve client'" in {
          exception.getMessage shouldBe "Failed to retrieve client"
        }
      }
    }

    "provided with an unauthorised user" should {
      lazy val ggConnector = mock[GovernmentGatewayConnector]

      when(ggConnector.getExistingClients(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(SuccessGovernmentGatewayResponse(clients))

      lazy val agentService = new AgentService(ggConnector)
      lazy val controller = setupController(correctAuthentication = false, agentService = agentService)
      lazy val result = controller.showClientList("/context/test")(FakeRequest())

      "return a status type of 303" in {
        status(result) shouldBe 303
      }
    }

    "provided with an invalid callback url" should {
      lazy val ggConnector = mock[GovernmentGatewayConnector]

      when(ggConnector.getExistingClients(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(FailedGovernmentGatewayResponse)

      lazy val agentService = new AgentService(ggConnector)

      when(mockWSHttp.GET[HttpResponse](ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any()))
        .thenReturn(Future.successful(HttpResponse(responseStatus = OK, responseJson = Some(Json.toJson(clients)))))

      lazy val controller = setupController(correctAuthentication = true, agentService = agentService)
      lazy val result = controller.showClientList("http://www.google.com")(FakeRequest())
      lazy val body = Jsoup.parse(bodyOf(result))

      "return a status of 400" in {
        status(result) shouldBe 400
      }

      "redirect to the Bad Request error page" in {
        body.title() shouldBe MessageLookup.Common.badRequest
       }
    }
  }
}
