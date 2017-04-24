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

import javax.inject.Inject

import auth.AuthorisedActions
import common.CountryList
import config.AppConfig
import connectors.KeystoreConnector
import forms.CorrespondenceAddressForm
import models.AddressModel
import play.api.data.Form
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

class CompanyController @Inject()(appConfig: AppConfig,
                                  authorisedActions: AuthorisedActions,
                                  correspondenceAddressForm: CorrespondenceAddressForm,
                                  countryList: CountryList,
                                  sessionService: KeystoreConnector) extends FrontendController {

  val enterCorrespondenceAddress: Action[AnyContent] = authorisedActions.authorisedAgentAction() {
    implicit user =>
      implicit request =>
      Future.successful(Ok(views.html.company.correspondenceAddress(appConfig,
        correspondenceAddressForm.correspondenceAddressForm, countryList.getListOfCountries)))
  }
  val submitCorrespondenceAddress: Action[AnyContent] = authorisedActions.authorisedAgentAction() {
    implicit user =>
      implicit request =>

      def errorAction(form: Form[AddressModel]) = {
        Future.successful(BadRequest(views.html.company.correspondenceAddress(appConfig,
          form, countryList.getListOfCountries)))
      }

      def successAction(model: AddressModel): Future[Result] = {
        sessionService.saveFormData("", model)
        Future.successful(Ok("google.com"))
        //stubbed until page implemented
      }

      correspondenceAddressForm.correspondenceAddressForm.bindFromRequest.fold(errorAction, successAction)
  }

}
