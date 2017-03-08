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

package config

import play.api.Configuration
import javax.inject.{Inject, Singleton}

import uk.gov.hmrc.play.config.ServicesConfig

trait AppConfig {
  val analyticsToken: String
  val analyticsHost: String
  val reportAProblemPartialUrl: String
  val reportAProblemNonJSUrl: String
  val contactFormServiceIdentifier: String
  val contactFrontendService: String
  val agentPostSignIn: String
  val governmentGatewaySignIn: String
  val notAuthorisedRedirect: String
  val badAffinity: String
  val noEnrolment: String
}

@Singleton
class ApplicationConfig @Inject()(configuration: Configuration) extends AppConfig with ServicesConfig {

  //This loads only from the root of the application config file.
  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

  //Contact Frontend Config
  override lazy val contactFrontendService: String = baseUrl("contact-frontend")
  override lazy val contactFormServiceIdentifier = "CGT-Agent-Client"
  override lazy val reportAProblemPartialUrl = s"$contactFrontendService/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  override lazy val reportAProblemNonJSUrl = s"$contactFrontendService/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"

  //GA Config
  override lazy val analyticsToken: String = loadConfig(s"google-analytics.token")
  override lazy val analyticsHost: String = loadConfig(s"google-analytics.host")

  override lazy val agentPostSignIn: String = configuration.getString(s"agent-sign-in.url").getOrElse("")
  override lazy val governmentGatewaySignIn: String = configuration.getString(s"government-gateway-sign-in.host").getOrElse("")
  override lazy val notAuthorisedRedirect: String = configuration.getString(s"not-authorised-callback.url").getOrElse("")
  override lazy val badAffinity: String = configuration.getString(s"agent-bad-affinity.url").getOrElse("")
  override lazy val noEnrolment: String = configuration.getString(s"no-enrolment.url").getOrElse("")
}
