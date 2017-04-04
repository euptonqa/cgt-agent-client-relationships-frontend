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

import data.MessageLookup.Errors
import models.CorrespondenceDetailsModel
import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec

class CorrespondenceDetailsFormSpec extends UnitSpec with OneAppPerSuite{

  "Creating a CorrespondenceDetailsForm" when {
    val form = app.injector.instanceOf[CorrespondenceDetailsForm]

    "provided with a valid map with no optional values" should {
      val map = Map("firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
                      "townOrCity" -> "Dark City", "county" -> "", "postcode" -> "", "country" -> "US")
      lazy val result = form.correspondenceDetailsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe true
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe CorrespondenceDetailsModel("John", "Smith", "15", "Light Road", "Dark City", None, None, "US")
      }

      "contain no errors" in {
        result.errors.isEmpty shouldBe true
      }
    }

    "provided with a valid map with all optional values" should {
      val map = Map("firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Dark City", "county" -> "Darkshire", "postcode" -> "TF4 3NT", "country" -> "US")
      lazy val result = form.correspondenceDetailsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe true
      }

      "return a model containing the stored data" in {
        result.value.get shouldBe CorrespondenceDetailsModel("John", "Smith", "15", "Light Road", "Dark City",
          Some("Darkshire"), Some("TF4 3NT"), "US")
      }

      "contain no errors" in {
        result.errors.isEmpty shouldBe true
      }
    }

    "provided with a invalid map with no first name" should {
      val map = Map("firstName" -> "", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Dark City", "county" -> "Darkshire", "postcode" -> "TF4 3NT", "country" -> "US")
      lazy val result = form.correspondenceDetailsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorRequired
      }
    }

    "provided with a invalid map with no last name" should {
      val map = Map("firstName" -> "John", "lastName" -> "", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Dark City", "county" -> "Darkshire", "postcode" -> "TF4 3NT", "country" -> "US")
      lazy val result = form.correspondenceDetailsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorRequired
      }
    }

    "provided with a invalid map with no address line one" should {
      val map = Map("firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Dark City", "county" -> "Darkshire", "postcode" -> "TF4 3NT", "country" -> "US")
      lazy val result = form.correspondenceDetailsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorRequired
      }
    }

    "provided with a invalid map with no address line two" should {
      val map = Map("firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "",
        "townOrCity" -> "Dark City", "county" -> "Darkshire", "postcode" -> "TF4 3NT", "country" -> "US")
      lazy val result = form.correspondenceDetailsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorRequired
      }
    }

    "provided with a invalid map with no townOrCity" should {
      val map = Map("firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "", "county" -> "Darkshire", "postcode" -> "TF4 3NT", "country" -> "US")
      lazy val result = form.correspondenceDetailsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.errorRequired
      }
    }

    "provided with a invalid map with no valid country code" should {
      val map = Map("firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "15", "addressLineTwo" -> "Light Road",
        "townOrCity" -> "Dark City", "county" -> "Darkshire", "postcode" -> "TF4 3NT", "country" -> "")
      lazy val result = form.correspondenceDetailsForm.bind(map)

      "return a valid model" in {
        result.value.isDefined shouldBe false
      }

      "contain one error" in {
        result.errors.size shouldBe 1
      }

      "contain an error message for a required field" in {
        result.errors.head.message shouldBe Errors.country
      }
    }
  }
}
