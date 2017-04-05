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
import common.{CountryList, Keys}
import config.WSHttp
import connectors.{FailedRelationshipResponse, KeystoreConnector, SuccessfulRelationshipResponse}
import data.MessageLookup.{ClientConfirmation => messages}
import data.{MessageLookup, TestUsers}
import forms.{BusinessTypeForm, ClientTypeForm, CorrespondenceDetailsForm}
import models.{RedirectModel, SubscriptionReference}
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.BeforeAndAfter
import play.api.inject.Injector
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ClientService, RelationshipService}
import traits.ControllerSpecHelper
import uk.gov.hmrc.domain.{AgentCode, AgentUserId}
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.frontend.auth.connectors.domain._

import scala.concurrent.Future

class ClientControllerSpec extends ControllerSpecHelper with BeforeAndAfter {

  val unauthorisedLoginUrl = "some-url"
  val clientTypeForm: ClientTypeForm = app.injector.instanceOf[ClientTypeForm]
  val correspondenceDetailsForm: CorrespondenceDetailsForm = app.injector.instanceOf[CorrespondenceDetailsForm]
  val businessTypeForm: BusinessTypeForm = app.injector.instanceOf[BusinessTypeForm]
  lazy val clientService: ClientService = mock[ClientService]
  lazy val relationshipService: RelationshipService = mock[RelationshipService]

  private val testOnlyUnauthorisedLoginUri = "just-a-test"

  lazy val injector: Injector = app.injector
  lazy val auditLogger: Logging = injector.instanceOf[Logging]
  lazy val mockWSHttp: WSHttp = mock[WSHttp]
  lazy val countryList: CountryList = injector.instanceOf[CountryList]

  before {
    reset(mockWSHttp)
  }

  def setupController(valid: Boolean = true, authContext: AuthContext = TestUsers.strongUserAuthContext,
                      redirect: Option[RedirectModel] = Some(RedirectModel("context/test"))): ClientController = {
    val mockActions = mock[AuthorisedActions]
    if (valid) {
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

    val sessionService = mock[KeystoreConnector]

    when(sessionService.fetchAndGetFormData[RedirectModel](ArgumentMatchers.eq(Keys.KeystoreKeys.redirect))(any(), any()))
      .thenReturn(Future.successful(redirect))

    new ClientController(config, mockActions, clientService, relationshipService, clientTypeForm,
      correspondenceDetailsForm, messagesApi, auditLogger, sessionService, countryList, businessTypeForm)
  }

  "Calling .clientType" when {

    "an authorised user made the request" should {
      lazy val controller = setupController()
      lazy val result = controller.clientType(FakeRequest("GET", ""))

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the clientType page" in {
        lazy val doc = Jsoup.parse(bodyOf(result))

        doc.title() shouldBe MessageLookup.ClientType.title
      }
    }

    "an unauthorised user made the request" should {
      lazy val controller = setupController(valid = false)
      lazy val result = controller.clientType(FakeRequest("GET", ""))

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }

  "Calling .submitClient" when {
    "supplied with a valid form with a clientType of Individual" should {
      lazy val controller = setupController()
      lazy val result = controller.submitClientType(FakeRequest("POST", "").withFormUrlEncodedBody(("clientType", "Individual")))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the enterIndividualCorrespondenceDetails page" in {
        redirectLocation(result) shouldBe Some(controllers.routes.ClientController.enterIndividualCorrespondenceDetails().url)
      }
    }

    "supplied with a valid form with a clientType of Company" should {
      lazy val controller = setupController()
      lazy val result = controller.submitClientType(FakeRequest("POST", "").withFormUrlEncodedBody(("clientType", "Company")))

      "return a status of 501" in {
        status(result) shouldBe 501
      }
    }

    "supplied with an invalid form" should {
      lazy val controller = setupController()
      lazy val result = controller.submitClientType(FakeRequest("POST", "").withFormUrlEncodedBody(("notAValidField", "")))

      "return a status of 400" in {
        status(result) shouldBe 400
      }
    }
  }

