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

import data.MessageLookup.{Common, ConfirmPermission}
import org.jsoup.Jsoup
import traits.ViewSpecHelper
import views.html.confirmPermission

class ConfirmPermissionViewSpec extends ViewSpecHelper {

  "The confirm permission view" should {
    lazy val view = confirmPermission(config)
    lazy val doc = Jsoup.parse(view.body)

    s"have a title of ${ConfirmPermission.title}" in {
      doc.title() shouldBe ConfirmPermission.title
    }

    "have a header" which {
      lazy val header = doc.select("h1")

      "has a class of heading-xlarge" in {
        header.attr("class") shouldBe "heading-xlarge"
      }

      s"has the text ${ConfirmPermission.title}" in {
        header.text() shouldBe ConfirmPermission.title
      }
    }

    "have a paragraph" which {
      lazy val paragraph = doc.select("main p").get(1)

      s"has the text s${ConfirmPermission.body}" in {
        paragraph.text() shouldBe ConfirmPermission.body
      }
    }

    "have a link" which {
      lazy val link = doc.select("main a").get(1)

      "has the class button" in {
        link.attr("class") shouldBe "button"
      }

      s"has the href ${controllers.routes.ClientController.clientType().url}" in {
        link.attr("href") shouldBe controllers.routes.ClientController.clientType().url
      }

      s"has the text ${Common.continue}" in {
        link.text() shouldBe Common.continue
      }
    }
  }
}
