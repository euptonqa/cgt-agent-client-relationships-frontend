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
import config.AppConfig
import models.UserFactsModel
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class ClientController @Inject()(authorisedActions: AuthorisedActions,
                                 clientService: ClientService,
                                 appConfig: AppConfig,
                                 val messages: MessagesApi) extends FrontendController with I18nSupport {

  val clientType = TODO

  val submitClientType = TODO

  val enterIndividualCorrespondenceDetails = TODO

  val submitIndividualCorrespondenceDetails: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>

        def handleRealtionshipResponse(response: T) = {

        }

        def successAction(model: UserFactsModel): Future[Result] = {
          for {
            cgtRef <- clientService.subscribeIndividualClient(model)
            relationshipResponse <- relationshipService.createRelationship(cgtRef)
          }



          Future.successful(Redirect(routes.ContactDetailsController.contactDetails()))
        }


        correspondenceAddressForm.correspondenceAddressForm.bindFromRequest.fold(errors =>
          Future.successful(BadRequest(views.html.address.enterCorrespondenceAddress(appConfig, errors))),
          successAction)

  }

  val confirmation = TODO
}
