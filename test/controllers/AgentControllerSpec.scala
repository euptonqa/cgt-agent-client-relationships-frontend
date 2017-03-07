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

import assets.{ControllerSpecHelper, MessageLookup}
import org.jsoup.Jsoup
import play.api.test.FakeRequest

class AgentControllerSpec extends ControllerSpecHelper {

  "Calling .declaration" when {

    "provided with a valid authorised user" should {
      val controller = new AgentController(config, messagesApi)
      lazy val result = controller.makeDeclaration(FakeRequest())

      "return a status of 200" in {
        status(result) shouldBe 200
      }

      "load the confirmPermission view" in {
        lazy val doc = Jsoup.parse(bodyOf(result))

        doc.title() shouldBe MessageLookup.ConfirmPermission.title
      }
    }

    //TODO currently fails, to be updated with correct setup when auth is introduced
    "provided with an invalid unauthorised user" should {
      val controller = new AgentController(config, messagesApi)
      lazy val result = controller.makeDeclaration(FakeRequest())

      "return a status of 303" in {
        status(result) shouldBe 303
      }
    }
  }
}
