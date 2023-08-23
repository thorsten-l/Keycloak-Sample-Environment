package l9g.webapp.keycloak.controller;

import java.security.Principal;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import l9g.webapp.keycloak.config.BuildProperties;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Controller
public class InfoController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    InfoController.class.getName());

  @GetMapping("/info")
  public String infoGET(Model model)
  {
    LOGGER.debug("infoGET");

    KeycloakSecurityContext securityContext = null;
    String realm = null;
    IDToken idToken = null;
    AccessToken accessToken = null;
    Map<String, Object> otherClaims = null;

    KeycloakAuthenticationToken authentication
      = (KeycloakAuthenticationToken) SecurityContextHolder.getContext()
        .getAuthentication();

    Principal principal = (Principal) authentication.getPrincipal();

    if (principal instanceof KeycloakPrincipal)
    {
      KeycloakPrincipal<KeycloakSecurityContext> kPrincipal
        = (KeycloakPrincipal<KeycloakSecurityContext>) principal;

      securityContext = kPrincipal.getKeycloakSecurityContext();

      if (securityContext != null)
      {
        realm = securityContext.getRealm();
        idToken = securityContext.getIdToken();
        accessToken = securityContext.getToken();
      }

      if (idToken != null)
      {
        otherClaims = idToken.getOtherClaims();
      }
    }

    //
    model.addAttribute("principal", principal);
    model.addAttribute("build", BuildProperties.getInstance());
    model.addAttribute("applicationName", System.getProperties().getProperty(
      "application.name", "application.name not set"));
    model.addAttribute("securityContext", securityContext);
    model.addAttribute("realm", realm);
    model.addAttribute("idToken", idToken);
    model.addAttribute("accessToken", accessToken);
    model.addAttribute("otherClaims", otherClaims);
    return "info";
  }

  @GetMapping("/logout")
  public String logoutGET(HttpServletRequest request) throws Exception
  {
    LOGGER.debug("logoutGET");
    request.logout();
    return "redirect:/";
  }
}
