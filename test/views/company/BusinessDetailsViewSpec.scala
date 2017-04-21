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

import forms.BusinessDetailsForm
import org.jsoup.Jsoup
import traits.ViewSpecHelper
import data.MessageLookup._

class BusinessDetailsViewSpec extends ViewSpecHelper {

  lazy val form = app.injector.instanceOf[BusinessDetailsForm]

  "The BusinessDetails view" when {
    "no errors are shown" should {
      lazy val view = views.html.company.businessDetails(config, form.businessDetailsForm)
      lazy val doc = Jsoup.parse(view.body)

      doc.title shouldBe BusinessDetails.title
    }


  }

}
