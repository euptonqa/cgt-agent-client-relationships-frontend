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
import forms.BusinessUtrDetailsForm
import org.jsoup.Jsoup
import traits.ViewSpecHelper

class BusinessDetailsViewSpec extends ViewSpecHelper {

  lazy val form: BusinessUtrDetailsForm = app.injector.instanceOf[BusinessUtrDetailsForm]

  "The BusinessDetails view" when {

    "no errors are shown" should {
      lazy val view = views.html.company.businessDetails(config, form.businessUtrDetailsForm)
      lazy val doc = Jsoup.parse(view.body)

      s"have a title of '${BusinessDetails.title}'" in {
        doc.title shouldBe BusinessDetails.title
      }

      "have a heading" which {
        lazy val heading = doc.select("h1")

        s"has the text '${BusinessDetails.title}'" in {
          heading.text() shouldBe BusinessDetails.title
        }

        "has the class 'heading-xlarge'" in {
          heading.attr("class") shouldBe "heading-xlarge"
        }
      }

      "has a subheading" which {
        lazy val subHeading = doc.select("article p").first()

        s"has the text '${BusinessDetails.subHeading}'" in {
          subHeading.text() shouldBe BusinessDetails.subHeading
        }

        "has the class 'lede form-group'" in {
          subHeading.attr("class") shouldBe "lede form-group"
        }
      }

      "has a form" which {
        lazy val form = doc.select("form")

        "has a method of POST" in {
          form.attr("method") shouldBe "POST"
        }

        s"has an action of '${controllers.routes.ClientController.submitBusinessDetails().url}'" in {
          form.attr("action") shouldBe controllers.routes.ClientController.submitBusinessDetails().url
        }
      }

      "has an input for company name" which {
        lazy val input = doc.select("label[for=companyName]")

        s"has the label '${BusinessDetails.registeredName}'" in {
          input.select("span").first().text() shouldBe BusinessDetails.registeredName
        }

        "has a hint" which {
          lazy val hint = input.select("span").get(1)

          s"has the text '${BusinessDetails.registeredNameHint}'" in {
            hint.text() shouldBe BusinessDetails.registeredNameHint
          }

          "has the class 'form-hint'" in {
            hint.attr("class") shouldBe "form-hint"
          }
        }
      }

      "has an input for the utr" which {
        lazy val input = doc.select("label[for=utr]")

        s"has the label '${BusinessDetails.utr}'" in {
          input.select("span").first().text() shouldBe BusinessDetails.utr
        }

        "has a hint" which {
          lazy val hint = input.select("span").get(1)

          s"has the text '${BusinessDetails.utrHelp}'" in {
            hint.text() shouldBe BusinessDetails.utrHelp
          }

          "has the class 'form-hint'" in {
            hint.attr("class") shouldBe "form-hint"
          }
        }
      }

      "has additional content for the utr" which {
        lazy val content = doc.select("details")

        "has a role of 'group'" in {
          content.attr("role") shouldBe "group"
        }

        "contains a summary" which {
          lazy val summary = content.select("summary")

          s"has the text '${BusinessDetails.utrLink}'" in {
            summary.text() shouldBe BusinessDetails.utrLink
          }

          "has the span class 'summary'" in {
            summary.select("span").attr("class") shouldBe "summary"
          }

          "has the role 'button'" in {
            summary.attr("role") shouldBe "button"
          }

          "has the aria-controls 'details-content'" in {
            summary.attr("aria-controls") shouldBe "details-content"
          }

          "has the aria-expanded 'false'" in {
            summary.attr("aria-expanded") shouldBe "false"
          }
        }

        "contains a div" which {
          lazy val div = content.select("div")

          s"has a first paragraph of '${BusinessDetails.utrHelpOne}'" in {
            div.select("p").get(0).text() shouldBe BusinessDetails.utrHelpOne
          }

          s"has a second paragraph of '${BusinessDetails.utrHelpTwo}'" in {
            div.select("p").get(1).text() shouldBe BusinessDetails.utrHelpTwo
          }

          s"has a third paragraph of '${BusinessDetails.utrHelpThree}'" in {
            div.select("p").get(2).text() shouldBe BusinessDetails.utrHelpThree
          }

          "has a class of 'indent'" in {
            div.attr("class") shouldBe "indent"
          }

          "has an id of 'details-content'" in {
            div.attr("id") shouldBe "details-content"
          }

          "has an aria-hidden of 'true'" in {
            div.attr("aria-hidden") shouldBe "true"
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
      val map = Map("companyName" -> "")
      lazy val view = views.html.company.businessDetails(config, form.businessUtrDetailsForm.bind(map))
      lazy val doc = Jsoup.parse(view.body)

      "have an error summary" in {
        lazy val errorSummary = doc.select("div#error-summary-display")
        errorSummary.size() shouldBe 1
      }
    }
  }
}
