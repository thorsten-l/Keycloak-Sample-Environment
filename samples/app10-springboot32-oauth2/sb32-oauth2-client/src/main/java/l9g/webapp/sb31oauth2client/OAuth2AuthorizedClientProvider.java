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
package l9g.webapp.sb31oauth2client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Component
public class OAuth2AuthorizedClientProvider
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    OAuth2AuthorizedClientProvider.class.getName());

  @Autowired
  private OAuth2AuthorizedClientService clientService;

  public OAuth2AuthorizedClient getClient()
  {
    LOGGER.debug("getClient");

    OAuth2AuthenticationToken oauthToken
      = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().
        getAuthentication();
    
    return clientService.loadAuthorizedClient(
      oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());
  }
}
