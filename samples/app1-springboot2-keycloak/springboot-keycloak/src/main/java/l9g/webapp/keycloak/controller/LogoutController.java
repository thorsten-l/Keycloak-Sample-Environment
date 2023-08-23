package l9g.webapp.keycloak.controller;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import org.keycloak.jose.jws.JWSInput;
import org.keycloak.representations.LogoutToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@RestController
public class LogoutController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    LogoutController.class.getName());

  @PostMapping(
    path = "/logout",
    consumes =
    {
      MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }
  )
  public void backChannelLogout(HttpServletRequest request,
    LogoutRequest logoutRequest) throws Exception
  {
    LOGGER.debug("backChannelLogout {}", logoutRequest);

    LogoutToken logoutToken
      = new JWSInput(logoutRequest.logout_token)
        .readJsonContent(LogoutToken.class);

    LOGGER.debug("  - logoutToken.getSubject {}", logoutToken.getSubject());
    LOGGER.debug("  - logoutToken.getId {}", logoutToken.getId());
    LOGGER.debug("  - logoutToken.getIssuer {}", logoutToken.getIssuer());
    LOGGER.debug("  - logoutToken.getSid {}", logoutToken.getSid());
    request.setAttribute( "logoutToken", logoutToken );
    request.logout();
  }
}
