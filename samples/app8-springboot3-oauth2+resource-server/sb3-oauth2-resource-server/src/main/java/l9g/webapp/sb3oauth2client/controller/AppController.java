package l9g.webapp.sb3oauth2client.controller;

import java.time.LocalDateTime;
import java.util.Map;
import l9g.util.BuildProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    AppController.class.getName());

  @GetMapping("/")
  public String homeGET(JwtAuthenticationToken jwtAuthenticationToken)
  {
    LOGGER.debug("homeGET");
    LOGGER.debug("authentication.name={}", jwtAuthenticationToken.getName());
    LOGGER.debug("authentication.class={}", jwtAuthenticationToken.getClass().
      getCanonicalName());
    LOGGER.debug("authentication.principal.class={}", jwtAuthenticationToken.
      getPrincipal().getClass().getCanonicalName());
    LOGGER.debug("authentication.details.class={}", jwtAuthenticationToken.
      getDetails().getClass().getCanonicalName());
    LOGGER.debug("authentication.credentials.class={}", jwtAuthenticationToken.
      getCredentials().getClass().getCanonicalName());

    Jwt jwt = (Jwt) jwtAuthenticationToken.getPrincipal();

    String preferredUsername
      = jwt.getClaimAsString(StandardClaimNames.PREFERRED_USERNAME);

    StringBuilder message = new StringBuilder("Welcome from resource server!\n");
    LocalDateTime time = LocalDateTime.now();

    message = message.append("\n-----------");
    message = message.append("\njwt=").append(jwt.getClass().getCanonicalName());
    message = message.append("\npreferred_username=").append(preferredUsername);

    WebAuthenticationDetails wad = (WebAuthenticationDetails) jwtAuthenticationToken.
      getDetails();

    message = message.append("\nremote ip addr=").append(wad.getRemoteAddress());

    message = message.append("\n-----------");

    message = message.append("\nlocal time=").append(time);
    message = message.append("\njwt id=").append(jwt.getId());
    message = message.append("\njwt issuer=").append(jwt.getIssuer().
      toExternalForm());

    Map<String, Object> claims = jwt.getClaims();

    message = message.append("\n-----------");

    for (String key : claims.keySet())
    {
      message = message.append("\njwt claim '").append(key);
      message = message.append("' = ").append(claims.get(key).toString());
    }

    return message.toString();
  }

  @GetMapping(path = "/n/{name}")
  public String getName(@PathVariable("name") String name,
    Authentication authentication)
  {
    LOGGER.debug("getName name = {}", name);

    StringBuilder message = new StringBuilder("Welcome from resource server!\n");
    LocalDateTime time = LocalDateTime.now();

    message = message.append("\nname=").append(name);

    Jwt jwt = (Jwt) authentication.getPrincipal();
    message = message.append("\n\nlocal time=").append(time);
    message = message.append("\njwt id=").append(jwt.getId());
    message = message.append("\njwt issuer=").append(jwt.getIssuer().
      toExternalForm());

    Map<String, Object> claims = jwt.getClaims();

    for (String key : claims.keySet())
    {
      message = message.append("\njwt claim '").append(key);
      message = message.append("' = ").append(claims.get(key).toString());
    }

    return message.toString();
  }

  @GetMapping(path = "/p")
  public String getParamName(@RequestParam("name") String name,
    Authentication authentication)
  {
    LOGGER.debug("getParamName name = {}", name);

    StringBuilder message = new StringBuilder("Welcome from resource server!\n");
    LocalDateTime time = LocalDateTime.now();

    message = message.append("\nparameter name=").append(name);

    Jwt jwt = (Jwt) authentication.getPrincipal();
    message = message.append("\n\nlocal time=").append(time);
    message = message.append("\njwt id=").append(jwt.getId());
    message = message.append("\njwt issuer=").append(jwt.getIssuer().
      toExternalForm());

    Map<String, Object> claims = jwt.getClaims();

    for (String key : claims.keySet())
    {
      message = message.append("\njwt claim '").append(key);
      message = message.append("' = ").append(claims.get(key).toString());
    }

    return message.toString();
  }

  @GetMapping(path = "/buildinfo")
  public BuildProperties getBuildinfo()
  {
    LOGGER.debug("getBuildinfo");
    return BuildProperties.getInstance();
  }

  @GetMapping(path = "/rscs-principal")
  public Jwt getPrincipal(JwtAuthenticationToken jwtAuthenticationToken)
  {
    LOGGER.debug("getPrincipal");
    return (Jwt) jwtAuthenticationToken.getPrincipal();
  }
  
  @GetMapping(path = "/rscs-authtoken")
  public JwtAuthenticationToken getAuthToken(JwtAuthenticationToken jwtAuthenticationToken)
  {
    LOGGER.debug("getAuthToken");
    return jwtAuthenticationToken;
  }

}
