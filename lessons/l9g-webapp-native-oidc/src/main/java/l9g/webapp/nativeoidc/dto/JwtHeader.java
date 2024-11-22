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
 * Represents the header section of a JWT (JSON Web Token).
 * This class is used to deserialize the JWT header from its JSON representation.
 *
 * <p>
 * The JWT header typically contains metadata about the token, such as the
 * algorithm used for signing the token, the type of the token, and a key ID
 * (kid) that can be used to identify the key used to sign the token.</p>
 *
 * <p>
 * Example JSON representation of a JWT header:</p>
 * <pre>
 * {
 *   "alg": "HS256",
 *   "typ": "JWT",
 *   "kid": "12345"
 * }
 * </pre>
 *
 * <p>
 * This class uses Jackson annotations to map JSON properties to Java fields.</p>
 *
 * @param algorithm The algorithm used to sign the JWT.
 * @param type The type of the token, typically "JWT".
 * @param kid The key ID used to identify the key used to sign the JWT.
 *
 * @see <a href="https://tools.ietf.org/html/rfc7519#section-5">RFC 7519 Section 5</a>
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record JwtHeader(
  @JsonProperty("alg")
  String algorithm,
  @JsonProperty("typ")
  String type,
  @JsonProperty("kid")
  String kid)
  {
}
