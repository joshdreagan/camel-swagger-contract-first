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

import org.apache.camel.Exchange;
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
      .description("petStoreApi", "Swagger Pet Store API", null)
      .routingSlip(simple("direct:${headers[operationName]}"))
    ;
    
    from("direct:getPetById")
      .log(LoggingLevel.INFO, log, "Getting pet by ID: [${headers[petId]}]")
      .to("sql:select * from PET, CATEGORY, PET_TAG_ASS, TAG, PET_PHOTO_ASS, PHOTO where PET.ID=:#petId and PET.CATEGORY_ID=CATEGORY.ID and PET.ID=PET_TAG_ASS.PET_ID and PET_TAG_ASS.TAG_ID=TAG.ID and PET.ID=PET_PHOTO_ASS.PET_ID and PET_PHOTO_ASS.PHOTO_ID=PHOTO.ID?dataSource=#dataSource&outputType=SelectList")
      .filter()
        .simple("${body} == ${null} || ${body.empty}")
        .log("Pet [${headers[petId]}] not found")
        .setHeader(Exchange.HTTP_RESPONSE_CODE).constant("404")
        .setHeader(Exchange.HTTP_RESPONSE_TEXT).constant("Pet not found")
        .stop()
      .end()
      .log("Pet [${headers[petId]}] found")
      .transform().groovy("resource:classpath:/META-INF/groovy/SqlResultToPet.groovy")
    ;
    
    from("direct:findPetsByTags")
      .log(LoggingLevel.INFO, log, "Getting pet by tags: [${headers[tags]}]")
      .filter()
        .simple("${headers[tags]} == ${null} || ${headers[tags].empty} || ${headers[tags]} == ''")
        .log("No tags provided")
        .setHeader(Exchange.HTTP_RESPONSE_CODE).constant("400")
        .setHeader(Exchange.HTTP_RESPONSE_TEXT).constant("Invalid tag value")
        .stop()
      .end()
      .setHeader("tags").groovy("request.headers['tags'].collect() { it.toLowerCase() }")
      .to("sql:select * from PET, CATEGORY, PET_TAG_ASS, TAG, PET_PHOTO_ASS, PHOTO where TAG.LTAG_NAME in (:#in:tags) and PET.CATEGORY_ID=CATEGORY.ID and PET.ID=PET_TAG_ASS.PET_ID and PET_TAG_ASS.TAG_ID=TAG.ID and PET.ID=PET_PHOTO_ASS.PET_ID and PET_PHOTO_ASS.PHOTO_ID=PHOTO.ID?dataSource=#dataSource&outputType=SelectList")
      .filter()
        .simple("${body} == ${null} || ${body.empty}")
        .log("Pets with tags [${headers[tags]}] not found")
        .setHeader(Exchange.HTTP_RESPONSE_TEXT).constant(null)
        .stop()
      .end()
      .log("Pets with tags [${headers[tags]}] found")
      .transform().groovy("request.body.groupBy() { it['ID'] }.values()")
      .split(body()).aggregationStrategyRef("listOfPetAggregationStrategy")
        .transform().groovy("resource:classpath:/META-INF/groovy/SqlResultToPet.groovy")
      .end()
    ;
    
    from("direct:findPetsByStatus")
      .log(LoggingLevel.INFO, log, "Getting pet by status: [${headers[status]}]")
      .filter()
        .simple("${headers[status]} == ${null} || ${headers[status].empty} || ${headers[status]} == ''")
        .log("No status provided")
        .setHeader(Exchange.HTTP_RESPONSE_CODE).constant("400")
        .setHeader(Exchange.HTTP_RESPONSE_TEXT).constant("Invalid status value")
        .stop()
      .end()
      .setHeader("status")
        .groovy("request.headers['status'].collect() { it.toLowerCase() }")
      .to("sql:select * from PET, CATEGORY, PET_TAG_ASS, TAG, PET_PHOTO_ASS, PHOTO where PET.STATUS in (:#in:status) and PET.CATEGORY_ID=CATEGORY.ID and PET.ID=PET_TAG_ASS.PET_ID and PET_TAG_ASS.TAG_ID=TAG.ID and PET.ID=PET_PHOTO_ASS.PET_ID and PET_PHOTO_ASS.PHOTO_ID=PHOTO.ID?dataSource=#dataSource&outputType=SelectList")
      .filter()
        .simple("${body} == ${null} || ${body.empty}")
        .log("Pets with status [${headers[status]}] not found")
        .setHeader(Exchange.HTTP_RESPONSE_TEXT).constant(null)
        .stop()
      .end()
      .log("Pets with status [${headers[status]}] found")
      .transform().groovy("request.body.groupBy() { it['ID'] }.values()")
      .split(body()).aggregationStrategyRef("listOfPetAggregationStrategy")
        .transform().groovy("resource:classpath:/META-INF/groovy/SqlResultToPet.groovy")
      .end()
    ;
  }
}
