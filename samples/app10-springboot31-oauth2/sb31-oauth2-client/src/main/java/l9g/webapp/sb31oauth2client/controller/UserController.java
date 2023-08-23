/*
 * Copyright 2022 Thorsten Ludewig (t.ludewig@gmail.com).
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
package l9g.webapp.sb31oauth2client.controller;

import java.util.Map;
import l9g.webapp.sb31oauth2client.OAuth2AuthorizedClientProvider;
import l9g.webapp.sb31oauth2client.config.BuildProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
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

  @Autowired
  private final OAuth2AuthorizedClientProvider oauth2AuthorizedClientProvider;

  @Autowired
  private JwtDecoder jwtDecoder;

  @GetMapping("/principal")
  public OAuth2User principalGET(
    @AuthenticationPrincipal OAuth2User principal)
  {
    LOGGER.debug("principal.class={}", principal.getClass().getCanonicalName());
    LOGGER.debug("principal.name={}", principal.getName());
    return principal;
  }

  @GetMapping("/principal-authtoken")
  public OAuth2AuthenticationToken principalAuthTokenGET(
    OAuth2AuthenticationToken authToken)
  {
    LOGGER.debug("authToken={}", authToken);
    return authToken;
  }

  @GetMapping("/authentication")
  public Authentication accessTokenGET(Authentication authentication)
  {
    LOGGER.debug("authentication.class={}", authentication.getClass().
      getCanonicalName());
    return authentication;
  }

  @GetMapping("/principal-attributes")
  public Map<String, Object> principalAttributesGET(
    @AuthenticationPrincipal DefaultOidcUser principal)
  {
    return principal.getAttributes();
  }

  @GetMapping("/principal-userinfo")
  public OidcUserInfo principalUserinfoGET(
    @AuthenticationPrincipal DefaultOidcUser principal)
  {
    return principal.getUserInfo();
  }

  @GetMapping("/principal-idtoken")
  public OidcIdToken principalIdTokenGET(
    @AuthenticationPrincipal DefaultOidcUser principal)
  {
    LOGGER.debug("idToken={}", principal.getIdToken().getTokenValue());
    return principal.getIdToken();
  }

  @GetMapping("/accesstoken")
  public Jwt accessTokenGET()
  {
    LOGGER.debug("accessTokenGET");

    OAuth2AuthorizedClient client = oauth2AuthorizedClientProvider.getClient();
    OAuth2RefreshToken refreshToken = client.getRefreshToken();

    return jwtDecoder.decode(client.getAccessToken().getTokenValue());
  }

  @GetMapping("/principal-claims")
  public Map<String, Object> principalClaimsGET(
    @AuthenticationPrincipal DefaultOidcUser principal)
  {
    return principal.getClaims();
  }

  @GetMapping("/build-properties")
  public BuildProperties buildPropertiesGET()
  {
    return BuildProperties.getInstance();
  }
}
