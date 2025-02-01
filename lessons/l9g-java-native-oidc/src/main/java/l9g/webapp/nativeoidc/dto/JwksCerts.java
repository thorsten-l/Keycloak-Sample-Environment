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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a collection of JSON Web Key Set (JWKS) certificates.
 * This class is used to deserialize the JWKS response from an OpenID Connect provider.
 *
 * @param keys A list of JSON Web Keys (JWKs).
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
public record JwksCerts(
  List<JwksKey> keys)
  {
  /**
   * Represents a JSON Web Key (JWK) used in the JWKS response.
   *
   * @param keyId The unique identifier for the key.
   * @param keyType The cryptographic algorithm family used with the key.
   * @param algorithm The specific algorithm used with the key.
   * @param keyUsage The intended use of the key (e.g., signature, encryption).
   * @param modulus The modulus value for RSA keys.
   * @param exponent The exponent value for RSA keys.
   * @param x509CertificateChain The X.509 certificate chain corresponding to the key.
   * @param x509CertificateThumbprint The thumbprint of the X.509 certificate.
   * @param x509CertificateSha256Thumbprint The SHA-256 thumbprint of the X.509 certificate.
   */
  public record JwksKey(
    @JsonProperty("kid")
    String keyId,
    @JsonProperty("kty")
    String keyType,
    @JsonProperty("alg")
    String algorithm,
    @JsonProperty("use")
    String keyUsage,
    @JsonProperty("n")
    String modulus,
    @JsonProperty("e")
    String exponent,
    @JsonProperty("x5c")
    List<String> x509CertificateChain,
    @JsonProperty("x5t")
    String x509CertificateThumbprint,
    @JsonProperty("x5t#S256")
    String x509CertificateSha256Thumbprint)
    {
  }

}
