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

package common

import java.util.Properties
import javax.inject.Inject

import play.api.Environment

import scala.collection.JavaConverters
import scala.io.Source

class CountriesMatcher @Inject()(environment: Environment) {
  lazy val p = new Properties
  p.load(Source.fromInputStream(environment.classLoader.getResourceAsStream("country-code.properties"), "UTF-8").bufferedReader())

  def getIsoCodeTupleList: List[(String, String)] = {
    JavaConverters.propertiesAsScalaMapConverter(p).asScala.toList.sortBy(_._2)
  }

  def getSelectedCountry(isoCode: String): String = {
    def trimCountry(selectedCountry: String) = {
      val position = selectedCountry.indexOf(":")
      if (position > 0) {
        selectedCountry.substring(0, position).trim
      } else {
        selectedCountry
      }
    }

    def getCountry(isoCode: String): Option[String] = {
      val country = Option(p.getProperty(isoCode.toUpperCase))
      country.map{ selectedCountry =>
        trimCountry(selectedCountry)
      }
    }

    getCountry(isoCode.toUpperCase).fold(isoCode){x=>x}
  }
}
