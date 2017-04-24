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

import config.AppConfig
import data.MessageLookup.{ReviewClientsDetails => messages}
import models.CompanyAddressModel
import org.jsoup.Jsoup
import org.scalatestplus.play.OneAppPerSuite
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.inject.Injector
import traits.FakeRequestHelper
import uk.gov.hmrc.play.test.UnitSpec
import views.html.reviewClientsDetails


class reviewClientsDetailsSpec extends UnitSpec with OneAppPerSuite with FakeRequestHelper with I18nSupport {
  lazy val injector: Injector = app.injector
  lazy val appConfig: AppConfig = injector.instanceOf[AppConfig]
  implicit lazy val messagesApi: MessagesApi = injector.instanceOf[MessagesApi]

  "The ReviewBusinessDetailsView" should {

    val registeredModel = CompanyAddressModel(Some("line1"), Some("line2"), Some("line3"), Some("line4"), Some("postCode"), Some("country"))

    lazy val view = reviewClientsDetails(appConfig, registeredModel, "business name")
    lazy val doc = Jsoup.parse(view.body)

    "contain a header" which {
      "has the class 'heading-xlarge'" in {
        doc.select("h1").attr("class") shouldBe "heading-xlarge"
      }

      s"has the message '${messages.title}" in {
        doc.select("h1").text() shouldBe messages.title
      }
    }


    "has a div of class form-group" which {
      lazy val element = doc.select("div.form-group")
      "has a table that" should {
        lazy val table = element.select("table")

        "contain a header" which {
          "has the class 'bold-medium'" in {
            doc.select("h2").attr("class") shouldBe "bold-medium"
          }

        }

        "contain a list" which {
          lazy val list = doc.select("main ul")

          "has the first address line" in {
          list.select("li").get(0).text shouldBe "line1"
        }

        "has the second address line" in {
          list.select("li").get(1).text shouldBe "line2"
        }

        "has the third address line" in {
          list.select("li").get(2).text shouldBe "line3"
        }

        "has the fourth address line" in {
          list.select("li").get(3).text shouldBe "line4"
        }

        "has the postcode" in {
          list.select("li").get(4).text shouldBe "postCode"
        }

        "has the country" in {
          list.select("li").get(5).text shouldBe "country"
        }
      }


        "have a button" which {
          lazy val button = doc.select("button")
          s"has the text ${messages.confirmContinue}" in {
            button.text() shouldBe messages.confirmContinue
          }

          "has the class 'button'" in {
            button.attr("class") shouldBe "button"
          }

          "has the type 'submit'" in {
            button.attr("type") shouldBe "submit"
          }
        }
      }
    }
  }
}