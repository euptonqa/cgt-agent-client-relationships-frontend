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

import models.{AddressModel, BusinessDetailsModel, ContactDetailsModel}
import org.jsoup.Jsoup
import traits.ViewSpecHelper
import views.html.company.confirmBusinessDetails
import data.MessageLookup.{ConfirmBusinessDetails => messages}

class ConfirmBusinessDetailsViewSpec extends ViewSpecHelper {

  "The confirmBusinessDetails view" should {

    val addressModel = AddressModel("address line 1", "address line 2", None, None, Some("postcode"), "France")
    val contactDetailsModel = ContactDetailsModel("john smith", "12345 567890")
    val businessDetailsModel = BusinessDetailsModel("business name", addressModel, None)

    lazy val view = confirmBusinessDetails(config, businessDetailsModel, addressModel, contactDetailsModel)
    lazy val doc = Jsoup.parse(view.body)

    "contain a header" which {

      "has the class 'heading-xlarge'" in {
        doc.select("h1").attr("class") shouldBe "heading-xlarge"
      }

      s"has the message '${messages.title}" in {
        doc.select("h1").text() shouldBe messages.title
      }
    }

    "have a h2" which {

      "has the class 'heading'" in {
        doc.select("h2").attr("class") shouldBe "heading"
      }

      "has the inline style 'font-weight:normal'" in {
        doc.select("h2").attr("style") shouldBe "font-weight:normal"
      }
    }

    "has a div of class form-group" which {

      lazy val element = doc.select("div.form-group")

      "has a table that" should {

        lazy val table = element.select("table")

        "that should have a table row" which {

          lazy val row = table.select("tr:nth-of-type(1)")

          "has an header with an inline style of 'width:60%'" in {
            row.select("th").attr("style") shouldBe "width:60%"
          }

          "has a td with the text 'business name''" in {
            row.select("td:nth-of-type(1)").text() shouldBe "business name"
          }

          "has an empty td" which {

            lazy val tdTwo = row.select("td:nth-of-type(2)")

            "has empty html" in {
              tdTwo.html() shouldBe ""
            }
          }
        }

        "has a secondary table row" which {

          lazy val row = table.select("tr:nth-of-type(2)")

          "has a table header" which {
            lazy val tHeader = row.select("th")

            "has an inline style of 'vertical-align: top'" in {
              tHeader.attr("style") shouldBe "vertical-align: top"
            }

            s"with a title ${messages.registeredAddress}" in {
              tHeader.text() shouldBe messages.registeredAddress
            }

            "has a td" which {
              lazy val tdOne = row.select("td:nth-of-type(1) ul")

              "has four list elements" in {
                tdOne.select("li").size() shouldBe 4
              }

              s"has a first element of ${businessDetailsModel.addressModel.addressLineOne}" in {
                tdOne.select("li").get(0).text() shouldBe businessDetailsModel.addressModel.addressLineOne
              }

              s"has a second element of ${businessDetailsModel.addressModel.addressLineTwo}" in {
                tdOne.select("li").get(1).text() shouldBe businessDetailsModel.addressModel.addressLineTwo
              }

              s"has a third element of ${businessDetailsModel.addressModel.postcode.get}" in {
                tdOne.select("li").get(2).text() shouldBe businessDetailsModel.addressModel.postcode.get
              }

              s"has a fourth element of ${businessDetailsModel.addressModel.country}" in {
                tdOne.select("li").get(3).text() shouldBe businessDetailsModel.addressModel.country
              }
            }
          }
        }

        "has a tertiary table row" which {

          lazy val row = table.select("tr:nth-of-type(3)")

          "has a table header" which {
            lazy val tHeader = row.select("th")

            "has an inline style of 'vertical-align: top'" in {
              tHeader.attr("style") shouldBe "vertical-align: top"
            }

            s"with a title ${messages.correspondenceAddress}" in {
              tHeader.text() shouldBe messages.correspondenceAddress
            }
          }

          "has a td" which {

            lazy val tdOne = row.select("td:nth-of-type(1) ul")

            "has four list elements" in {
              tdOne.select("li").size() shouldBe 4
            }

            s"has a first element of ${addressModel.addressLineOne}" in {
              tdOne.select("li").get(0).text() shouldBe addressModel.addressLineOne
            }

            s"has a second element of ${addressModel.addressLineTwo}" in {
              tdOne.select("li").get(1).text() shouldBe addressModel.addressLineTwo
            }

            s"has a td for ${messages.change}" which {

              lazy val tdTwo = row.select("td:nth-of-type(2)")

              "has the change link text" in {
                tdTwo.text() shouldBe messages.change
              }

              s"has the link to ${controllers.routes.CompanyController.enterCorrespondenceAddress().url}" in {
                tdTwo.select("a").attr("href") shouldBe controllers.routes.CompanyController.enterCorrespondenceAddress().url
              }
            }
          }
        }

        "have a fourth table row" which {

          lazy val row = table.select("tr:nth-of-type(4)")

          "has a table header" which {
            lazy val tHeader = row.select("th")

            "has an inline style of 'vertical-align: top'" in {
              tHeader.attr("style") shouldBe "vertical-align: top"
            }

            s"with a title ${messages.contactDetails}" in {
              tHeader.text() shouldBe messages.contactDetails
            }
          }

          "has a td for CGT contact details" which {
            lazy val tdOne = row.select("td:nth-of-type(1) ul")

            s"has a first element of ${contactDetailsModel.contactName}" in {
              tdOne.select("li").get(0).text() shouldBe contactDetailsModel.contactName
            }

            s"has a second element of ${contactDetailsModel.telephone}" in {
              tdOne.select("li").get(1).text() shouldBe contactDetailsModel.telephone
            }
          }

          s"has a td for ${messages.change}" which {
            lazy val tdTwo = row.select("td:nth-of-type(2)")

            "has the change link text" in {
              tdTwo.text() shouldBe messages.change
            }

            s"has the link to ${controllers.routes.ClientController.contactDetails().url}" in {
              tdTwo.select("a").attr("href") shouldBe controllers.routes.ClientController.contactDetails().url
            }
          }
        }

        "have a button" which {

          lazy val button = doc.select("button")

          s"has the text ${messages.register}" in {
            button.text() shouldBe messages.register
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
