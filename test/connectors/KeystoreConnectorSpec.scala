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

import java.util.UUID

import org.mockito.Mockito._
import config.{AgentClientSessionCache, WSHttp}
import org.mockito.ArgumentMatchers
import org.scalatest.mock.MockitoSugar
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.json.Json
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.http.logging.SessionId
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class KeystoreConnectorSpec extends UnitSpec with MockitoSugar with OneAppPerSuite {

  lazy val sessionId: String = UUID.randomUUID.toString
  lazy val http: WSHttp = mock[WSHttp]
  lazy val agentClientSessionCache: AgentClientSessionCache = mock[AgentClientSessionCache]

  lazy implicit val hc: HeaderCarrier = HeaderCarrier(sessionId = Some(SessionId(sessionId.toString)))

  lazy val target = new KeystoreConnector(agentClientSessionCache)

  "KeystoreConnector .fetchFormData" should {

    val testData = Some("hello")

    when(agentClientSessionCache.fetchAndGetEntry[String](ArgumentMatchers.eq("String"))(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(testData))

    "should be able to retrieve a String" in {
      lazy val result = target.fetchAndGetFormData[String]("String")
      await(result) shouldBe Some("hello")
    }
  }

  "KeystoreConnector .saveFormData" should {
    val testData = "hello"
    val returnedCacheMap = CacheMap("key", Map("data" -> Json.toJson(testData)))

    when(agentClientSessionCache.cache[String](ArgumentMatchers.anyString(), ArgumentMatchers.anyString())(ArgumentMatchers.any(), ArgumentMatchers.any()))
      .thenReturn(Future.successful(returnedCacheMap))

    "save data to keystore" in {
      lazy val result = target.saveFormData[String]("key", testData)

      await(result) shouldBe returnedCacheMap
    }
  }
}
