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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * The PKCE (Proof Key for Code Exchange) class provides methods to generate
 * a code verifier and a code challenge as part of the OAuth 2.0 PKCE extension.
 *
 * <p>
 * This class contains two main methods:
 * <ul>
 * <li>{@link #generateCodeVerifier()} - Generates a secure random code verifier.</li>
 * <li>{@link #generateCodeChallenge(String)} - Generates a code challenge based on the provided code verifier.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Usage example:
 * <pre>
 * {@code
 * String codeVerifier = PKCE.generateCodeVerifier();
 * String codeChallenge = PKCE.generateCodeChallenge(codeVerifier);
 * }
 * </pre>
 * </p>
 *
 * <p>
 * Note: The {@link #generateCodeChallenge(String)} method throws a {@link NoSuchAlgorithmException}
 * if the SHA-256 algorithm is not available.</p>
 *
 * <p>
 * Author: Thorsten Ludewig (t.ludewig@gmail.com)</p>
 */
public class PKCE
{
  private PKCE()
  {
  }

  public static String generateCodeVerifier()
  {
    SecureRandom secureRandom = new SecureRandom();
    byte[] randomBytes = new byte[32];
    secureRandom.nextBytes(randomBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
  }

  public static String generateCodeChallenge(String codeVerifier)
    throws NoSuchAlgorithmException
  {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
    return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
  }
}
