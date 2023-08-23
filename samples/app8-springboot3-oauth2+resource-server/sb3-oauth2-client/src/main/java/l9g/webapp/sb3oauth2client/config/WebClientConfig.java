package l9g.webapp.sb3oauth2client.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Configuration
public class WebClientConfig
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    WebClientConfig.class.getName());

  @Value("${spring.webclient.base-url}")
  String webClientBaseUrl;
  
  @Bean
  public WelcomeClient welcomeClient(
    OAuth2AuthorizedClientManager authorizedClientManager) throws Exception
  {
    return httpServiceProxyFactory(authorizedClientManager)
      .createClient(WelcomeClient.class);
  }

  private HttpServiceProxyFactory httpServiceProxyFactory(
    OAuth2AuthorizedClientManager authorizedClientManager)
  {
    LOGGER.debug("httpServiceProxyFactory webClientBaseUrl={}", webClientBaseUrl);
    
    ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client
      = new ServletOAuth2AuthorizedClientExchangeFilterFunction(
        authorizedClientManager);

    oauth2Client.setDefaultOAuth2AuthorizedClient(true);
    
    WebClient webClient = WebClient.builder()
      .apply(oauth2Client.oauth2Configuration())
      .baseUrl(webClientBaseUrl)
      .build();
    
    WebClientAdapter client = WebClientAdapter.forClient(webClient);
    return HttpServiceProxyFactory.builder(client).build();
  }

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager(
    ClientRegistrationRepository clientRegistrationRepository,
    OAuth2AuthorizedClientRepository authorizedClientRepository)
  {

    OAuth2AuthorizedClientProvider authorizedClientProvider
      = OAuth2AuthorizedClientProviderBuilder.builder()
        .authorizationCode()
        .refreshToken()
        .build();

    DefaultOAuth2AuthorizedClientManager authorizedClientManager
      = new DefaultOAuth2AuthorizedClientManager(
        clientRegistrationRepository, authorizedClientRepository);

    authorizedClientManager.
      setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }

}
