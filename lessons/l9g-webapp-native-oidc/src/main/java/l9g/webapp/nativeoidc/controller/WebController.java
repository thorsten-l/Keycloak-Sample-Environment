/*
 * Copyright 2024 Thorsten Ludewig (t.ludewig@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package l9g.webapp.nativeoidc.controller;

import jakarta.annotation.PostConstruct;
import l9g.webapp.nativeoidc.service.OidcService;
import l9g.webapp.nativeoidc.service.JwtService;
import l9g.webapp.nativeoidc.dto.OAuth2Tokens;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.UUID;
import l9g.webapp.nativeoidc.service.PKCE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class WebController
{
  private static final String SESSION_OAUTH2_STATE = "oauth2State";

  private static final String SESSION_OAUTH2_TOKENS = "oauth2Tokens";

  private static final String SESSION_OAUTH2_CODE_VERIFIER = "oauth2CodeVerifier";

  private final OidcService oidcService;

  private final JwtService jwtService;

  @Value("${oauth2.redirect-uri}")
  private String oauth2RedirectUri;

  @Value("${oauth2.client.id}")
  private String oauth2ClientId;

  @Value("${oauth2.client.scope}")
  private String oauth2ClientScope;

  @Value("${oauth2.post-logout-redirect-uri}")
  private String oauth2PostLogoutRedirectUri;

  private final HashMap<String, HttpSession> sessionStore = new HashMap<>();

  @PostConstruct
  private void initialize()
  {
    log.debug("oauth2RedirectUrl={}", oauth2RedirectUri);
    log.debug("oauth2ClientId={}", oauth2ClientId);
    log.debug("oauth2ClientScope={}", oauth2ClientScope);
  }

  @GetMapping("/")
  public String home(HttpSession session, Model model)
    throws Exception
  {
    String oauth2State = UUID.randomUUID().toString();
    String oauth2CodeVerifier = PKCE.generateCodeVerifier();
    String oauth2CodeChallenge = PKCE.generateCodeChallenge(oauth2CodeVerifier);

    log.debug("home={}", session.getId());

    log.debug("oauth2State={}", oauth2State);
    log.debug("oauth2CodeVerifier={}", oauth2CodeVerifier);
    log.debug("oauth2CodeChallenge={}", oauth2CodeChallenge);

    session.setAttribute(SESSION_OAUTH2_STATE, oauth2State);
    session.setAttribute(SESSION_OAUTH2_CODE_VERIFIER, oauth2CodeVerifier);

    model.addAttribute(SESSION_OAUTH2_STATE, oauth2State);
    model.addAttribute("oauth2ClientScope", oauth2ClientScope);
    model.addAttribute("oauth2ClientId", oauth2ClientId);
    model.addAttribute("oauth2RedirectUri", oauth2RedirectUri);
    model.addAttribute("oauth2AuthorizationEndpoint", oidcService.getOauth2AuthorizationEndpoint());
    model.addAttribute("oauth2ResponseType", "code");
    model.addAttribute("oauth2CodeChallenge", oauth2CodeChallenge);
    model.addAttribute("oauth2CodeChallengeMethod", "S256");
    model.addAttribute("oauth2CodeVerifier", oauth2CodeVerifier);
    model.addAttribute("sessionId", session.getId());

    return "home";
  }

  @GetMapping("/oidc-login")
  public String oidcLogin(
    @RequestParam(name = "code", required = false) String code,
    @RequestParam(name = "state", required = false) String state,
    @RequestParam(name = "error", required = false) String error,
    @RequestParam(name = "error_description", required = false) String errorDescription,
    HttpSession session,
    Model model)
  {
    log.debug("oidcLogin: code={}", code);
    log.debug("oidcLogin: state={}", state);
    log.debug("oidcLogin: error={}", error);
    log.debug("oidcLogin: error_description={}", errorDescription);

    String oauth2State = (String)session.getAttribute(SESSION_OAUTH2_STATE);

    if(oauth2State == null ||  ! oauth2State.equals(state))
    {
      log.error("Illegal 'state'");
      return "redirect:/";
    }

    if(error != null &&  ! error.isBlank())
    {
      log.error("Error: {} / {}", error, errorDescription);
      return "redirect:/";
    }

    OAuth2Tokens tokens = oidcService.fetchOAuth2Tokens(
      code, (String)session.getAttribute(SESSION_OAUTH2_CODE_VERIFIER));
    session.setAttribute(SESSION_OAUTH2_TOKENS, tokens);

    sessionStore.put(
      jwtService.decodeJwtPayload(tokens.idToken()).get("sid"), session);

    return "redirect:/app";
  }

  @GetMapping("/oidc-logout")
  public String oidcLogout(HttpSession session)
  {
    log.debug("oidcLogout");
    session.invalidate();
    return "redirect:/";
  }

  @PostMapping("/oidc-backchannel-logout")
  public ResponseEntity<Void> handleBackchannelLogout(@RequestBody String logoutToken)
  {
    log.debug("handleBackchannelLogout logoutToken={}", logoutToken);
    log.debug("decoded logoutToken={}", jwtService.decodeJwtPayload(logoutToken));

    String sid = jwtService.decodeJwtPayload(logoutToken).get("sid");

    if(sid != null)
    {
      HttpSession session = sessionStore.get(sid);
      if(session != null)
      {
        log.debug("invalidate session {}", session.getId());
        session.invalidate();
        sessionStore.remove(sid);
      }
    }

    return ResponseEntity.ok().build();
  }

  @GetMapping("/app")
  public String app(HttpSession session, Model model)
  {
    log.debug("app: session id = {}", session.getId());

    OAuth2Tokens oauth2Tokens = (OAuth2Tokens)session.getAttribute(SESSION_OAUTH2_TOKENS);

    if(oauth2Tokens == null || oauth2Tokens.idToken() == null)
    {
      return "redirect:/";
    }

    model.addAttribute("oauth2EndSessionEndpoint", oidcService.getOauth2EndSessionEndpoint());
    model.addAttribute("oauth2IdToken", oauth2Tokens.idToken());
    model.addAttribute("oauth2PostLogoutRedirectUri", oauth2PostLogoutRedirectUri);

    model.addAttribute("accessTokenMap", jwtService.decodeJwtPayload(oauth2Tokens.accessToken()));
    model.addAttribute("idTokenMap", jwtService.decodeJwtPayload(oauth2Tokens.idToken()));
    model.addAttribute("refreshTokenMap", jwtService.decodeJwtPayload(oauth2Tokens.refreshToken()));

    return "app";
  }

}
