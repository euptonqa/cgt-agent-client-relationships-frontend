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
import connectors.{FailedGovernmentGatewayResponse, GovernmentGatewayResponse, SuccessGovernmentGatewayResponse}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Result}
import services.AgentService
import uk.gov.hmrc.play.frontend.controller.FrontendController

import scala.concurrent.Future

@Singleton
class AgentController @Inject()(authorisedActions: AuthorisedActions,
                                agentService: AgentService,
                                appConfig: AppConfig,
                                val messagesApi: MessagesApi) extends FrontendController with I18nSupport {

  val showClientList: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        def handleGGResponse(response: GovernmentGatewayResponse): Result = {
          response match {
            case SuccessGovernmentGatewayResponse(clients) => {
              if (clients.size > 0)
                Ok(views.html.clientList(appConfig, clients))
              else Ok(views.html.confirmPermission(appConfig))
            }
            case FailedGovernmentGatewayResponse => InternalServerError
          }
        }

        for {
          clients <- agentService.getExistingClients(user.authContext)
        } yield handleGGResponse(clients)
  }

  val selectClient = TODO

  val makeDeclaration: Action[AnyContent] = authorisedActions.authorisedAgentAction {
    implicit user =>
      implicit request =>
        Future.successful(Ok(views.html.confirmPermission(appConfig)))
  }
}
