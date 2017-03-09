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
import common.Constants.AffinityGroup._
import common.Keys
import data.TestUsers
import models.{AuthorisationDataModel, Enrolment, Identifier}
import services.AuthorisationService
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import play.api.inject.Injector
import play.api.test.FakeRequest
import uk.gov.hmrc.play.frontend.auth.connectors.domain.{Accounts, ConfidenceLevel, CredentialStrength, PayeAccount}
import uk.gov.hmrc.play.http.HeaderCarrier
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

import scala.concurrent.Future

class VisibilityPredicateSpec extends UnitSpec with WithFakeApplication with MockitoSugar {
  def mockedService(authorisationDataModel: Option[AuthorisationDataModel],
                    enrolments: Option[Seq[Enrolment]],
                    enrolmentUri: String = "http://enrolments-uri.com",
                    affinityGroup: String): AuthorisationService = {

    val mockService = mock[AuthorisationService]

    when(mockService.getAffinityGroup(ArgumentMatchers.any()))
      .thenReturn(Future.successful(Some(affinityGroup)))

    when(mockService.getEnrolments(ArgumentMatchers.any()))
      .thenReturn(Future.successful(enrolments))

    mockService
  }

  implicit val hc = HeaderCarrier()

  val injector: Injector = fakeApplication.injector
  val enrolmentsCheck = injector.instanceOf[EnrolmentCheck]

  def predicate(dataModel: Option[AuthorisationDataModel], enrolments: Option[Seq[Enrolment]], affinityGroup: String="Agent"): VisibilityPredicate = {
    new VisibilityPredicate(enrolmentsCheck, mockedService(dataModel, enrolments, affinityGroup=affinityGroup))("example.com","exampletwo.com")}

  "return true for page visibility when the component predicates' conditions are met" in {
    val authContext = TestUsers.create500ConfidenceUserAuthContext
    val enrolments = Seq(Enrolment(Keys.EnrolmentKeys.agentEnrolmentKey, Seq(Identifier("DummyKey", "DummyValue")), ""))
    val authorisationDataModelPass = AuthorisationDataModel(CredentialStrength.Strong, Agent, ConfidenceLevel.L200, "", Accounts())

    lazy val result = predicate(Some(authorisationDataModelPass), Some(enrolments))(authContext, FakeRequest())

    lazy val pageVisibility = await(result)

    pageVisibility.isVisible shouldBe true
  }

  "return false for page visibility when the component predicates' conditions are not met" in {
    val authContext = TestUsers.create500ConfidenceUserAuthContext
    val enrolments = Seq(Enrolment("Not agent enrolment key", Seq(Identifier("DummyKey", "DummyValue")), ""))
    val authorisationDataModelFail = AuthorisationDataModel(CredentialStrength.Strong, Individual, ConfidenceLevel.L200, "", Accounts())

    lazy val result = predicate(Some(authorisationDataModelFail), Some(enrolments), affinityGroup=Individual)(authContext, FakeRequest())

    lazy val pageVisibility = await(result)

    pageVisibility.isVisible shouldBe false
  }
}
