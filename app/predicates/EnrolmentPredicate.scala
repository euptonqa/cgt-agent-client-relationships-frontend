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

import play.api.mvc.Results._
import checks.EnrolmentCheck
import play.api.mvc.{AnyContent, Request}
import services.AuthorisationService
import uk.gov.hmrc.play.frontend.auth._
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class EnrolmentPredicate @Inject()(authService: AuthorisationService)
                                  (enrolmentURI: String)(implicit hc: HeaderCarrier) extends PageVisibilityPredicate {

  override def apply(authContext: AuthContext, request: Request[AnyContent]): Future[PageVisibilityResult] = {
    for {
      enrolments <- authService.getEnrolments
      isEnrolled <- EnrolmentCheck.checkEnrolments(enrolments)
    } yield {
      if(isEnrolled) {
        PageIsVisible
      } else {
        PageBlocked(needsEnrolment)
      }
    }
  }

  private val needsEnrolment = Future.successful(Redirect(enrolmentURI.toString))
}
