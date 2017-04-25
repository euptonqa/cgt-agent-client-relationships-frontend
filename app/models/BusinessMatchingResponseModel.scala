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

package models

import play.api.libs.json.{Json, OFormat}

case class BusinessMatchingResponseModel(sapNumber: String,
                                         safeId: String,
                                         organisation: OrganisationModel,
                                         address: AddressModel,
                                         contactDetails: ContactDetailsModel)

object BusinessMatchingResponseModel {
  implicit val formats: OFormat[BusinessMatchingResponseModel] = Json.format[BusinessMatchingResponseModel]
}

case class OrganisationModel(organisationName: String, isAGroup: Option[Boolean], organisationType: Option[String])

object OrganisationModel {
  implicit val formats: OFormat[OrganisationModel] = Json.format[OrganisationModel]
}
