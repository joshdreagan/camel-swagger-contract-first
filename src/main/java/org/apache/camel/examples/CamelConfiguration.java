/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.examples;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CamelConfiguration extends RouteBuilder {

  private static final Logger log = LoggerFactory.getLogger(CamelConfiguration.class);
  
  @Autowired
  private ApiProperties apiProperties;
  
  @Override
  public void configure() throws Exception {

    fromF("cxfrs:%s?bindingStyle=SimpleConsumer&resourceClasses=org.apache.camel.examples.api.PetApi,org.apache.camel.examples.api.StoreApi,org.apache.camel.examples.api.UserApi&features=#cxfFeatures&providers=#cxfProviders", apiProperties.getBasePath())
      .log(LoggingLevel.INFO, log, "Swagger Petstore API invoked. Please add implementation...")
    ;
  }
}
