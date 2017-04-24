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

import javax.inject.{Inject, Singleton}

import config.AppConfig
import models.{AddressModel, BusinessDetailsModel, ContactDetailsModel}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class CompanyController @Inject()(appConfig: AppConfig,
                                  val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  val confirmBusinessDetails: Action[AnyContent] = Action.async { implicit request =>
    //TODO: remove this during controller task
    val address = AddressModel("address line 1", "address line 2", None, Some("address line 4"), Some("sy1 2hl"), "England")
    val contact = ContactDetailsModel("john smith", "12345 567890")
    Future.successful(Ok(views.html.company.confirmBusinessDetails(appConfig, BusinessDetailsModel("business name", address, None), address, contact)))
  }

  val submitConfirmationOfBusinessDetails = TODO

  val enterCorrespondenceAddress = TODO

  val submitCorrespondenceAddress = TODO
}
