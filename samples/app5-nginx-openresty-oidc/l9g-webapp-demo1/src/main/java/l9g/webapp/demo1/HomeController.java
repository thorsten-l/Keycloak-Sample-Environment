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
package l9g.webapp.demo1;

import ch.qos.logback.core.joran.sanity.Pair;
import jakarta.servlet.http.HttpServletRequest;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Controller
public class HomeController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    HomeController.class.getName());

  @GetMapping(path = "/**", produces = MediaType.TEXT_PLAIN_VALUE)
  public String handleGetHome(
    HttpServletRequest request,
    @RequestParam(name = "name", required = false, defaultValue = "friend") String name,
    Model model)
  {
    LOGGER.debug("GET home " + request.getRemoteAddr());

    Enumeration<String> headerNames = request.getHeaderNames();

    final ArrayList<String> headerNamesList = new ArrayList<>();
    request.getHeaderNames().asIterator().forEachRemaining(
      n -> headerNamesList.add(n));
    Collections.sort(headerNamesList);

    final ArrayList<AbstractMap.SimpleEntry> sortedHeadersList
      = new ArrayList<>();
    headerNamesList.forEach(n -> sortedHeadersList.add(
      new AbstractMap.SimpleEntry(n, request.getHeader(n))));

    model.addAttribute("request", request);
    model.addAttribute("headers", sortedHeadersList);

    return "home";
  }
}
