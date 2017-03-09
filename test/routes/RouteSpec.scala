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

package routes

import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}

class RouteSpec extends UnitSpec with WithFakeApplication {

  "The URL for the AgentController .showClientList action" should {
    "be equal to /capital-gains-tax/agent/client" in {
      val path = controllers.routes.AgentController.showClientList().url
      path shouldEqual "/capital-gains-tax/agent/client"
    }
  }

  "The URL for the AgentController .selectClient action" should {
    "be equal to /capital-gains-tax/agent/client" in {
      val path = controllers.routes.AgentController.selectClient().url
      path shouldEqual "/capital-gains-tax/agent/client"
    }
  }

  "The URL for the AgentController .makeDeclaration action" should {
    "be equal to /capital-gains-tax/agent/declaration" in {
      val path = controllers.routes.AgentController.makeDeclaration().url
      path shouldEqual "/capital-gains-tax/agent/declaration"
    }
  }

  "The URL for the ClientController .clientType action" should {
    "be equal to /capital-gains-tax/agent/client-type" in {
      val path = controllers.routes.ClientController.clientType().url
      path shouldEqual "/capital-gains-tax/agent/client-type"
    }
  }

  "The URL for the ClientController .submitClientType action" should {
    "be equal to /capital-gains-tax/agent/client-type" in {
      val path = controllers.routes.ClientController.submitClientType().url
      path shouldEqual "/capital-gains-tax/agent/client-type"
    }
  }

  "The URL for the ClientController .enterIndividualCorrespondenceDetails action" should {
    "be equal to /capital-gains-tax/agent/individual/correspondence-details" in {
      val path = controllers.routes.ClientController.enterIndividualCorrespondenceDetails().url
      path shouldEqual "/capital-gains-tax/agent/individual/correspondence-details"
    }
  }

  "The URL for the ClientController .submitIndividualCorrespondenceDetails action" should {
    "be equal to /capital-gains-tax/agent/individual/correspondence-details" in {
      val path = controllers.routes.ClientController.submitIndividualCorrespondenceDetails().url
      path shouldEqual "/capital-gains-tax/agent/individual/correspondence-details"
    }
  }

  "The URL for the ClientController .confirmation action" should {
    "be equal to /capital-gains-tax/agent/confirmation" in {
      val path = controllers.routes.ClientController.confirmation().url
      path shouldEqual "/capital-gains-tax/agent/confirmation"
    }
  }
}
