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
    val badRequest = "Bad request"
  }

  object ErrorSummary {
    val errorSummaryHeading = "This page has errors"
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

  object ClientType {
    val title = "Is your client a company or an individual?"
    val error = "Select your client type."
    val company = "Company"
    val individual = "Individual"
  }

  object BusinessType {
    val title = "Select your client's business type"
    val error = "Select your client's business type."
    val nonUK = "Overseas company without a UK Unique Tax Reference"
    val limitedCompany = "Limited company"
  }

  object BusinessDetails {
    val title = "Enter your client's details"
    val subHeading = "We will attempt to match your details against information we currently hold"
    val registeredName = "Registered company name"
    val registeredNameHint = "This is the registered name on your incorporation certificate"
    val utr = "Corporation Tax Unique Tax Reference (UTR)"
    val utrHelp = "Example 1234567890"
    val utrLink = "Where to find your UTR"
    val utrHelpOne = "A UTR is issued by HM Revenue & Customs (HMRC) when you have registered as a business."
    val utrHelpTwo = "It is 10 numbers, for example 12345 67890, and can be found on documentation issued by HMRC. It may be printed next to the headings 'Tax Reference', 'UTR' or 'Official Use'."
    val utrHelpThree = "You can find your UTR reference in the header of any of the letter that you received from HMRC."
  }

  object ClientConfirmation {
    val title = "Capital Gains Tax Service registration complete"
    val reference = "Your client's Capital Gains Tax reference:"
    val whatNext = "What happens next?"
    val guidance = "{COPY TO BE CONFIRMED}"
  }

  object ReviewClientsDetails {
    val title = "Confirm your details"
    val registeredAddress = "Registered address"
    val correspondenceAddress = "Correspondence address"
    val notTheRightBusinessLink = "Not the right business?"
    val confirmContinue = "Confirm and continue"
  }

  object Errors {
    val errorRequired = "This field is required"
    val errorTelephone = "Enter a valid telephone number"
  }

  object ContactDetails {
    val title = "Your client's Capital Gains Tax contact details"
    val text = "The person responsible for Capital Gains Tax related queries."
    val contactName = "Contact name"
    val telephone = "Telephone"
  }
}
