package l9g.webapp.sb3oauth2client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class ClientSecurityConfig
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    ClientSecurityConfig.class.getName());

  @Autowired
  private ClientRegistrationRepository clientRegistrationRepository;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
  {
    LOGGER.debug("filterChain");
    http.authorizeHttpRequests(
      authorize -> authorize
        .requestMatchers("/").permitAll()
        .requestMatchers( new AntPathRequestMatcher("/actuator/**")).permitAll()
        .requestMatchers( new AntPathRequestMatcher("/webjars/**")).permitAll()
        .anyRequest().authenticated())
      .oauth2Login()
      .and()
      .logout((logout) ->
      {
        logout.logoutSuccessHandler(oidcLogoutSuccessHandler());
      });

    return http.build();
  }

  private LogoutSuccessHandler oidcLogoutSuccessHandler()
  {
    LOGGER.debug("oidcLogoutSuccessHandler");

    OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler
      = new OidcClientInitiatedLogoutSuccessHandler(
        this.clientRegistrationRepository);

    // Sets the location that the End-User's User Agent will be redirected to
    // after the logout has been performed at the Provider
    oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");
    //oidcLogoutSuccessHandler.setPostLogoutRedirectUri("http://app1.dev.sonia.de:8081");

    return oidcLogoutSuccessHandler;
  }

}
