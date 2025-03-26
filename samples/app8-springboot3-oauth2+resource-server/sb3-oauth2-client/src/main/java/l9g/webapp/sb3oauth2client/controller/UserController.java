package l9g.webapp.sb3oauth2client.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import l9g.webapp.sb3oauth2client.config.WelcomeClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.info.BuildProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@RestController
@RequiredArgsConstructor
public class UserController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    UserController.class.getName());

  private final WelcomeClient welcomeClient;

  private final BuildProperties buildProperties;

  @GetMapping("/token")
  public OAuth2AuthenticationToken authenticationGET()
  {
    OAuth2AuthenticationToken token =
      (OAuth2AuthenticationToken)SecurityContextHolder.getContext().
        getAuthentication();

    token.getAuthorities().forEach(ga ->
    {

      if(ga instanceof OidcUserAuthority)
      {
        OidcUserAuthority userAuth = (OidcUserAuthority)ga;
        LOGGER.debug(">>> '{}'", userAuth.getIdToken().getTokenValue());
      }
    });

    return token;
  }

  @GetMapping("/principal")
  public OAuth2User principalGET(
    @AuthenticationPrincipal OAuth2User principal)
  {
    LOGGER.debug("principal.class={}", principal.getClass().getCanonicalName());
    LOGGER.debug("principal.name={}", principal.getName());
    return principal;
  }

  @GetMapping("/principal-attributes")
  public Map<String, Object> principalAttributesGET(
    @AuthenticationPrincipal OAuth2User principal)
  {
    return principal.getAttributes();
  }

  @GetMapping("/principal-userinfo")
  public OidcUserInfo principalUserinfoGET(
    @AuthenticationPrincipal OAuth2User principal)
  {
    return ((DefaultOidcUser)principal).getUserInfo();
  }

  @GetMapping("/principal-idtoken")
  public OidcIdToken principalIdTokenGET(
    @AuthenticationPrincipal OAuth2User principal)
  {
    return ((DefaultOidcUser)principal).getIdToken();
  }

  @GetMapping("/rscs1/welcome")
  public String welcomeFromResourceServer()
  {
    String welcome = welcomeClient.getWelcome();
    return "<pre>" + welcome + "</pre>";
  }

  @GetMapping("/rscs1/name")
  public String rscs1Name()
  {
    LOGGER.debug("rscs1Name");
    String message = welcomeClient.getName("Egon");
    return "<pre>" + message + "</pre>";
  }

  @GetMapping("/rscs1/parameter")
  public String rscs1ParameterName()
  {
    LOGGER.debug("rscs1ParameterName");
    String message = welcomeClient.getParamName("Fritz");
    return "<pre>" + message + "</pre>";
  }

  @GetMapping("/rscs1/buildinfo")
  public Map<String, String> rscs1Buildinfo()
  {
    LOGGER.debug("rscs1Buildinfo");
    return welcomeClient.getBuildinfo();
  }

  @GetMapping(path = "/rscs1/principal", produces = "application/json")
  public String getPrincipal()
  {
    return welcomeClient.getPrincipal();
  }

  @GetMapping("/api/buildinfo")
  public Map<String, String> apiBuildinfo()
  {
    LOGGER.debug("api buildinfo");

    ArrayList<String> keys = new ArrayList<>();
    buildProperties.forEach(p -> keys.add(p.getKey()));
    Collections.sort(keys);
    LinkedHashMap<String, String> properties = new LinkedHashMap<>();
    for(String key : keys)
    {
      properties.put(key, buildProperties.get(key));
    }

    return properties;
  }

}
