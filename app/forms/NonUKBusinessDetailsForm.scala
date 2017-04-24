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

package forms

import javax.inject.Inject

import common.FormValidation._
import models.{AddressModel, BusinessDetailsModel, OverseasTaxModel}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}

class NonUKBusinessDetailsForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport {

  val validation: BusinessDetailsModel => Boolean = model =>
    (model.overseasTaxReference == "Yes" &&
      model.overseasTaxModel.nonEmpty && (model.overseasTaxModel.get.countryOfIssue != "" &&
      model.overseasTaxModel.get.institutionOfIssue != "" &&
      model.overseasTaxModel.get.taxReference != "")) || model.overseasTaxReference == "No"

  val nonUKBusinessDetailsForm: Form[BusinessDetailsModel] = Form(
    mapping(
      "businessName" -> text
        .verifying(Messages("errors.required"), nonEmptyCheck),
      "addressModel" -> mapping(
        "addressLineOne" -> text
          .verifying(Messages("errors.required"), nonEmptyCheck),
        "addressLineTwo" -> text
          .verifying(Messages("errors.required"), nonEmptyCheck),
        "addressLineThree" -> text
          .transform(textToOptional, optionalToText),
        "addressLineFour" -> text
          .transform(textToOptional, optionalToText),
        "postcode" -> text
          .transform(textToOptional, optionalToText),
        "county" -> text
          .verifying(Messages("errors.required"), nonEmptyCheck)
      )(AddressModel.apply)(AddressModel.unapply),
      "overseasTaxReference" -> text
        .verifying(Messages("errors.required"), nonEmptyCheck)
        .verifying(yesNoCheck),
      "overseasTaxModel" -> mapping(
        "taxReference" -> text
          .verifying(Messages("errors.required"), nonEmptyCheck),
        "countryOfIssue" -> text
          .verifying(Messages("errors.required"), nonEmptyCheck),
        "institutionOfIssue" -> text
          .verifying(Messages("errors.required"), nonEmptyCheck)
      )(OverseasTaxModel.apply)(OverseasTaxModel.unapply)
        .transform(modelToOptional, optionalToModel)
    )(BusinessDetailsModel.apply)(BusinessDetailsModel.unapply)
      .verifying("please supply overseas tax details", validation)
  )
}
