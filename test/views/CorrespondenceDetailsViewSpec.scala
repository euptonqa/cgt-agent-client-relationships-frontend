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

package views

import data.MessageLookup.CorrespondenceDetails
import forms.CorrespondenceDetailsForm
import org.jsoup.Jsoup
import traits.ViewSpecHelper
import views.html.individual.correspondenceDetails

class CorrespondenceDetailsViewSpec extends ViewSpecHelper {

  "The correspondence details view with a form with no errors" should {
    lazy val form = new CorrespondenceDetailsForm(messagesApi)
    lazy val view = correspondenceDetails(config, form.correspondenceDetailsForm)
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

      s"has an action of '${controllers.routes.ClientController.enterIndividualCorrespondenceDetails().url}'" in {
        form.attr("action") shouldBe controllers.routes.ClientController.enterIndividualCorrespondenceDetails().url
      }
    }

    "have an input for first name" which {
      lazy val label = doc.select("label[for=firstName]")
      lazy val input = label.select("input#firstName")

      s"has the text '${CorrespondenceDetails.firstName}'" in {
        label.text() shouldBe CorrespondenceDetails.firstName
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }
    }

    "have an input for last name" which {
      lazy val label = doc.select("label[for=lastName]")
      lazy val input = label.select("input#lastName")

      s"has the text '${CorrespondenceDetails.lastName}'" in {
        label.text() shouldBe CorrespondenceDetails.lastName
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }
    }

    "have a heading for contact address" which {
      lazy val heading = doc.getElementsByTag("p").eq(2)

      s"has the text '${CorrespondenceDetails.contactAddress}'" in {
        heading.text() shouldBe CorrespondenceDetails.contactAddress
      }

      "has a class of heading-large" in {
        heading.attr("class") shouldBe "heading-medium"
      }
    }

    "have a paragraph for building and street" which {
      lazy val heading = doc.getElementsByTag("p").eq(3)

      s"has the text '${CorrespondenceDetails.buildingAndStreet}'" in {
        heading.text() shouldBe CorrespondenceDetails.buildingAndStreet
      }
    }

    "have a visually hidden label for building" which {
      lazy val label = doc.select("label span").eq(2)

      s"has the text '${CorrespondenceDetails.building}'" in {
        label.text() shouldBe CorrespondenceDetails.building
      }

      "has a class of visuallyhidden" in {
        label.hasClass("visuallyhidden") shouldBe true
      }
    }

    "have a visually hidden label for street" which {
      lazy val label = doc.select("label span").eq(3)

      s"has the text '${CorrespondenceDetails.street}'" in {
        label.text() shouldBe CorrespondenceDetails.street
      }

      "has a class of visuallyhidden" in {
        label.hasClass("visuallyhidden") shouldBe true
      }
    }

    "have an input for town or city" which {
      lazy val label = doc.select("label[for=townOrCity]")
      lazy val input = label.select("input#townOrCity")

      s"has the text '${CorrespondenceDetails.town}'" in {
        label.text() shouldBe CorrespondenceDetails.town
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }
    }

    "have an input for county" which {
      lazy val label = doc.select("label[for=county]")
      lazy val input = label.select("input#county")

      s"has the text '${CorrespondenceDetails.county}'" in {
        label.text() shouldBe CorrespondenceDetails.county
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
      }
    }

    "have an input for country" which {
      lazy val label = doc.select("label[for=country]")
      lazy val input = label.select("input#country")

      s"has the text '${CorrespondenceDetails.country}'" in {
        label.text() shouldBe CorrespondenceDetails.country
      }

      "has a label class of 'form-group'" in {
        label.attr("class") should include("form-group")
      }

      "has a type of text" in {
        input.attr("type") shouldBe "text"
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

      "has a class of 'input-small'" in {
        input.attr("class") shouldBe "shim input--small"
      }
    }
  }

  "The correspondence details view with a form with errors" should {
    lazy val form = new CorrespondenceDetailsForm(messagesApi)
    lazy val map = Map("firstName" -> "John", "lastName" -> "Smith", "addressLineOne" -> "", "addressLineTwo" -> "Light Road",
      "town" -> "Dark City", "county" -> "Darkshire", "postcode" -> "TF4 3NT", "country" -> "United States")
    lazy val view = correspondenceDetails(config, form.correspondenceDetailsForm.bind(map))
    lazy val doc = Jsoup.parse(view.body).toString

    "display an error summary" in {
      doc should include("id=\"error-summary-display\"")
    }
  }
}
