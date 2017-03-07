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

package predicates

import checks.EnrolmentCheck
import common.Keys
import data.TestUsers
import models.{Enrolment, Identifier}
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.inject.Injector
import play.api.test.FakeRequest
import services.AuthorisationService
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class EnrolmentPredicateSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  val dummyUrl = "http://example.com"
  val injector: Injector = fakeApplication.injector
  lazy val cgtCheck = injector.instanceOf[EnrolmentCheck]
  implicit val hc = HeaderCarrier()

  def mockedPredicate(response: Option[Seq[Enrolment]]): EnrolmentPredicate = {
    val mockService = mock[AuthorisationService]

    when(mockService.getEnrolments(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))

    new EnrolmentPredicate(cgtCheck, mockService)(dummyUrl)
  }

  "Calling the EnrolmentPredicate" should {

    "return a false for users with no agent enrolment" in {
      val enrolments = Seq(Enrolment("Not the Agent Key", Seq(Identifier("DummyKey", "DummyValue")), ""))
      val predicate = mockedPredicate(Some(enrolments))
      val authContext = TestUsers.create200ConfidenceUserAuthContext
      val result = predicate.apply(authContext, FakeRequest())

      await(result).isVisible shouldBe false
    }

    "return true for users with an agent enrolment" in {
      val enrolments = Seq(Enrolment(Keys.EnrolmentKeys.agentEnrolmentKey, Seq(Identifier("DummyKey", "DummyValue")), ""))
      val predicate = mockedPredicate(Some(enrolments))
      val authContext = TestUsers.create200ConfidenceUserAuthContext
      val result = predicate.apply(authContext, FakeRequest())

      await(result).isVisible shouldBe true
    }
  }

}
