/*
 * Copyright 2022 Thorsten Ludewig (t.ludewig@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package l9g.webapp.sb31oauth2client;

import l9g.webapp.sb31oauth2client.config.BuildProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Sb31Oauth2ClientApplication
{
  /*
  
    use "data/config/" for better integration with docker image build
    by spring-boot maven task 
  
    mvn spring-boot:build-image
  
    docker run --rm --name sb3-oauth2-client -p 8081:8081 \
      -v `pwd`/data:/workspace/data sb3-oauth2-client:0.0.1-SNAPSHOT
  
  */
  private final static String CONFIG_PATH = "data/config/";

  static
  {
    // very first set configurations
    System.setProperty(
      "spring.config.location", "file:" + CONFIG_PATH);
    System.setProperty(
      "spring.banner.location", "file:" + CONFIG_PATH + "banner.txt");
    System.setProperty(
      "logging.config", CONFIG_PATH + "logback.xml");
  }

  private final static Logger LOGGER = LoggerFactory.getLogger(Sb31Oauth2ClientApplication.class.getName());

  public static void main(String[] args)
  {
    LOGGER.info("START");
    LOGGER.info("{}", BuildProperties.toFormattedString());
    SpringApplication.run(Sb31Oauth2ClientApplication.class, args);
  }
}
