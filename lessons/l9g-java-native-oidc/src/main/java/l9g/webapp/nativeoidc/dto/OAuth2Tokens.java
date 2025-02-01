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

/**
 * A record representing OAuth2 tokens.
 * <p>
 * This record is used to encapsulate the various tokens received from an OAuth2
 * authorization server, including the access token, ID token, refresh token,
 * token type, and the expiration time of the access token.
 * </p>
 * <p>
 * The fields are annotated with {@link JsonProperty} to map the JSON properties
 * to the corresponding fields in this record.
 * </p>
 *
 * @param accessToken The access token issued by the authorization server.
 * @param idToken The ID token issued by the authorization server.
 * @param refreshToken The refresh token issued by the authorization server.
 * @param tokenType The type of the token issued by the authorization server.
 * @param expiresIn The lifetime in seconds of the access token.
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuth2Tokens(
  @JsonProperty("access_token")
  String accessToken,
  @JsonProperty("id_token")
  String idToken,
  @JsonProperty("refresh_token")
  String refreshToken,
  @JsonProperty("token_type")
  String tokenType,
  @JsonProperty("expires_in")
  int expiresIn)
  {
}
