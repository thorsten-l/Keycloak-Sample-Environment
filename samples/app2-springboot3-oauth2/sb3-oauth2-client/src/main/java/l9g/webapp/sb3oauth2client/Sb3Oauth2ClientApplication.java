package l9g.webapp.sb3oauth2client;

import l9g.webapp.sb3oauth2client.config.BuildProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Sb3Oauth2ClientApplication
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

  private final static Logger LOGGER = LoggerFactory.getLogger(
    Sb3Oauth2ClientApplication.class.getName());

  public static void main(String[] args)
  {
    LOGGER.info("START");
    LOGGER.info("{}", BuildProperties.toFormattedString());
    SpringApplication.run(Sb3Oauth2ClientApplication.class, args);
  }
}
