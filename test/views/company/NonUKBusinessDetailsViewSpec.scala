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

import data.MessageLookup._
import org.jsoup.Jsoup
import traits.ViewSpecHelper
import forms.NonUKBusinessDetailsForm
import views.html.company.nonUKBusinessDetails

class NonUKBusinessDetailsViewSpec extends ViewSpecHelper {

  lazy val form: NonUKBusinessDetailsForm = app.injector.instanceOf[NonUKBusinessDetailsForm]

  "The NonUKBusinessDetailsView" when {

    "no errors are displayed" should {

      lazy val view = nonUKBusinessDetails(config, form.nonUKBusinessDetailsForm, List(("test", "test")))
      lazy val doc = Jsoup.parse(view.body)

      s"Have a title of ${NonUKBusinessDetails.title}" in {
        doc.title shouldBe NonUKBusinessDetails.title
      }

      "have a heading" which {
        lazy val heading = doc.select("h1")

        s"has the text '${NonUKBusinessDetails.title}" in {
          heading.text() shouldBe NonUKBusinessDetails.title
        }

        "has the class 'heading-xlarge" in {
          heading.attr("class") shouldBe "heading-xlarge"
        }
      }

      "has a form" which {
        lazy val form = doc.select("form")

        "has a method of POST" in {
          form.attr("method") shouldBe "POST"
        }

        //TODO: Update to actually controller action
        s"has an action of '${controllers.routes.ClientController.businessDetails().url}'" in {
          form.attr("action") shouldBe controllers.routes.ClientController.businessDetails().url
        }
      }

      "has an input for Business Name" which {
        lazy val label = doc.body().select("label[for=businessName")
        lazy val input = label.select("input#businessName")

        s"has the text '${NonUKBusinessDetails.businessName}'" in {
          label.text() shouldBe NonUKBusinessDetails.businessName
        }

        "has a label class of form-group" in {
          label.attr("class") should include("form-group")
        }

        "has a type of text" in {
          input.attr("type") shouldBe "text"
        }

        "has a class of 'shim input grid-1-2'" in {
          input.attr("class") should include("shim  input grid-1-2")
        }
      }

      "has an input for Address" which {
        lazy val label = doc.body().select("label[for=addressLineOne")
        lazy val input = label.select("input#addressLineOne")

        s"has the text '${NonUKBusinessDetails.addressLineOne}'" in {
          label.text() shouldBe NonUKBusinessDetails.addressLineOne
        }

        "has a label class of form-group" in {
          label.attr("class") should include("form-group")
        }

        "has a type of text" in {
          input.attr("type") shouldBe "text"
        }

        "has a class of 'shim input grid-1-2'" in {
          input.attr("class") should include("shim  input grid-1-2")
        }
      }

      "has an input for Address Line Two" which {
        lazy val label = doc.body().select("label[for=addressLineTwo")
        lazy val input = label.select("input#addressLineTwo")

        s"has the text '${NonUKBusinessDetails.addressLineTwo}'" in {
          label.text() shouldBe NonUKBusinessDetails.addressLineTwo
        }

        "has a label class of form-group" in {
          label.attr("class") should include("form-group")
        }

//        "has a visually hidden label" in {
//          label.attr("class") should include("visuallyhidden")
//        }

        "has a type of text" in {
          input.attr("type") shouldBe "text"
        }

        "has a class of 'shim input grid-1-2'" in {
          input.attr("class") should include("shim  input grid-1-2")
        }
      }

      "has an input for Address Line Three" which {
        lazy val label = doc.body().select("label[for=addressLineThree")
        lazy val input = label.select("input#addressLineThree")

        s"has the text '${NonUKBusinessDetails.addressLineThree}'" in {
          label.text() shouldBe NonUKBusinessDetails.addressLineThree
        }

        "has a label class of form-group" in {
          label.attr("class") should include("form-group")
        }

//        "has a visually hidden label" in {
//          label.attr("class") should include("visuallyhidden")
//        }

        "has a type of text" in {
          input.attr("type") shouldBe "text"
        }

        "has a class of 'shim input grid-1-2'" in {
          input.attr("class") should include("shim  input grid-1-2")
        }
      }

      "has an input for Address Line Four" which {
        lazy val label = doc.body().select("label[for=addressLineFour")
        lazy val input = label.select("input#addressLineFour")

        s"has the text '${NonUKBusinessDetails.addressLineFour}'" in {
          label.text() shouldBe NonUKBusinessDetails.addressLineFour
        }

        "has a label class of form-group" in {
          label.attr("class") should include("form-group")
        }

//        "has a visually hidden label" in {
//          label.attr("class") should include("visuallyhidden")
//        }

        "has a type of text" in {
          input.attr("type") shouldBe "text"
        }

        "has a class of 'shim input grid-1-2'" in {
          input.attr("class") should include("shim  input grid-1-2")
        }
      }

      "have an input for country" which {
        lazy val input = doc.body().select("#country_div")
        lazy val label = input.select("label")
        lazy val select = input.select("select")

        s"has the text '${NonUKBusinessDetails.country}'" in {
          label.text() shouldBe NonUKBusinessDetails.country
        }

        "has a label class of 'form-group'" in {
          label.attr("class") should include("form-label")
        }

        "has a class of 'form-control'" in {
          select.attr("class") should include("form-control")
        }

        "has a name of 'country'" in {
          select.attr("name") shouldBe "country"
        }
      }
    }
  }
}
