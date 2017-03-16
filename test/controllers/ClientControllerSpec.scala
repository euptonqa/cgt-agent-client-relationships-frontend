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

import auth.{CgtAgent, _}
import data.TestUsers
import forms.ClientTypeForm
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.FakeRequest
import traits.ControllerSpecHelper
import play.api.test.Helpers._

class ClientControllerSpec extends ControllerSpecHelper {

  val unauthorisedLoginUrl = "some-url"
  val form = app.injector.instanceOf[ClientTypeForm]
  private val testOnlyUnauthorisedLoginUri = "just-a-test"

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

  "Calling .submitClient when" when {
    "supplied with a valid form with a clientType of Individual" should {
      val actions = createMockActions()
      lazy val controller = new ClientController(config, actions, form, messagesApi)
      lazy val result = controller.submitClientType(FakeRequest("POST", "").withFormUrlEncodedBody(("clientType", "Individual")))

      "return a status of 200" in {
        status(result) shouldBe 303
      }

      "redirect to the enterIndividualCorrespondenceDetails page" in {
        redirectLocation(result) shouldBe Some(controllers.routes.ClientController.enterIndividualCorrespondenceDetails().url)
      }

      "supplied with a valid form with a clientType of Company" should {
        val actions = createMockActions()
        lazy val controller = new ClientController(config, actions, form, messagesApi)
        lazy val result = controller.submitClientType(FakeRequest("POST", "").withFormUrlEncodedBody(("clientType", "Company")))
        "return a status of 501" in {
          status(result) shouldBe 501
        }
      }
    }

    "supplied with an invalid form" should {
      val actions = createMockActions()
      lazy val controller = new ClientController(config, actions, form, messagesApi)
      lazy val result = controller.submitClientType(FakeRequest("POST", "").withFormUrlEncodedBody(("notAValidField", "")))
      "return a status of 400" in {
        status(result) shouldBe 400
      }
    }
  }

}