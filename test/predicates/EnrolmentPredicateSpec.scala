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
import models.Enrolment
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import play.api.inject.Injector
import services.AuthorisationService
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class EnrolmentPredicateSpec extends UnitSpec with WithFakeApplication with MockitoSugar {

  val dummyUrl = "http://example.com"
  val injector: Injector = fakeApplication.injector
  lazy val cgtCheck = injector.instanceOf[EnrolmentCheck]

  def mockedPredicate(response: Option[Seq[Enrolment]], enrolmentCheck: Boolean): EnrolmentPredicate = {
    val mockService = mock[AuthorisationService]

    val mockEnrolmentCheck = mock[EnrolmentCheck]

    when(mockService.getEnrolments(ArgumentMatchers.any()))
      .thenReturn(Future.successful(response))

    new EnrolmentPredicate(cgtCheck, mockService)
  }

  "Calling the EnrolmentPredicate"

}
