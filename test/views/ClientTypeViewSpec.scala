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

import org.jsoup.Jsoup
import traits.ViewSpecHelper
import data.MessageLookup.{Common, ClientType => messages}
import forms.ClientTypeForm

class ClientTypeViewSpec extends ViewSpecHelper {

  "The ClientType view" when {
    lazy val form = app.injector.instanceOf[ClientTypeForm]

    "no errors are shown" should {
      lazy val view = views.html.clientType(config, form.clientTypeForm)
      lazy val doc = Jsoup.parse(view.body)

      s"have a title of '${messages.title}'" in {
        doc.title shouldBe messages.title
      }

      "have a header" which {
        lazy val header = doc.select("h1")

        "has the class 'heading-xlarge'" in {
          header.attr("class") shouldBe "heading-xlarge"
        }

        s"has the text '${messages.title}'" in {
          header.text() shouldBe messages.title
        }
      }

      "have a form" which {
        lazy val form = doc.body().select("form")

        "has a method of POST" in {
          form.attr("method") shouldBe "POST"
        }

        s"has an action of '${controllers.routes.ClientController.submitClientType().url}'" in {
          form.attr("action") shouldBe controllers.routes.ClientController.submitClientType().url
        }
      }

      "have a radio input" which {
        lazy val input = doc.select("fieldset")

        "has a class of 'inline form-group radio-list'" in {
          input.attr("class") shouldBe "form-group radio-list"
        }

        "has a legend with a class of 'visuallyhidden'" in {
          input.select("legend").attr("class") shouldBe "visuallyhidden"
        }

        s"has the legend '${messages.title}'" in {
          input.select("legend").text() shouldBe messages.title
        }

        "has a label for the individual option" which {
          lazy val individualLabel = input.select("label[for=clientType-Individual]")

          "has the class 'block-label'" in {
            individualLabel.attr("class") shouldBe "block-label"
          }

          s"has the text '${messages.individual}'" in {
            individualLabel.text() shouldBe messages.individual
          }

          "has the name 'clientType'" in {
            individualLabel.select("input").attr("name") shouldBe "clientType"
          }

          s"has the value '${messages.individual}'" in {
            individualLabel.select("input").attr("value") shouldBe messages.individual
          }
        }

        "has a label for the company option" which {
          lazy val companyLabel = input.select("label[for=clientType-Company]")

          "has the class 'block-label'" in {
            companyLabel.attr("class") shouldBe "block-label"
          }

          "has the text 'Company'" in {
            companyLabel.text() shouldBe "Company"
          }

          "has the name 'clientType'" in {
            companyLabel.select("input").attr("name") shouldBe "clientType"
          }

          "has the value 'Company'" in {
            companyLabel.select("input").attr("value") shouldBe "Company"
          }
        }
      }

      "have a button" which {
        lazy val button = doc.select("button")

        "has the text 'Continue'" in {
          button.text() shouldBe Common.continue
        }

        "has the class 'button'" in {
          button.attr("class") shouldBe "button"
        }

        "has the type 'submit'" in {
          button.attr("type") shouldBe "submit"
        }

        "has the id 'continue-button'" in {
          button.attr("id") shouldBe "continue-button"
        }
      }

      "have no error summary" in {
        lazy val errorSummary = doc.select("div#error-summary-display")
        errorSummary.size() shouldBe 0
      }
    }

    "errors are shown" should {
      val map = Map("clientType" -> "")
      lazy val view = views.html.clientType(config, form.clientTypeForm.bind(map))
      lazy val doc = Jsoup.parse(view.body)

      "have an error summary" in {
        lazy val errorSummary = doc.select("div#error-summary-display")
        errorSummary.size() shouldBe 1
      }
    }
  }
}
