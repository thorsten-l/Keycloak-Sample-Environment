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
import java.util.function.Supplier;
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
 * OidcService is a Spring service that handles OpenID Connect (OIDC) and OAuth2 operations.
 * It initializes OIDC discovery endpoints and fetches OAuth2 tokens.
 *
 * <p>
 * It uses the following properties for configuration:
 * <ul>
 * <li>oidc.discovery-uri: URI for OIDC discovery</li>
 * <li>oauth2.authorization.endpoint: OAuth2 authorization endpoint</li>
 * <li>oauth2.token.endpoint: OAuth2 token endpoint</li>
 * <li>oauth2.end-session.endpoint: OAuth2 end session endpoint</li>
 * <li>oauth2.client.secret: OAuth2 client secret</li>
 * <li>oauth2.redirect-uri: OAuth2 redirect URI</li>
 * <li>oauth2.jwks_uri: OAuth2 JWKS URI</li>
 * </ul>
 *
 * <p>
 * Methods:
 * <ul>
 * <li>{@link #initialize()}: Initializes the OIDC discovery and sets up endpoints.</li>
 * <li>{@link #fetchOAuth2Tokens(String, String)}: Fetches OAuth2 tokens using authorization code and code verifier.</li>
 * </ul>
 *
 * <p>
 * Dependencies:
 * <ul>
 * <li>{@link RestClient.Builder}: Builder for creating RestClient instances.</li>
 * <li>{@link JwtService}: Service for handling JWT operations.</li>
 * </ul>
 *
 * <p>
 * Author: Thorsten Ludewig (t.ludewig@gmail.com)
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

  @Value("${oidc.discovery-uri}")
  private String oidcDiscoveryUri;

  @Value("${oauth2.authorization.endpoint:}")
  private String oauth2AuthorizationEndpoint;

  @Value("${oauth2.token.endpoint:}")
  private String oauth2tokenEndpoint;

  @Value("${oauth2.end-session.endpoint:}")
  private String oauth2EndSessionEndpoint;

  @Value("${oauth2.client.id}")
  private String oauth2ClientId;

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

    oidcDiscovery = fetchData(oidcDiscoveryUri, OidcDiscovery.class);

    if(oidcDiscovery != null)
    {
      oauth2AuthorizationEndpoint = defaultIfNullOrBlank(
        oauth2AuthorizationEndpoint, oidcDiscovery :: authorizationEndpoint);
      oauth2tokenEndpoint = defaultIfNullOrBlank(
        oauth2tokenEndpoint, oidcDiscovery :: tokenEndpoint);
      oauth2EndSessionEndpoint = defaultIfNullOrBlank(
        oauth2EndSessionEndpoint, oidcDiscovery :: endSessionEndpoint);
      oauth2JwksUri = defaultIfNullOrBlank(
        oauth2JwksUri, oidcDiscovery :: jwksUri);

      log.debug("oauth2AuthorizationEndpoint={}", oauth2AuthorizationEndpoint);
      log.debug("oauth2AuthorizationEndpoint={}", oauth2tokenEndpoint);
      log.debug("oauth2EndSessionEndpoint={}", oauth2EndSessionEndpoint);
      log.debug("oauth2JwksUri={}", oauth2JwksUri);

      JwksCerts jwksCerts = fetchData(oauth2JwksUri, JwksCerts.class);

      if(jwksCerts != null)
      {
        log.debug("{}",jwksCerts);
        jwtService.setOauth2JwksCerts(jwksCerts);
      }
    }
    else
    {
      log.error("Coult not fetch discovery endpoints.");
      System.exit(-1);
    }
  }

  public OAuth2Tokens fetchOAuth2Tokens(String code, String codeVerifier)
  {
    OAuth2Tokens tokens = null;

    MultiValueMap<String, String> bodyData = new LinkedMultiValueMap<>();
    bodyData.add("grant_type", "authorization_code");
    bodyData.add("code", code);
    bodyData.add("redirect_uri", oauth2RedirectUri);
    bodyData.add("client_id", oauth2ClientId);
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

      log.trace("client secret='{}'", oauth2ClientSecret);
      log.debug("accessToken={}", tokens.accessToken());
      log.debug("valid access token = {}",
        jwtService.validateJwtSignature(tokens.accessToken()));
      log.debug("idToken={}", tokens.idToken());
      log.debug("valid id token = {}",
        jwtService.validateJwtSignature(tokens.idToken()));
      log.debug("refreshToken={}", tokens.refreshToken());
      log.debug("valid refresh token = {}",
        jwtService.validateJwtSignature(tokens.refreshToken()));
      log.debug("expiresIn={}", tokens.expiresIn());
    }

    return tokens;
  }

  private <T> T fetchData(String uri, Class clazz)
  {
    try
    {
      ResponseEntity<T> responseEntity = restClient
        .get()
        .uri(uri)
        .retrieve()
        .toEntity(parameterizedTypeReferenceFromClass(clazz));

      if(responseEntity.getStatusCode().is2xxSuccessful())
      {
        return responseEntity.getBody();
      }
    }
    catch(Exception e)
    {
      log.error("Error fetching data from {}", uri, e);
    }
    return null;
  }

  private String defaultIfNullOrBlank(
    String value, Supplier<String> defaultValueSupplier)
  {
    return (value == null
      || value.isBlank()) ? defaultValueSupplier.get() : value;
  }

  private <T> ParameterizedTypeReference<T>
    parameterizedTypeReferenceFromClass(Class<T> clazz)
  {
    return new ParameterizedTypeReference<>()
    {
      @Override
      public java.lang.reflect.Type getType()
      {
        return clazz;
      }

    };
  }

}
