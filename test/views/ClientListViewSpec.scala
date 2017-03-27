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

import data.MessageLookup.{ClientList => messages}
import models.{Client, IdentifierForDisplay}
import org.jsoup.Jsoup
import traits.ViewSpecHelper
import views.html.clientList

class ClientListViewSpec extends ViewSpecHelper {

  "The clientList view" should {

    val clients: Seq[Client] = Seq(
      Client("John Smith", List(IdentifierForDisplay("CGTREF", "CGT12345678"))),
      Client("Company 123", List(IdentifierForDisplay("CGTREF", "CGT01234567"))))

    lazy val view = clientList(config, clients)
    lazy val doc = Jsoup.parse(view.body)

    s"display a title of ${messages.title}" in {
      doc.title shouldEqual messages.title
    }

    "have a heading" which {

      s"has the text '${messages.title}'" in {
        doc.select("h1").text() shouldEqual messages.title
      }

      "should have the class heading-xlarge" in {
        doc.select("h1").hasClass("heading-xlarge") shouldEqual true
      }
    }

    "have a div of class form-group" which {

      lazy val element = doc.select("div.form-group")

      "has a table" which {

        lazy val table = element.select("table")

        "has a table row" which {

          lazy val row = table.select("tr:nth-of-type(1)")

          "has a table header" which {

            lazy val th = row.select("th")

            "has the class bold-small" in {
              th.hasClass("bold-small") shouldBe true
            }

            s"has the text ${messages.clientName}" in {
              th.text shouldEqual messages.clientName
            }
          }

          "has an empty td" which {
            lazy val td = row.select("td:nth-of-type(2)")

            "is empty" in {
              td.isEmpty shouldBe true
            }
          }
        }

        "has a second table row" which {

          lazy val row2 = table.select("tr:nth-of-type(2)")

          "has a form" which {

            lazy val form = row2.select("form")

            "has a POST method" in {
              form.attr("method") shouldBe "POST"
            }

            s"has an action of '${controllers.routes.AgentController.selectClient().url}'" in {
              form.attr("action") shouldBe controllers.routes.AgentController.selectClient().url
            }
          }

          "has a td" which {

            lazy val td = row2.select("td:nth-of-type(1)")

            "has the class font-small" in {
              td.hasClass("font-small") shouldBe true
            }

            "has the text 'John Smith'" in {
              td.text shouldEqual "John Smith"
            }

            "has an input 'friendlyName'" should {

              lazy val friendlyName = td.select("#friendlyName")

              "have class visually hidden" in {
                friendlyName.hasClass("visuallyhidden") shouldEqual true
              }

              "have the value 'John Smith'" in {
                friendlyName.attr("value") shouldEqual "John Smith"
              }
            }

            "has an input 'cgtRef'" should {

              lazy val cgtRef = td.select("#cgtRef")

              "have class visually hidden" in {
                cgtRef.hasClass("visuallyhidden") shouldEqual true
              }

              "have the value 'CGT12345678'" in {
                cgtRef.attr("value") shouldEqual "CGT12345678"
              }
            }
          }

          "has a second td" which {

            lazy val td = row2.select("td:nth-of-type(2)")

            "has the class font-small" in {
              td.hasClass("font-small") shouldBe true
            }

            "has a button" which {
              lazy val button = td.select("button")

              "has class 'button--link-style'" in {
                button.hasClass("button--link-style") shouldEqual true
              }

              s"has the text ${messages.report}" in {
                button.text shouldEqual messages.report
              }

              "is of type submit" in {
                button.attr("type") shouldEqual "submit"
              }
            }
          }
        }

        "has a third table row" which {

          lazy val row2 = table.select("tr:nth-of-type(3)")

          "has a td" which {

            lazy val td = row2.select("td:nth-of-type(1)")

            "has the text 'Company 123'" in {
              td.text shouldEqual "Company 123"
            }

            "has an input 'friendlyName'" should {

              lazy val friendlyName = td.select("#friendlyName")

              "have class visually hidden" in {
                friendlyName.hasClass("visuallyhidden") shouldEqual true
              }

              "have the value 'Company 123'" in {
                friendlyName.attr("value") shouldEqual "Company 123"
              }
            }

            "has an input 'cgtRef'" should {

              lazy val cgtRef = td.select("#cgtRef")

              "have class visually hidden" in {
                cgtRef.hasClass("visuallyhidden") shouldEqual true
              }

              "have the value 'CGT01234567'" in {
                cgtRef.attr("value") shouldEqual "CGT01234567"
              }
            }
          }

          "has a second td" which {

            lazy val td = row2.select("td:nth-of-type(2)")

            "has a button" which {
              lazy val button = td.select("button")

              "has class 'button--link-style'" in {
                button.hasClass("button--link-style") shouldEqual true
              }

              s"has the text ${messages.report}" in {
                button.text shouldEqual messages.report
              }

              "is of type submit" in {
                button.attr("type") shouldEqual "submit"
              }
            }
          }
        }
      }
    }

    "has a button" which {

      lazy val button = doc.getElementById("add-client")

      "has the class button" in {
        button.hasClass("button") shouldEqual true
      }

      s"has the text ${messages.addClient}" in {
        button.text shouldEqual messages.addClient
      }
    }
  }
}
