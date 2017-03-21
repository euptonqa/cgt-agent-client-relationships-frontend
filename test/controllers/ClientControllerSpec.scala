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

import data.MessageLookup.{ClientConfirmation => messages}
import forms.{ClientTypeForm, CorrespondenceDetailsForm}
import play.api.inject.Injector
import audit.Logging
import auth.{CgtAgent, _}
import config.WSHttp
import data.{MessageLookup, TestUsers}
import models.SubscriptionReference
import org.jsoup.Jsoup
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.BeforeAndAfter
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.{ClientService, RelationshipService}
import traits.ControllerSpecHelper

class ClientControllerSpec extends ControllerSpecHelper with BeforeAndAfter{

  val unauthorisedLoginUrl = "some-url"
  val clientTypeForm: ClientTypeForm = app.injector.instanceOf[ClientTypeForm]
  val correspondenceDetailsForm: CorrespondenceDetailsForm = app.injector.instanceOf[CorrespondenceDetailsForm]
  lazy val clientService: ClientService = mock[ClientService]
  lazy val relationshipService: RelationshipService = mock[RelationshipService]


  private val testOnlyUnauthorisedLoginUri = "just-a-test"

  lazy val injector: Injector = app.injector
  lazy val auditLogger: Logging = injector.instanceOf[Logging]
  lazy val mockWSHttp: WSHttp = mock[WSHttp]

  before {
    reset(mockWSHttp)
  }

  def createMockActions(valid: Boolean = true): AuthorisedActions = {
    val authContext = TestUsers.strongUserAuthContext
    val mockActions = mock[AuthorisedActions]
    if (valid) {
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
    mockActions
  }

  "Calling .clientType" when {

    "an authorised user made the request" should {
      val actions = createMockActions()
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
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
      val actions = createMockActions(valid = false)
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
      lazy val result = controller.clientType(FakeRequest("GET", ""))

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }

  "Calling .submitClient" when {
    "supplied with a valid form with a clientType of Individual" should {
      val actions = createMockActions()
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
      lazy val result = controller.submitClientType(FakeRequest("POST", "").withFormUrlEncodedBody(("clientType", "Individual")))

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the enterIndividualCorrespondenceDetails page" in {
        redirectLocation(result) shouldBe Some(controllers.routes.ClientController.enterIndividualCorrespondenceDetails().url)
      }
    }

    "supplied with a valid form with a clientType of Company" should {
      val actions = createMockActions()
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
      lazy val result = controller.submitClientType(FakeRequest("POST", "").withFormUrlEncodedBody(("clientType", "Company")))

      "return a status of 501" in {
        status(result) shouldBe 501
      }
    }

    "supplied with an invalid form" should {
      val actions = createMockActions()
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
      lazy val result = controller.submitClientType(FakeRequest("POST", "").withFormUrlEncodedBody(("notAValidField", "")))

      "return a status of 400" in {
        status(result) shouldBe 400
      }
    }
  }

  "Calling .confirmation" when {

    "an authorised user made the request" should {
      val actions = createMockActions()
      val fakeRequest = FakeRequest("GET", "/")
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
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
      val actions = createMockActions(valid = false)
      val fakeRequest = FakeRequest("GET", "/")
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
      lazy val result = controller.confirmation("TestRef")(fakeRequest)

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }

  "Calling .enterIndividualCorrespondenceDetails" when {

    "an authorised user made the request" should {
      val actions = createMockActions()
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
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
      val actions = createMockActions(valid = false)
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
      lazy val result = controller.enterIndividualCorrespondenceDetails(FakeRequest())

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }

  "Calling .submitIndividualCorrespondenceDetails" when {

    "an authorised user made the request, the arn is present, and subscription completes" should {

      "return a status of 303" in {

      }

      "redirect to the confirmation view" in {
//        redirectLocation(result).get should include ("/calculate-your-capital-gains/resident/properties/session-timeout")
      }
    }

    "an authorised user made the request but no arn was present" should {

      val actions = createMockActions()
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
      lazy val result = controller.submitIndividualCorrespondenceDetails(FakeRequest("POST", "").withFormUrlEncodedBody(
        "firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "town" -> "Dark City", "county" -> "", "postcode" -> "", "country" -> "United States"))

      when(clientService.subscribeIndividualClient(ArgumentMatchers.any())(ArgumentMatchers.any()))
        .thenReturn(SubscriptionReference("dummyReference"))

      "return a status of 500" in {
        status(result) shouldBe 500
      }
    }

    "an authorised user made the request but the subscription of the individual failed" should {

      "return a status of 500" in {

      }
    }

    "an authorised user made the request but the create relationship for the agent and client failed" should {

      "return a status of 500" in {

      }
    }

    "an unauthorised user made the request" should {

      val actions = createMockActions(valid = false)
      lazy val controller = new ClientController(config, actions, clientService, relationshipService, clientTypeForm, correspondenceDetailsForm, messagesApi)
      lazy val result = controller.enterIndividualCorrespondenceDetails(FakeRequest())

      "return a status of 303" in {
        status(result) shouldBe 303
      }

      "redirect to the test-url" in {
        redirectLocation(result).get should include ("just-a-test")
      }
    }
  }
}
