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
package l9g.app6.sb3.saml2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ClientSecurityConfig
{
  private final static Logger LOGGER
    = LoggerFactory.getLogger(ClientSecurityConfig.class.getName());

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
  {
    LOGGER.debug("filterChain");
    http.authorizeHttpRequests(
      authorize -> authorize
        .requestMatchers(
          "/favicon.ico",
          "/webjars/**",
          "/actuator/**",
          "/images/**"
        ).permitAll()
        .anyRequest().authenticated())
      .saml2Login(withDefaults())
      .saml2Logout(withDefaults());

    return http.build();
  }
}
