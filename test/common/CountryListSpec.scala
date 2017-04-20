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

package common

import org.scalatestplus.play.OneAppPerSuite
import uk.gov.hmrc.play.test.UnitSpec

class CountryListSpec extends UnitSpec with OneAppPerSuite {

  lazy val helper: CountryList = app.injector.instanceOf[CountryList]

  "Calling .getSelectedCountry" should {

    "return a country of Germany from DE" in {
      helper.getSelectedCountry("DE") shouldBe "Germany"
    }

    "return a country of United Kingdom from GB" in {
      helper.getSelectedCountry("GB") shouldBe "United Kingdom"
    }
  }
}
