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

import common.Constants.BusinessType._
import data.MessageLookup.{Common, BusinessType => messages}
import forms.BusinessTypeForm
import org.jsoup.Jsoup
import traits.ViewSpecHelper

class BusinessTypeViewSpec extends ViewSpecHelper {

  "The BusinessType view" when {
    lazy val form = app.injector.instanceOf[BusinessTypeForm]

    "no errors are shown" should {
      lazy val view = views.html.company.businessType(config, form.businessTypeForm)
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

        s"has an action of '${controllers.routes.ClientController.submitBusinessType().url}'" in {
          form.attr("action") shouldBe controllers.routes.ClientController.submitBusinessType().url
        }
      }

      "have a radio input" which {
        lazy val input = doc.select("fieldset")

        "has a class of 'form-group radio-list'" in {
          input.attr("class") shouldBe "form-group radio-list"
        }

        "has a legend with a class of 'visuallyhidden'" in {
          input.select("legend").attr("class") shouldBe "visuallyhidden"
        }

        s"has the legend '${messages.title}'" in {
          input.select("legend").text() shouldBe messages.title
        }

        "has a label for the non UK option" which {
          lazy val nonUKLabel = input.select("label[for=businessType-NUK]")

          "has the class 'block-label'" in {
            nonUKLabel.attr("class") shouldBe "block-label"
          }

          s"has the text '${messages.nonUK}'" in {
            nonUKLabel.text() shouldBe messages.nonUK
          }

          "has the name 'businessType'" in {
            nonUKLabel.select("input").attr("name") shouldBe "businessType"
          }

          s"has the value '$nonUK'" in {
            nonUKLabel.select("input").attr("value") shouldBe nonUK
          }
        }

        "has a label for the limited company option" which {
          lazy val companyLabel = input.select("label[for=businessType-LTD]")

          "has the class 'block-label'" in {
            companyLabel.attr("class") shouldBe "block-label"
          }

          s"has the text '${messages.limitedCompany}'" in {
            companyLabel.text() shouldBe messages.limitedCompany
          }

          "has the name 'businessType'" in {
            companyLabel.select("input").attr("name") shouldBe "businessType"
          }

          s"has the value '$limitedCompany'" in {
            companyLabel.select("input").attr("value") shouldBe limitedCompany
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
      val map = Map("businessType" -> "")
      lazy val view = views.html.company.businessType(config, form.businessTypeForm.bind(map))
      lazy val doc = Jsoup.parse(view.body)

      "have an error summary" in {
        lazy val errorSummary = doc.select("div#error-summary-display")
        errorSummary.size() shouldBe 1
      }
    }
  }
}
