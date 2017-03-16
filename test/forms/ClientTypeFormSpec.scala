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

package forms

import models.ClientTypeModel
import traits.ViewSpecHelper
import data.MessageLookup.{ClientType => messages}

class ClientTypeFormSpec extends ViewSpecHelper {

  "Creating a clientTypeForm" when {
    lazy val form = app.injector.instanceOf[ClientTypeForm]

    "passing in a valid type of 'Individual'" should {
      val clientType = "Individual"
      val result = form.clientTypeForm.bind(Map("clientType" -> clientType))

      "have no errors" in {
        result.errors.size shouldBe 0
      }

      "have a valid model" in {
        result.value shouldBe Some(ClientTypeModel("Individual"))
      }
    }

    "passing in a valid type of 'Company'" should {
      val clientType = "Company"
      val result = form.clientTypeForm.bind(Map("clientType" -> clientType))

      "have no errors" in {
        result.errors.size shouldBe 0
      }

      "have a valid model" in {
        result.value shouldBe Some(ClientTypeModel("Company"))
      }
    }

    "passing in an invalid type of 'Agent'" should {
      val clientType = "Agent"
      val result = form.clientTypeForm.bind(Map("clientType" -> clientType))

      "have one error" in {
        result.errors.size shouldBe 1
      }

      "have no valid model" in {
        result.value shouldBe None
      }

      s"have an error message of '${messages.error}'" in {
        result.errors.head.message shouldBe messages.error
      }
    }

    "passing in an invalid blank type" should {
      val clientType = ""
      val result = form.clientTypeForm.bind(Map("clientType" -> clientType))

      "have one error" in {
        result.errors.size shouldBe 1
      }

      "have no valid model" in {
        result.value shouldBe None
      }

      s"have an error message of '${messages.error}'" in {
        result.errors.head.message shouldBe messages.error
      }
    }
  }
}
