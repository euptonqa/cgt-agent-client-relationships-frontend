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

import javax.inject.Inject

import config.{AppConfig, WSHttp}
import models.RelationshipModel
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.play.http.{HeaderCarrier, HttpResponse}
import play.api.http.Status._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RelationshipConnectorResponse
case object SuccessfulRelationshipResponse extends RelationshipConnectorResponse
case object FailedRelationshipResponse extends RelationshipConnectorResponse

class RelationshipConnector @Inject()(appConfig: AppConfig, http: WSHttp) {

  lazy val serviceUrl: String = appConfig.agentRelationship
  val createRelationship: String = "/relationship"

  def createClientRelationship(relationshipModel: RelationshipModel)(implicit hc: HeaderCarrier): Future[RelationshipConnectorResponse] = {
    val postUrl = s"""$serviceUrl/$createRelationship"""
    http.POST[JsValue, HttpResponse](postUrl, Json.toJson(relationshipModel)).map {
      response =>
        response.status match {
          case NO_CONTENT => Logger.info(s"Successful agent client relationship creation" +
            s"with arn reference: ${relationshipModel.arn}")
            SuccessfulRelationshipResponse
          case _ => Logger.warn(s"Failed agent client relationship creation" +
            s"with arn reference: ${relationshipModel.arn}")
            FailedRelationshipResponse
        }
    }
  }
}
