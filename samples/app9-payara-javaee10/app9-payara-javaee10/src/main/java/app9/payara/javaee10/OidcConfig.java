/*
 * Copyright 2023 Thorsten Ludewig (t.ludewig@gmail.com).
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
package app9.payara.javaee10;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@OpenIdAuthenticationMechanismDefinition(
  providerURI = "${oidcConfig.issuerUri}",
  clientId = "${oidcConfig.clientId}",
  clientSecret = "${oidcConfig.clientSecret}",
  redirectURI = "${baseURL}/callback",
  scope = {"openid", "email", "profile", "roles"},
  jwksReadTimeout = 5000
)

@ApplicationScoped
@Named("oidcConfig")
public class OidcConfig
{

  private static final Logger LOGGER = Logger.getLogger(OidcConfig.class.
    getName());

  private String domain;

  private String clientId;

  private String clientSecret;

  private String issuerUri;

  @PostConstruct
  void init()
  {
    try
    {
      var properties = new Properties();
      properties.load(getClass().getResourceAsStream("/oidc.properties"));
      domain = properties.getProperty("domain");
      clientId = properties.getProperty("clientId");
      clientSecret = properties.getProperty("clientSecret");
      issuerUri = properties.getProperty("issuerUri");
    }
    catch (IOException e)
    {
      LOGGER.log(Level.SEVERE, "Failed to load oidc.properties", e);
    }
  }

  public String getDomain()
  {
    return domain;
  }

  public String getClientId()
  {
    return clientId;
  }

  public String getClientSecret()
  {
    return clientSecret;
  }

  public String getIssuerUri()
  {
    return issuerUri;
  }
}
