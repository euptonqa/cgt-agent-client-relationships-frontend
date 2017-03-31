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
import config.{AppConfig, Keys}
import connectors.{FailedGovernmentGatewayResponse, GovernmentGatewayResponse, KeystoreConnector, SuccessGovernmentGatewayResponse}
import forms.SelectedClientForm
import models.{CallbackUrlModel, SelectedClient}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.AgentService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

@Singleton
class AgentController @Inject()(authorisedActions: AuthorisedActions,
                                agentService: AgentService,
                                appConfig: AppConfig,
                                val messagesApi: MessagesApi,
                                selectedClientForm: SelectedClientForm,
                                sessionService: KeystoreConnector) extends FrontendController with I18nSupport {

  val showClientList: String => Action[AnyContent] = callbackUrl => authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        def handleGGResponse(response: GovernmentGatewayResponse): Result = {
          response match {
            case SuccessGovernmentGatewayResponse(clients) =>
              if (clients.nonEmpty)
                Ok(views.html.clientList(appConfig, clients))
              else Redirect(controllers.routes.AgentController.makeDeclaration())
            case FailedGovernmentGatewayResponse => throw new Exception("Failed to retrieve client")
          }
        }

        Try (CallbackUrlModel(callbackUrl)) match {
          case Success(value) => sessionService.saveFormData[CallbackUrlModel](Keys.KeystoreKeys.callbackUrl, value)
            agentService.getExistingClients(user.authContext).map{x => handleGGResponse(x)}
          case Failure(_) => Future.successful(BadRequest(views.html.error_template(Messages("errors.badRequest"),
            Messages("errors.badRequest"), Messages("errors.checkAddress"), appConfig)))
        }
  }

  val selectClient: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>

        def errorAction(form: Form[SelectedClient]) = throw new Exception

        def successAction(model: SelectedClient): Future[Result] = {
          Future.successful(Redirect(appConfig.iFormUrl))
        }

        selectedClientForm.selectedClientForm.bindFromRequest.fold(errorAction, successAction)
  }

  val makeDeclaration: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        Future.successful(Ok(views.html.confirmPermission(appConfig)))
  }
}
