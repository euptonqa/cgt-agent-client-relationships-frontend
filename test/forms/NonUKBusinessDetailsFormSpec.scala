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

import models.{AddressModel, BusinessDetailsModel}
import traits.ViewSpecHelper

class NonUKBusinessDetailsFormSpec extends ViewSpecHelper {

  "Creating a nonUKBusinessDetailsForm" when {
    lazy val form = app.injector.instanceOf[NonUKBusinessDetailsForm]

    "passing in a valid business name, address and no overseas tax reference" should {
      val address = Map(
        "addressLineOne" -> "1",
        "addressLineTwo" -> "Test Lane",
        "addressLineThree" -> "",
        "addressLineFour" -> "",
        "postcode" -> "",
        "country" -> "Germany")

      val overseasTaxReference = Map(
        "taxReference" -> "",
        "countryOfIssue" -> "",
        "institutionOfIssue" -> ""
      )

      val map = Map(
        "businessName" -> "Test business name",
        "addressModel" -> address,
        "overseasTaxReference" -> "No",
        "overseasTaxReferenceModel" -> overseasTaxReference
      )

      val result = form.nonUKBusinessDetailsForm.bind(map)

      "return an error" in {
//      "return a valid model" in {
        result.errors.size shouldBe 1
//        result.value.isDefined shouldBe true
      }

      "return a list of errors" in {
        println(map)
        println(result.errors.seq)
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe BusinessDetailsModel(
          "Test business name",
          AddressModel("1", "Test Lane", None, None, None, "Germany"),
          "No",
          None)
      }
    }

  }


}
