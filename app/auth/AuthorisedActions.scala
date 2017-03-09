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

package auth

import javax.inject.{Inject, Singleton}

import checks.EnrolmentCheck
import config.{ApplicationConfig, FrontendAuthConnector}
import play.api.mvc.{Action, AnyContent}
import predicates.VisibilityPredicate
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.auth.connectors.domain.Accounts
import uk.gov.hmrc.play.frontend.auth.{Actions, AuthContext, AuthenticationProvider, TaxRegime}
import uk.gov.hmrc.play.http.HeaderCarrier

@Singleton
class AuthorisedActions @Inject()(applicationConfig: ApplicationConfig,
                                  authorisationService: AuthorisationService,
                                  feAuthConnector: FrontendAuthConnector)(implicit val hc: HeaderCarrier) extends Actions {

  override val authConnector: FrontendAuthConnector = feAuthConnector

  private val composeAuthorisedAction: AuthenticatedAction => Action[AnyContent] = {
    val postSignInRedirectUrl = controllers.routes.AgentController.showClientList().url
    val ggProvider = new GovernmentGatewayProvider(postSignInRedirectUrl,
      applicationConfig.governmentGatewaySignIn)
    val regime = new CgtRegime {
      override def authenticationType: AuthenticationProvider = ggProvider
    }

    lazy val visibilityPredicate = new VisibilityPredicate(authorisationService)(applicationConfig.badAffinity,
      applicationConfig.noEnrolment)

    lazy val guardedAction: AuthenticatedBy = AuthorisedFor(regime, visibilityPredicate)

    val authenticatedAction: AuthenticatedAction => Action[AnyContent] = action => {
      guardedAction.async {
        authContext: AuthContext =>
          implicit request =>
          action(CgtAgent(authContext))(request)
      }
    }

    authenticatedAction
  }

  def authorisedAgentAction(action: AuthenticatedAction): Action[AnyContent] = composeAuthorisedAction(action)


  trait CgtRegime extends TaxRegime {
    override def isAuthorised(accounts: Accounts): Boolean = true

    override def authenticationType: AuthenticationProvider

    override def unauthorisedLandingPage: Option[String] = Some(applicationConfig.notAuthorisedRedirect)
  }

}
