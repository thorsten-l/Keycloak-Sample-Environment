package l9g.webapp.sb3oauth2client.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController
{
  @GetMapping("/app")
  public String appGET(Model model, @AuthenticationPrincipal OAuth2User principal)
  {
    model.addAttribute("fullname", ((DefaultOidcUser)principal).getFullName());
    return "app";
  }
}
