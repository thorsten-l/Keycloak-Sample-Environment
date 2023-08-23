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

import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@WebServlet("/username")
@ServletSecurity(
  @HttpConstraint(rolesAllowed =
  {
    "user"
}))
public class UserNameServlet extends HttpServlet
{
  private static final Logger LOGGER = Logger.getLogger(UserNameServlet.class.
    getName());

  private static final long serialVersionUID = -8864471503202001679L;

  @Inject
  SecurityContext securityContext;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException
  {
    String nameInRequest = request.getUserPrincipal() != null ? request.
      getUserPrincipal().getName() : "";

    var principal = securityContext.getCallerPrincipal();

    LOGGER.log(Level.INFO, "Principal: {0}", principal);
    var name = principal.getName();

    response.setContentType("text/html");
    response.getWriter().println("<h1>UserName Servlet</h1>");
    response.getWriter().println("<p>principal name in request userPrincipal:"
      + nameInRequest + "</p>");
    response.getWriter().println("<p>principal name:" + name + "</p>");
    response.getWriter().println("<p>isCallerInRole('user'):"
      + securityContext.isCallerInRole("user") + "</p>");
    response.getWriter().println("<p>isCallerInRole('foo'):"
      + securityContext.isCallerInRole("foo") + "</p>");
  }
}
