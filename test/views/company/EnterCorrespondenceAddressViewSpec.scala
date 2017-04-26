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

package views.company

import data.MessageLookup
import data.MessageLookup.{Common, CorrespondenceDetails}
import forms.CorrespondenceAddressForm
import org.jsoup.Jsoup
import traits.ViewSpecHelper
import views.html.company.correspondenceAddress

class EnterCorrespondenceAddressViewSpec extends ViewSpecHelper {
  "The correspondence details view with a form with no errors" should {
    lazy val form = new CorrespondenceAddressForm(messagesApi)
    lazy val view = correspondenceAddress(config, form.correspondenceAddressForm, List(("test", "test")))
    lazy val doc = Jsoup.parse(view.body)

    "have a header" which {
      lazy val header = doc.select("h1")

      "has a class of heading-xlarge" in {
        header.attr("class") shouldBe "heading-xlarge"
      }

      s"has the text ${CorrespondenceDetails.title}" in {
        header.text() shouldBe CorrespondenceDetails.title
      }
    }

    "have a form" which {
      lazy val form = doc.select("form")

      "has a POST method" in {
        form.attr("method") shouldBe "POST"
      }

      s"has an action of ${controllers.routes.CompanyController.enterCorrespondenceAddress().url}" in {
        form.attr("action") shouldBe controllers.routes.CompanyController.enterCorrespondenceAddress().url
      }
    }

    "have an input for address line 1" which {
      lazy val label = doc.select("label[for=addressLineOne]")
      lazy val input = label.select("input#addressLineOne")

      s"has the text ${CorrespondenceDetails.addressLine1}" in {
        label.text() shouldBe CorrespondenceDetails.addressLine1
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }
    }

    "have an input for address line 2" which {
      lazy val label = doc.select("label[for=addressLineTwo]")
      lazy val input = label.select("input#addressLineTwo")

      s"has the text ${CorrespondenceDetails.addressLine2}" in {
        label.text() shouldBe CorrespondenceDetails.addressLine2
      }

      "las a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }
    }

    "have an input for address line 3" which {
      lazy val label = doc.select("label[for=addressLineThree]")
      lazy val input = doc.select("input#addressLineThree")

      s"has the text ${CorrespondenceDetails.addressLine3}" in {
        label.text() shouldBe CorrespondenceDetails.addressLine3
      }

      "las a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }
    }

    "have an input for address line 4" which {
      lazy val label = doc.select("label[for=addressLineFour]")
      lazy val input = doc.select("input#addressLineFour")

      s"has the text ${CorrespondenceDetails.addressLine4}" in {
        label.text() shouldBe CorrespondenceDetails.addressLine4
      }

      "las a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }
    }

    "has an input for country" which {
      lazy val input = doc.body().select("#country_div")
      lazy val label = input.select("label")
      lazy val select = input.select("select")

      s"has the text '${CorrespondenceDetails.country}'" in {
        label.text() shouldBe CorrespondenceDetails.country
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-label")
      }

      "has a class of 'form-control'" in {
        select.attr("class") shouldBe " form-control "
      }

      "has a name of 'country'" in {
        select.attr("name") shouldBe "country"
      }
    }
    "have an input for postcode" which {
      lazy val label = doc.select("label[for=postcode]")
      lazy val input = label.select("input#postcode")

      s"has the text '${CorrespondenceDetails.postcode}'" in {
        label.text() shouldBe CorrespondenceDetails.postcode
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }
    }

    "has a Continue button" which {
      lazy val button = doc.select("button")

      s"has the text ${Common.continue}" in {
        button.text() shouldBe Common.continue
      }
    }
  }

  "The view is supplied with a form with errors" should {
    lazy val form = new CorrespondenceAddressForm(messagesApi)
    lazy val map = Map("addressLineOne" -> "", "addressLineTwo" -> "Light Road",
      "townOrCity" -> "Telford", "county" -> "Shropshire", "postcode" -> "LA1 5AP", "country" -> "United States")
    lazy val view = correspondenceAddress(config, form.correspondenceAddressForm.bind(map), List(("test", "test")))
    lazy val doc = Jsoup.parse(view.body).toString

    "display an error summary" in {
      doc should include("id=\"error-summary-display\"")
    }
  }
}
