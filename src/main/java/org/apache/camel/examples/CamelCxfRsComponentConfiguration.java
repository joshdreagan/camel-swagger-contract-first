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

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class CamelCxfRsComponentConfiguration {

  private static final Logger log = LoggerFactory.getLogger(CamelCxfRsComponentConfiguration.class);
  
  @Bean
  List<Feature> cxfFeatures(ApplicationContext applicationContext) {
    List<Feature> cxfFeatures = new ArrayList<>();
    Map<String, Feature> cxfFeatureBeanMap = applicationContext.getBeansOfType(Feature.class);
    cxfFeatures.addAll(cxfFeatureBeanMap.values());
    return cxfFeatures;
  }
  
  @Bean
  @ConfigurationProperties(prefix = "api.swagger")
  Swagger2Feature swaggerFeature() {
    Swagger2Feature swaggerFeature = new Swagger2Feature();
    
    // Override some defaults.
    swaggerFeature.setPrettyPrint(true);
    swaggerFeature.setRunAsFilter(true);
    
    return swaggerFeature;
  }
  
  @Bean
  List<Object> cxfProviders(ApplicationContext applicationContext) {
    List<Object> cxfProviders = new ArrayList<>();
    cxfProviders.add(applicationContext.getBean("corsFilter"));
    cxfProviders.add(applicationContext.getBean("jsonProvider"));
    return cxfProviders;
  }
  
  @Bean
  @ConfigurationProperties(prefix = "api.cors")
  CrossOriginResourceSharingFilter corsFilter() {
    CrossOriginResourceSharingFilter corsFilter = new CrossOriginResourceSharingFilter();

    // Set these to something editable so that we can use properties.
    corsFilter.setAllowHeaders(new ArrayList());
    corsFilter.setAllowOrigins(new ArrayList());
    corsFilter.setExposeHeaders(new ArrayList());
    
    return corsFilter;
  }
  
  @Bean
  JacksonJsonProvider jsonProvider() {
    JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider();
    jacksonJsonProvider.configure(SerializationFeature.INDENT_OUTPUT, true);
    return jacksonJsonProvider;
  }
}
