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

import auth.AuthorisedActions
import common.Constants
import common.Constants.AffinityGroup
import connectors.{FailedGovernmentGatewayResponse, GovernmentGatewayResponse, SuccessGovernmentGatewayResponse}
import play.api.mvc.{Action, AnyContent, Result}
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.controller.FrontendController

@Singleton
class ClientController @Inject()(authorisedActions: AuthorisedActions, authorisationService: AuthorisationService) extends FrontendController {

  val clientType= TODO

  val submitClientType: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        authorisationService.getAffinityGroup(hc).map{
          case Some(AffinityGroup.Individual) => Ok(enterIndividualCorrespondenceDetails)
          case _ => NotImplemented
        }
  }

  val enterIndividualCorrespondenceDetails = TODO

  val submitIndividualCorrespondenceDetails = TODO

  val confirmation = TODO
}
