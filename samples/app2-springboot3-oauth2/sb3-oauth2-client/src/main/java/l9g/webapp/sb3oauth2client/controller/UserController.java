package l9g.webapp.sb3oauth2client.controller;

import java.util.Map;
import l9g.webapp.sb3oauth2client.config.BuildProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@RestController
public class UserController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    UserController.class.getName());

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
    return ((DefaultOidcUser) principal).getUserInfo();
  }

  @GetMapping("/principal-idtoken")
  public OidcIdToken principalIdTokenGET(
    @AuthenticationPrincipal OAuth2User principal)
  {
    return ((DefaultOidcUser) principal).getIdToken();
  }

  @GetMapping("/build-properties")
  public BuildProperties buildPropertiesGET()
  {
    return BuildProperties.getInstance();
  }
}
