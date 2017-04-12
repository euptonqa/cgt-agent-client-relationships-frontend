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

import data.MessageLookup
import data.MessageLookup.{ClientConfirmation => messages}
import org.jsoup.Jsoup
import traits.ViewSpecHelper

class ClientConfirmationViewSpec extends ViewSpecHelper {

  "The cgtSubscriptionConfirmationView" should {

    lazy val view = views.html.clientConfirmation(config, "Generic CGT reference", "/context/test")
    lazy val doc = Jsoup.parse(view.body)

    s"display a title of ${messages.title}" in {
      doc.title shouldEqual messages.title
    }

    "have a heading" which {

      s"should have the text ${messages.title}" in {
        doc.select("h1").text shouldEqual messages.title
      }

      "should have the class visually hidden" in {
        doc.select("h1").hasClass("visuallyhidden") shouldEqual true
      }
    }

    "have a banner" which {
      lazy val greenBanner = doc.select("div #confirmation-banner")

      "has the class transaction-banner--complete" in {
        greenBanner.hasClass("transaction-banner--complete") shouldEqual true
      }

      s"has the first span" which {

        s"has text ${messages.reference}" in {
          greenBanner.select("span").get(0).text shouldEqual messages.reference
        }

        "has the class bold-large" in {
          greenBanner.select("span").get(0).hasClass("heading-large") shouldEqual true
        }
      }

      "has a paragraph" which {

        "has the text 'Generic CGT reference'" in {
          greenBanner.select("p").text shouldEqual "Generic CGT reference"
        }

        "has the class heading-medium" in {
          greenBanner.select("p").hasClass("heading-medium") shouldEqual true
        }
      }
    }

    "have guidance for what to do next" which {
      lazy val guidance = doc.select("div.form-group")

      "has a heading" which {

        "has the class 'heading-medium'" in {
          guidance.select("span").get(0).attr("class") shouldBe "heading-medium"
        }

        s"has the text '${messages.whatNext}'" in {
          guidance.select("span").get(0).text shouldBe messages.whatNext
        }
      }

      "has some content" which {

        s"has the text '${messages.guidance}'" in {
          guidance.select("p").text() shouldBe messages.guidance
        }
      }
    }

    "have a link" which {
      lazy val link = doc.select("#content a").first()

      "has the class 'button'" in {
        link.attr("class") shouldBe "button"
      }

      s"has the href '${controllers.routes.AgentController.showClientList("/context/test").url}'" in {
        link.attr("href") shouldBe controllers.routes.AgentController.showClientList("/context/test").url
      }

      "has the text 'Continue'" in {
        link.text() shouldBe MessageLookup.Common.continue
      }
    }
  }
}
