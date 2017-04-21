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

import models.BusinessTypeModel
import traits.ViewSpecHelper
import data.MessageLookup.{BusinessType => messages}

class BusinessTypeFormSpec extends ViewSpecHelper {

  "Creating a businessTypeForm" when {
    lazy val form = app.injector.instanceOf[BusinessTypeForm]

    "passing in a valid type of 'NUK'" should {
      val businessType = "NUK"
      val result = form.businessTypeForm.bind(Map("businessType" -> businessType))

      "have no errors" in {
        result.errors.size shouldBe 0
      }

      "have a valid model" in {
        result.value shouldBe Some(BusinessTypeModel("NUK"))
      }
    }

    "passing in a valid type of 'Company'" should {
      val businessType = "LTD"
      val result = form.businessTypeForm.bind(Map("businessType" -> businessType))

      "have no errors" in {
        result.errors.size shouldBe 0
      }

      "have a valid model" in {
        result.value shouldBe Some(BusinessTypeModel("LTD"))
      }
    }

    "passing in an invalid type of 'Agent'" should {
      val businessType = "LLP"
      val result = form.businessTypeForm.bind(Map("businessType" -> businessType))

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
      val businessType = ""
      val result = form.businessTypeForm.bind(Map("businessType" -> businessType))

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
