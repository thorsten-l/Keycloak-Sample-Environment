/*
 * Copyright 2022 Thorsten Ludewig (t.ludewig@gmail.com).
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
package l9g.webapp.sb31oauth2client.controller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.AddressStandardClaim;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    AppController.class.getName());

  private Map<String, Object> createValueMapFromGetters(Object object)
  {
    Map<String, Object> map = new HashMap<>();
    Method[] methods = object.getClass().getMethods();

    for (Method method : methods)
    {
      String name = method.getName();
      if (name.startsWith("get") && method.getParameterCount() == 0)
      {
        try
        {
          LOGGER.debug("method "+name);
          map.put(name.substring(3), method.invoke(object));
        }
        catch (Throwable ex)
        {
          LOGGER.error("ERROR: invoking method");
        }
      }
    }
    
    return map;
  }

  private Map<String, Object> sortKeyStringMap(Map<String, Object> map)
  {
    Map<String, Object> sortedMap = new LinkedHashMap<>();

    String[] keys = map.keySet().toArray(String[]::new);
    Arrays.sort(keys);
    
    for (String key : keys)
    {
      sortedMap.put(key, map.get(key));
    }

    return sortedMap;
  }

  @GetMapping("/")
  public String appGET(Model model,
    @AuthenticationPrincipal DefaultOidcUser principal)
  {
    LOGGER.debug("appGET");

    model.addAttribute("fullname", principal.getFullName());
    model.addAttribute("issuer", principal.getIssuer().toExternalForm());
    model.addAttribute("principal", principal);
    model.addAttribute("pgetter", sortKeyStringMap(createValueMapFromGetters(principal)));
    model.addAttribute("pclaims", sortKeyStringMap(principal.getClaims()));
    model.addAttribute("pattributes", sortKeyStringMap(principal.getAttributes()));
    model.addAttribute("paddress", sortKeyStringMap(createValueMapFromGetters(principal.getAddress())));
    return "app";
  }
}
