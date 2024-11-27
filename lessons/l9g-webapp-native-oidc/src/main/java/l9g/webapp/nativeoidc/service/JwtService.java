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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import l9g.webapp.nativeoidc.dto.JwksCerts;
import l9g.webapp.nativeoidc.dto.JwtHeader;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service class for handling JWT (JSON Web Token) operations such as decoding and validating signatures.
 * This class provides methods to split, decode, and validate JWT tokens using various algorithms.
 *
 * <p>
 * It supports RS256 and HS512 algorithms for signature validation.</p>
 *
 * <p>
 * Methods:</p>
 * <ul>
 * <li>{@link #splitJwt(String)} - Splits a JWT token into its constituent parts.</li>
 * <li>{@link #decodeJwtPayload(String)} - Decodes the payload of a JWT token and returns it as a Map.</li>
 * <li>{@link #validateJwtSignature(String)} - Validates the signature of a JWT token.</li>
 * <li>{@link #getPublicKeyFromJwks(JwksCerts, String)} - Retrieves the public key from JWKS (JSON Web Key Set) using the key ID.</li>
 * <li>{@link #validateRs256Signature(String, String)} - Validates the RS256 signature of a JWT token.</li>
 * <li>{@link #validateHs512Signature(String)} - Validates the HS512 signature of a JWT token (currently returns true).</li>
 * </ul>
 *
 * <p>
 * Note: The HS512 signature validation method is currently a stub and always returns true.</p>
 *
 * <p>
 * Author: Thorsten Ludewig (t.ludewig@gmail.com)</p>
 */
@Service
@Slf4j
@Getter
public class JwtService
{
  @Setter
  private JwksCerts oauth2JwksCerts;

  public String[] splitJwt(String jwt)
  {
    String[] parts = jwt.split("\\.");
    if(parts.length != 3)
    {
      throw new IllegalArgumentException("Ungültiges JWT-Format");
    }
    return parts;
  }

  // decode Jwt ///////////////////////////////////////////////////////////
  public Map<String, String> decodeJwtPayload(String jwt)
  {
    try
    {
      String[] parts = splitJwt(jwt);

      String payload = parts[1];

      byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
      String decodedPayload = new String(decodedBytes);

      // JSON in Map umwandeln
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(decodedPayload, HashMap.class);
    }
    catch(Exception e)
    {
      throw new RuntimeException("Fehler beim Decodieren des JWT-Tokens", e);
    }
  }

  // validate Jwt ///////////////////////////////////////////////////////////
  public boolean validateJwtSignature(String jwt)
  {
    String[] parts = splitJwt(jwt);
    try
    {
      ObjectMapper mapper = new ObjectMapper();
      JwtHeader jwtHeader = mapper.readValue(
        new String(Base64.getUrlDecoder().decode(parts[0])), JwtHeader.class);

      log.debug("jwt header = {}", jwtHeader);

      if(null == jwtHeader.algorithm())
      {
        log.error("Unsupported algorithm: {}", jwtHeader.algorithm());
        return false;
      }
      else
      {
        switch(jwtHeader.algorithm())
        {
          case "RS256":
            return validateRs256Signature(jwt, jwtHeader.kid());
          case "HS512":
            return validateHs512Signature(jwt);
          default:
            log.error("Unsupported algorithm: {}", jwtHeader.algorithm());
            return false;
        }
      }
    }
    catch(Throwable t)
    {
      log.error("ERROR: {}", t.getMessage());
      t.printStackTrace();
      return false;
    }
  }

  /////////////////////////////////////////////////////////////////////////////
  private RSAPublicKey getPublicKeyFromJwks(JwksCerts jwksCerts, String kid)
    throws Exception
  {
    for(JwksCerts.JwksKey key : jwksCerts.keys())
    {
      log.debug("key={}", key);
      if(key.algorithm().equals("RS256"))
      {
        String x509Cert = key.x509CertificateChain().get(0);
        byte[] decodedCert = Base64.getDecoder().decode(x509Cert);
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        X509Certificate certificate =
          (X509Certificate)factory.generateCertificate(
            new java.io.ByteArrayInputStream(decodedCert));
        return (RSAPublicKey)certificate.getPublicKey();
      }
    }
    throw new IllegalArgumentException(
      "Public key with kid=" + kid + " not found");
  }

  private boolean validateRs256Signature(String jwt, String keyId)
  {
    try
    {
      String[] parts = splitJwt(jwt);
      String data = parts[0] + "." + parts[1];
      byte[] signatureBytes = Base64.getUrlDecoder().decode(parts[2]);

      // Öffentlichen Schlüssel abrufen
      RSAPublicKey publicKey = getPublicKeyFromJwks(oauth2JwksCerts, keyId);
      if(publicKey == null)
      {
        log.error("Public key with kid={} not found", keyId);
        return false;
      }

      // Signatur validieren
      Signature signature = Signature.getInstance("SHA256withRSA");
      signature.initVerify(publicKey);
      signature.update(data.getBytes(StandardCharsets.UTF_8));
      return signature.verify(signatureBytes);
    }
    catch(Throwable t)
    {
      log.error("ERROR in RS256 validation: {}", t.getMessage());
      t.printStackTrace();
      return false;
    }
  }

  private boolean validateHs512Signature(String jwt)
  {
    return true;
    /*
    String[] parts = splitJwt(jwt);
    
    log.debug("jwt={}",jwt);
    
    try
    {
      String data = parts[0] + "." + parts[1];

      log.debug("Header (Base64): {}", parts[0]);
      log.debug("Payload (Base64): {}", parts[1]);
      log.debug("Signature (Base64): {}", parts[2]);

      String headerDecoded = new String(
        Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);

      log.debug("Header (Decoded): {}", headerDecoded);
      log.debug("oauth2ClientSecret: {}", oauth2ClientSecret);
      log.debug("data: {}", data);

      byte[] signatureBytes = Base64.getUrlDecoder().decode(parts[2]);

      // ---------------------------------------------------------------------

      Mac mac = Mac.getInstance("HmacSHA512");
      
      mac.init(new SecretKeySpec(
        oauth2ClientSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
      
      byte[] calculatedSignature =
        mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

      // ---------------------------------------------------------------------
      
      log.debug("Calculated Signature (Base64): {}",
        Base64.getUrlEncoder().withoutPadding()
          .encodeToString(calculatedSignature));
      
      log.debug("Received Signature (Base64): {}",
        Base64.getUrlEncoder().withoutPadding()
          .encodeToString(signatureBytes));

      return MessageDigest.isEqual(signatureBytes, calculatedSignature);
    }
    catch(Throwable t)
    {
      log.error("ERROR in HS512 validation: {}", t.getMessage());
      t.printStackTrace();
      return false;
    }
     */
  }

}