  "Calling .confirmation" when {

    "an authorised user made the request" should {
      lazy val controller = setupController()
      val fakeRequest = FakeRequest("GET", "/")
      lazy val result = controller.confirmation("TestRef")(fakeRequest)

      "return 200" in {
        status(result) shouldBe 200
      }

      "display the confirmationOfSubscription screen" in {
        lazy val doc = Jsoup.parse(bodyOf(result))
        doc.title() shouldEqual messages.title
      }
    }

    "an unauthorised user made the request" should {
      lazy val controller = setupController(valid = false)
      val fakeRequest = FakeRequest("GET", "/")

      lazy val result = controller.confirmation("TestRef")(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }

    "no callback url exists in keystore" should {
      lazy val controller = setupController(redirect = None)
      val fakeRequest = FakeRequest("GET", "/")
      lazy val result = controller.confirmation("TestRef")(fakeRequest)
      lazy val ex = intercept[Exception] {
        await(result)
      }

      "return an exception with message 'No callback url found in session'" in {
        ex.getMessage shouldEqual "No callback url found in session"
      }
    }
  }

  "Calling .enterIndividualCorrespondenceDetails" when {

    "an authorised user made the request" should {
      lazy val controller = setupController()

      lazy val result = controller.enterIndividualCorrespondenceDetails(FakeRequest())

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the correspondence details view" in {
        lazy val doc = Jsoup.parse(bodyOf(result))

        doc.title() shouldBe MessageLookup.CorrespondenceDetails.title
      }
    }

    "provided with an invalid unauthorised user" should {
      lazy val controller = setupController(valid = false)

      lazy val result = controller.enterIndividualCorrespondenceDetails(FakeRequest())

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }

  "Calling .submitIndividualCorrespondenceDetails" when {

    lazy val authContextWithAgentAccount = AuthContext.apply(
      Authority("testUserId", Accounts(agent = Some(AgentAccount(
        "link", AgentCode("agentCodeValue"), AgentUserId("value"),
        AgentAdmin, None
      ))), None, None, CredentialStrength.None, ConfidenceLevel.L50, None, Some("testEnrolmentUri"), None, "")
    )

    "an authorised user made the request, the arn is present, and subscription completes" should {

      lazy val controller = setupController(authContext = authContextWithAgentAccount)

      lazy val result = controller.submitIndividualCorrespondenceDetails(FakeRequest("POST", "").withFormUrlEncodedBody(
        "firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Dark City", "county" -> "", "postcode" -> "", "country" -> "United States"))

      when(clientService.subscribeIndividualClient(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(SubscriptionReference("dummyReference"))

      when(relationshipService.createClientRelationship(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(SuccessfulRelationshipResponse)

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the confirmation view" in {
        redirectLocation(result).get should include("/capital-gains-tax/agent/confirmation?cgtReference=dummyReference")
      }
    }

    "an authorised user made the request but the subscription of the individual failed" should {

      lazy val controller = setupController(authContext = authContextWithAgentAccount)

      lazy val result = controller.submitIndividualCorrespondenceDetails(FakeRequest("POST", "").withFormUrlEncodedBody(
        "firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Dark City", "county" -> "", "postcode" -> "", "country" -> "United States"))

      "return an exception" in {
        when(clientService.subscribeIndividualClient(ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(Future.failed(new Exception("Dummy exception")))

        lazy val ex = intercept[Exception] {
          await(result)
        }
        ex.getMessage shouldBe "Dummy exception"
      }
    }


    "an authorised user made the request but no arn was present" should {

      lazy val controller = setupController()

      lazy val result = controller.submitIndividualCorrespondenceDetails(FakeRequest("POST", "").withFormUrlEncodedBody(
        "firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Dark City", "county" -> "", "postcode" -> "", "country" -> "United States"))

      "return an exception" in {
        when(clientService.subscribeIndividualClient(ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(SubscriptionReference("dummyReference"))

        lazy val ex = intercept[Exception] {
          await(result)
        }
        ex.getMessage shouldBe "Failed to find ARN"
      }
    }

    "an authorised user made the request but the create relationship for the agent and client failed" should {

      lazy val controller = setupController(authContext = authContextWithAgentAccount)

      lazy val result = controller.submitIndividualCorrespondenceDetails(FakeRequest("POST", "").withFormUrlEncodedBody(
        "firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Dark City", "county" -> "", "postcode" -> "", "country" -> "United States"))

      "return an exception" in {
        when(clientService.subscribeIndividualClient(ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(SubscriptionReference("dummyReference"))

        when(relationshipService.createClientRelationship(ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any()))
          .thenReturn(FailedRelationshipResponse)

        lazy val ex = intercept[Exception] {
          await(result)
        }
        ex.getMessage shouldBe "Failed to create relationship"
      }
    }

    "an unauthorised user made the request" should {

      lazy val controller = setupController(valid = false)

      lazy val result = controller.enterIndividualCorrespondenceDetails(FakeRequest())

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the test-url" in {
        redirectLocation(result).get should include("just-a-test")
      }
    }
  }
}
