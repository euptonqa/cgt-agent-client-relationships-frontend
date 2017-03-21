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
import common.Constants.{ClientType => CTConstants}
import config.AppConfig
import connectors.{RelationshipConnectorResponse, SuccessfulRelationshipResponse}
import forms.{ClientTypeForm, CorrespondenceDetailsForm}
import models._
import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.{ClientService, RelationshipService}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import views.html.{clientType => clientTypeView}

import scala.concurrent.Future

@Singleton
class ClientController @Inject()(appConfig: AppConfig,
                                 authorisedActions: AuthorisedActions,
                                 clientService: ClientService,
                                 relationshipService: RelationshipService,
                                 clientTypeForm: ClientTypeForm,
                                 correspondenceDetailsForm: CorrespondenceDetailsForm,
                                 val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  lazy val form: Form[ClientTypeModel] = clientTypeForm.clientTypeForm

  val clientType: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        Future.successful(Ok(views.html.clientType(appConfig, clientTypeForm.clientTypeForm)))
  }

  val submitClientType: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        def errorAction(form: Form[ClientTypeModel]) = {
          Future.successful(BadRequest(clientTypeView(appConfig, form)))
        }

        def successAction(model: ClientTypeModel): Future[Result] = {
          model.clientType match {
            case CTConstants.individual => Future.successful(Redirect(routes.ClientController.enterIndividualCorrespondenceDetails().url))
            case CTConstants.company => Future.successful(NotImplemented)
          }
        }

        form.bindFromRequest.fold(errorAction, successAction)
  }

  val enterIndividualCorrespondenceDetails: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        Future.successful(Ok(views.html.individual.correspondenceDetails(appConfig, correspondenceDetailsForm.correspondenceDetailsForm)))
  }

  val submitIndividualCorrespondenceDetails: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>

        val arn = user.authContext.principal.accounts.agent.get.agentCode.value

        def handleRelationshipResponse(clientSubscriptionResponse: RelationshipConnectorResponse, cgtRef: String): Future[Result] =
          (arn, clientSubscriptionResponse) match {
            case (_: String, SuccessfulRelationshipResponse) => Future.successful(Redirect(routes.ClientController.confirmation(cgtRef)))
            case (_, _) => Future.successful(InternalServerError)
          }

        def successAction(model: CorrespondenceDetailsModel): Future[Result] = {
          for {
            subscription <- clientService.subscribeIndividualClient(model)
            relationshipResponse <- relationshipService.createClientRelationship(RelationshipModel(arn, subscription.cgtRef))
            response <- handleRelationshipResponse(relationshipResponse, subscription.cgtRef)
          } yield response
        }

        correspondenceDetailsForm.correspondenceDetailsForm.bindFromRequest.fold(errors =>
          Future.successful(BadRequest(views.html.individual.correspondenceDetails(appConfig, errors))),
          successAction).recoverWith {
          case error =>
            Logger.warn(s"Failed to submit the correspondence address sue to ${error.getMessage}")
            Future.successful(InternalServerError)
        }

  }

  val confirmation: String => Action[AnyContent] = cgtReference => authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        Future.successful(Ok(views.html.clientConfirmation(appConfig, cgtReference)))
  }
}
