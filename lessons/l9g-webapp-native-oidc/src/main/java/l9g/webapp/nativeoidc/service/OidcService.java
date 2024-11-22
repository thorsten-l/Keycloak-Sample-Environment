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
package l9g.webapp.nativeoidc.service;

import l9g.webapp.nativeoidc.dto.JwksCerts;
import l9g.webapp.nativeoidc.dto.OAuth2Tokens;
import l9g.webapp.nativeoidc.dto.OidcDiscovery;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Service
@Slf4j
@Getter
@RequiredArgsConstructor
public class OidcService
{
  private final RestClient.Builder builder;

  private final JwtService jwtService;

  private RestClient restClient;

  @Value("${oidc.discovery-uri:}")
  private String oidcDiscoveryUri;

  @Value("${oauth2.authorization.endpoint:}")
  private String oauth2AuthorizationEndpoint;

  @Value("${oauth2.token.endpoint:}")
  private String oauth2tokenEndpoint;

  @Value("${oauth2.end-session.endpoint:}")
  private String oauth2EndSessionEndpoint;

  @Value("${oauth2.client.secret}")
  private String oauth2ClientSecret;

  @Value("${oauth2.redirect-uri}")
  private String oauth2RedirectUri;

  @Value("${oauth2.jwks_uri:}")
  private String oauth2JwksUri;

  private OidcDiscovery oidcDiscovery;

  @PostConstruct
  private void initialize()
  {
    log.debug("initialize oidcDiscoveryUri={}", oidcDiscoveryUri);
    restClient = builder.baseUrl(oidcDiscoveryUri).build();

    ResponseEntity<OidcDiscovery> responseEntity = restClient
      .get()
      .uri(oidcDiscoveryUri)
      .retrieve()
      .toEntity(new ParameterizedTypeReference<OidcDiscovery>()
      {
      });

    if(responseEntity.getStatusCode().is2xxSuccessful())
    {
      log.debug("*** is2xxSuccessful {}", responseEntity.getBody());
      oidcDiscovery = responseEntity.getBody();

      if(oauth2AuthorizationEndpoint != null && oauth2AuthorizationEndpoint.isBlank())
      {
        oauth2AuthorizationEndpoint = oidcDiscovery.authorizationEndpoint();
      }
      log.debug("oauth2AuthorizationEndpoint={}", oauth2AuthorizationEndpoint);

      if(oauth2tokenEndpoint != null && oauth2tokenEndpoint.isBlank())
      {
        oauth2tokenEndpoint = oidcDiscovery.tokenEndpoint();
      }
      log.debug("oauth2AuthorizationEndpoint={}", oauth2tokenEndpoint);

      if(oauth2EndSessionEndpoint != null && oauth2EndSessionEndpoint.isBlank())
      {
        oauth2EndSessionEndpoint = oidcDiscovery.endSessionEndpoint();
      }
      log.debug("oauth2EndSessionEndpoint={}", oauth2EndSessionEndpoint);

      if(oauth2JwksUri != null && oauth2JwksUri.isBlank())
      {
        oauth2JwksUri = oidcDiscovery.jwksUri();
      }
      log.debug("oauth2JwksUri={}", oauth2JwksUri);

      ResponseEntity<JwksCerts> certEntity = restClient
        .get()
        .uri(oauth2JwksUri)
        .retrieve()
        .toEntity(new ParameterizedTypeReference<JwksCerts>()
        {
        });

      if(certEntity.getStatusCode().is2xxSuccessful())
      {
        log.debug("*** certEntity is2xxSuccessful {}", certEntity.getBody());
        jwtService.setOauth2JwksCert(certEntity.getBody());
      }
    }
  }

  public OAuth2Tokens fetchOAuth2Tokens(String code, String codeVerifier)
  {
    OAuth2Tokens tokens = null;

    MultiValueMap<String, String> bodyData = new LinkedMultiValueMap<>();
    bodyData.add("grant_type", "authorization_code");
    bodyData.add("code", code);
    bodyData.add("redirect_uri", oauth2RedirectUri);
    bodyData.add("client_id", "app1");
    bodyData.add("client_secret", oauth2ClientSecret);

    if(codeVerifier != null &&  ! codeVerifier.isBlank())
    {
      log.debug("code_verifier={}", codeVerifier);
      bodyData.add("code_verifier", codeVerifier);
    }

    ResponseEntity<OAuth2Tokens> responseEntity = restClient
      .post()
      .uri(oauth2tokenEndpoint)
      .header("Content-Type", "application/x-www-form-urlencoded")
      .body(bodyData)
      .retrieve()
      .toEntity(new ParameterizedTypeReference<OAuth2Tokens>()
      {
      });

    if(responseEntity.getStatusCode().is2xxSuccessful())
    {
      tokens = responseEntity.getBody();

      log.debug("accessToken={}", tokens.accessToken());
      log.debug("valid access token = {}", jwtService.validateJwtSignature(tokens.accessToken()));
      log.debug("idToken={}", tokens.idToken());
      log.debug("valid id token = {}", jwtService.validateJwtSignature(tokens.idToken()));
      log.debug("refreshToken={}", tokens.refreshToken());
      log.debug("valid refresh token = {}", jwtService.validateJwtSignature(tokens.refreshToken()));
      log.debug("expiresIn={}", tokens.expiresIn());
    }

    return tokens;
  }

}