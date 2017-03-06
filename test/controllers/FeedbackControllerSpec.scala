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

package test.controllers

import controllers.routes
import assets.ControllerSpecHelper
import config.WSHttp
import controllers.FeedbackController
import org.mockito.ArgumentMatchers
import org.mockito.Mockito._
import play.api.http.Status
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.http.{HttpGet, HttpResponse}
import uk.gov.hmrc.play.partials.{CachedStaticHtmlPartialRetriever, FormPartialRetriever}

import scala.concurrent.Future

class FeedbackControllerSpec extends ControllerSpecHelper {

  val mockHttp = mock[WSHttp]

  def setupController(): FeedbackController = {
    lazy val controller = new FeedbackController()(mockConfig, messagesApi) {
      override implicit val cachedStaticHtmlPartialRetriever: CachedStaticHtmlPartialRetriever = new CachedStaticHtmlPartialRetriever {
        override def httpGet: HttpGet = ???

        override def getPartialContent(url: String, templateParameters: Map[String, String], errorMessage: Html)(implicit request: RequestHeader): Html =
          Html("")
      }
      override implicit val formPartialRetriever: FormPartialRetriever = new FormPartialRetriever {
        override def crypto: (String) => String = ???

        override def httpGet: HttpGet = ???

        override def getPartialContent(url: String,
                                       templateParameters: Map[String, String],
                                       errorMessage: Html)(implicit request: RequestHeader): Html = Html("")
      }
    }
    controller
  }

  "GET /feedback" should {
    lazy val target = setupController()
    lazy val fakeRequest = FakeRequest("GET", "/")

    "return feedback page" in {
      lazy val result = target.show(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "capture the referrer in the session on initial session on the feedback load" in {
      lazy val result = target.show(fakeRequest.withHeaders("Referer" -> "Blah"))
      status(result) shouldBe Status.OK
    }
  }

  "POST /feedback" should {
    lazy val target = setupController()
    lazy val fakeRequest = FakeRequest("GET", "/")
    lazy val fakePostRequest = FakeRequest("POST", "/capital-gains-tax/subscription/agent/feedback").withFormUrlEncodedBody("test" -> "test")
    "return form with thank you for valid selections" in {
      when(mockHttp.POSTForm[HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
        Future.successful(HttpResponse(Status.OK, responseString = Some("1234"))))

      lazy val result = target.submit(fakePostRequest)
      redirectLocation(result) shouldBe Some(routes.FeedbackController.thankyou().url)
    }

    "return form with errors for invalid selections" in {
      when(mockHttp.POSTForm[HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
        Future.successful(HttpResponse(Status.BAD_REQUEST, responseString = Some("<p>:^(</p>"))))
      lazy val result = target.submit(fakePostRequest)
      status(result) shouldBe Status.BAD_REQUEST
    }

    "return error for other http code back from contact-frontend" in {
      when(mockHttp.POSTForm[HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
        Future.successful(HttpResponse(418))) // 418 - I'm a teapot
      lazy val result = target.submit(fakePostRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }

    "return internal server error when there is an empty form" in {
      when(mockHttp.POSTForm[HttpResponse](ArgumentMatchers.any(), ArgumentMatchers.any())(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(
        Future.successful(HttpResponse(Status.OK, responseString = Some("1234"))))

      lazy val result = target.submit(fakeRequest)
      status(result) shouldBe Status.INTERNAL_SERVER_ERROR
    }
  }

  "GET /feedback/thankyou" should {
    "should return the thank you page" in {
      lazy val target = setupController()
      lazy val fakeRequest = FakeRequest("GET", "/")
      lazy val result = target.thankyou(fakeRequest)
      status(result) shouldBe Status.OK
    }
  }
}