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

package connectors

import javax.inject.{Inject, Singleton}

import config.{ApplicationConfig, WSHttp}
import models.{SubscriptionReference, CorrespondenceDetailsModel}
import play.api.Logger
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SubscriptionConnector @Inject()(http: WSHttp, applicationConfig: ApplicationConfig) {

  lazy val serviceUrl: String = applicationConfig.subscriptionServiceUrl

  def subscribeIndividualClient(correspondenceDetailsModel: CorrespondenceDetailsModel)(implicit hc: HeaderCarrier): Future[SubscriptionReference] = {
    val postUrl = s"$serviceUrl/capital-gains-subscription/subscribe/agent/individual"

    http.POST[JsValue, HttpResponse](postUrl, Json.toJson(correspondenceDetailsModel)).map {
      response => response.status match {
        case OK => response.json.as[SubscriptionReference]
        case _ =>
          Logger.warn("Invalid response from Subscription Service")
          throw new Exception("Invalid response from Subscription Service")
      }
    }
  }
}
