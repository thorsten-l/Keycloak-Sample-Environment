package l9g.webapp.sb3oauth2client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ResourceServerSecurityConfig
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    ResourceServerSecurityConfig.class.getName());

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  String issuerUri;

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
  {
    return http
      .authorizeHttpRequests(auth -> auth
      .anyRequest().authenticated()
      )
      .oauth2ResourceServer(oauth2 -> oauth2
      .jwt(jwt -> { jwt.decoder(JwtDecoders.fromIssuerLocation(issuerUri)); }))
      .build();
  }
}
