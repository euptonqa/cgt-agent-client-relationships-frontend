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

import javax.inject.Inject

import checks.EnrolmentCheck
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.auth.{CompositePageVisibilityPredicate, PageVisibilityPredicate}
import uk.gov.hmrc.play.http.HeaderCarrier

class AgentVisibilityPredicate @Inject()(enrolmentCheck: EnrolmentCheck, authorisationService: AuthorisationService)
                                        (enrolmentUrl: String,
                                         agentUrl: String)(implicit val hc: HeaderCarrier) extends CompositePageVisibilityPredicate{

  override def children: Seq[PageVisibilityPredicate] = Seq (
    new AffinityGroupAgentPredicate(authorisationService)(agentUrl),
    new EnrolmentPredicate(enrolmentCheck, authorisationService)(enrolmentUrl)
  )

}
