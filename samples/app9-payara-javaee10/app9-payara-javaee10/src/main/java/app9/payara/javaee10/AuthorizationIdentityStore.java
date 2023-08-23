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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import static jakarta.security.enterprise.identitystore.IdentityStore.ValidationType.PROVIDE_GROUPS;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@ApplicationScoped
public class AuthorizationIdentityStore implements IdentityStore
{
  private static final Logger LOGGER = Logger.getLogger(
    AuthorizationIdentityStore.class.getName());

  @Override
  public Set<ValidationType> validationTypes()
  {
    LOGGER.info("validationTypes");
    return EnumSet.of(PROVIDE_GROUPS);
  }

  @Override
  public Set<String> getCallerGroups(CredentialValidationResult validationResult)
  {
    LOGGER.info("getCallerGroups principal.class=" 
      + validationResult.getCallerPrincipal().getClass().getCanonicalName() );

    var principal = validationResult.getCallerPrincipal().getName();

    LOGGER.log(Level.INFO, "Get principal name in validation result: {0}",
      principal);    
    return Set.of("user");
  }
}
