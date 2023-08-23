package l9g.webapp.keycloak.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    KeycloakConfig.class.getName());

  @Bean
  public KeycloakSpringBootConfigResolver keycloakConfigResolver()
  {
    LOGGER.debug("keycloakConfigResolver");
    KeycloakSpringBootConfigResolver configResolver
      = new KeycloakSpringBootConfigResolver();
    return configResolver;
  }
}
