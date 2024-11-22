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
package l9g.webapp.nativeoidc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents the OpenID Connect (OIDC) Discovery metadata.
 * This record is used to deserialize the JSON response from the OIDC discovery endpoint.
 *
 * @param issuer The issuer identifier for the OpenID Provider.
 * @param authorizationEndpoint The URL of the authorization endpoint.
 * @param tokenEndpoint The URL of the token endpoint.
 * @param introspectionEndpoint The URL of the introspection endpoint.
 * @param userinfoEndpoint The URL of the userinfo endpoint.
 * @param endSessionEndpoint The URL of the end session endpoint.
 * @param frontchannelLogoutSessionSupported Indicates if frontchannel logout session is supported.
 * @param frontchannelLogoutSupported Indicates if frontchannel logout is supported.
 * @param jwksUri The URL of the JSON Web Key Set (JWKS) endpoint.
 * @param checkSessionIframe The URL of the check session iframe.
 * @param grantTypesSupported The list of grant types supported by the OpenID Provider.
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OidcDiscovery(
  String issuer,
  @JsonProperty("authorization_endpoint")
  String authorizationEndpoint,
  @JsonProperty("token_endpoint")
  String tokenEndpoint,
  @JsonProperty("introspection_endpoint")
  String introspectionEndpoint,
  @JsonProperty("userinfo_endpoint")
  String userinfoEndpoint,
  @JsonProperty("end_session_endpoint")
  String endSessionEndpoint,
  @JsonProperty("frontchannel_logout_session_supported")
  boolean frontchannelLogoutSessionSupported,
  @JsonProperty("frontchannel_logout_supported")
  boolean frontchannelLogoutSupported,
  @JsonProperty("jwks_uri")
  String jwksUri,
  @JsonProperty("check_session_iframe")
  String checkSessionIframe,
  @JsonProperty("grant_types_supported")
  List<String> grantTypesTupported)
  {
}
