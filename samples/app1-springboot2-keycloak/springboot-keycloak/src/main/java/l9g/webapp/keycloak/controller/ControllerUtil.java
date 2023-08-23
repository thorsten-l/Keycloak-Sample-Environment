package l9g.webapp.keycloak.controller;

import l9g.webapp.keycloak.config.BuildProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
public class ControllerUtil
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    ControllerUtil.class.getName());

  public static String addGeneralAttributes(Model model)
  {
    String userId = null;
    model.addAttribute("errorMessage", null);
    model.addAttribute("build", BuildProperties.getInstance());
    return userId;
  }
}
