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

import models.AddressModel
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec


class CorrespondenceAddressFormSpec extends UnitSpec with OneAppPerSuite {

  "Creating a CorrespondenceAddressForm" when {
    val form = app.injector.instanceOf[CorrespondenceAddressForm]
    "provided with a valid mpa with no optional values" should {
      val map = Map("addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "", "county" -> "", "postcode" -> "", "country" -> "United States")

      lazy val result = form.correspondenceAddressForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe true
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe AddressModel("15", "Light Road", None, None, None, "United States")
      }

      "contain no errors" in {
        result.errors.isEmpty shouldBe true
      }

    }

    "provided with a valid map with all optional values" should {
      val map = Map("addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Telford", "county" -> "Shropshire", "postcode" -> "LA1 5AP", "country" -> "United States")

      lazy val result = form.correspondenceAddressForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe true
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe AddressModel("15", "Light Road", Some("Telford"), Some("Shropshire"), Some("LA1 5AP"), "United States")
      }

      "contain no errors" in {
        result.errors.isEmpty shouldBe true
      }

    }
  }

}
