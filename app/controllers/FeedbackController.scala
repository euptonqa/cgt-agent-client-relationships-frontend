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

package controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Request, RequestHeader}
import play.mvc.BodyParser.AnyContent
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.play.frontend.filters.SessionCookieCryptoFilter
import uk.gov.hmrc.play.http.HttpGet
import uk.gov.hmrc.play.http.ws.WSHttp
import uk.gov.hmrc.play.partials.{CachedStaticHtmlPartialRetriever, FormPartialRetriever, HtmlPartial, PartialRetriever}

class FeedbackController @Inject()(val wsHttp: WSHttp, val messagesApi: MessagesApi)
  extends FrontendController with PartialRetriever with I18nSupport{

  override val httpGet = wsHttp
  val httpPost = wsHttp

  private val TICKET_ID = "ticketId"

  implicit val cachedStaticHtmlPartialRetriever: CachedStaticHtmlPartialRetriever = new CachedStaticHtmlPartialRetriever {
    override def httpGet: HttpGet = wsHttp
  }

  protected def loadPartial(url: String)(implicit request: RequestHeader): HtmlPartial = ???

  private def formPartialRetriever(implicit request:Request[AnyContent]): FormPartialRetriever = new FormPartialRetriever {
    override def httpGet: HttpGet = wsHttp
    override def crypto: (String) => String = cookie => SessionCookieCryptoFilter.encrypt(cookie)
  }

  def contactFormReferrer(implicit request: Request[AnyContent]): String = request.headers.get(REFERER).getOrElse("")

  def localSubmitUrl(implicit request: Request[AnyContent]): String = routes.FeedbackController.submit().url
  //TODO: Update!


}
