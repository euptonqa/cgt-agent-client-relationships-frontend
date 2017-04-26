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

package controllers

import auth.AuthorisedActions
import common.CountryList
import config.WSHttp
import data.TestUsers
import forms.CorrespondenceAddressForm
import org.scalatest.BeforeAndAfter
import org.scalatestplus.play.OneAppPerSuite
import traits.ControllerSpecHelper
import uk.gov.hmrc.play.frontend.auth.AuthContext
import uk.gov.hmrc.play.test.UnitSpec

class CompanyControllerSpec extends ControllerSpecHelper with BeforeAndAfter {

  val unauthorisedLoginUrl = "some-url"
  lazy val correspondenceAddressForm: CorrespondenceAddressForm = app.injector.instanceOf[CorrespondenceAddressForm]
  lazy val mockWSHttp: WSHttp = mock[WSHttp]
  lazy val countryList: CountryList = app.injector.instanceOf[CountryList]

  before {
    reset(mockWSHttp)
  }

  def setupController(validAuthentication: Boolean = true, authContext: AuthContext =
  TestUsers.strongUserAuthContext): CompanyController = {
    val mockActions = mock[AuthorisedActions]
    
  }

  "Calling .enterCorrespondenceAddress" when {
    "an authorised user made the request" should {

    }
  }
}
