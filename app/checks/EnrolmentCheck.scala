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

package checks

import javax.inject.Inject

import common.Keys
import models.Enrolment

import scala.concurrent.Future

class EnrolmentCheck @Inject()() {
  def checkEnrolments(enrolments: Option[Seq[Enrolment]]): Future[Boolean] = enrolments match {
    case Some(data) => Future.successful(data.exists(_.key == Keys.EnrolmentKeys.cgtAgentEnrolmentKey))
    case None => Future.successful(false)
  }
}
