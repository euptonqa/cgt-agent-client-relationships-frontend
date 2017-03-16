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

package data

object MessageLookup {

  object Common {
    val continue = "Continue"
  }

  object ConfirmPermission {
    val title = "Confirm you have permission"
    val body = "By continuing, you confirm that you have permission to act on your client's behalf."
  }

  object CorrespondenceDetails {
    val title = "Your client's correspondence details"
    val firstName = "First name"
    val lastName = "Last name"
    val contactAddress = "Contact address"
    val buildingAndStreet = "Building and street"
    val building = "Building"
    val street = "street"
    val town = "Town or city"
    val county = "County"
    val country = "Country"
    val postcode = "Postcode"
  }

  object ClientList {
    val title = "Client overview"
    val clientName = "Client name"
    val addClient = "Add a new client"
    val report = "Report"
  }

  object Errors {
    val errorRequired = "This field is required"
  }
}
