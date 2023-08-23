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
import jakarta.security.enterprise.identitystore.openid.OpenIdContext;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@WebServlet("/protected")
@ServletSecurity(
  @HttpConstraint(rolesAllowed = "user")
)
public class ProtectedServlet extends HttpServlet
{
  private static final long serialVersionUID = -5485979496887565898L;

  @Inject
  private OpenIdContext context;

  @Inject
  SecurityContext securityContext;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException
  {

    var principal = securityContext.getCallerPrincipal();
    var name = principal.getName();
    
    String html = """
            <div style="margin: 0 10%%; width: 80%%; overflow-wrap: anywhere;">
                <h1>Protected Servlet</h1>
                <p>principal name: %s </p>
                <p>access token (type = %s):</p>
                <p>%s</p>
                <p>preferred_username: %s</p>
                <p>roles: %s</p>
                <p>scope: %s</p>
                <p>claims:</p>
                <p>%s</p>
            </div>
            """.
      formatted(
        name,
        context.getTokenType(),
        context.getAccessToken(),
        context.getClaimsJson().get("preferred_username").toString(),
        context.getAccessToken().getClaim("groups"),
        context.getAccessToken().getClaim("scope"),
        context.getClaimsJson()
      );
    
    Map<String,Object> map = context.getAccessToken().getClaims();

    for( String k : map.keySet() )
    {
      System.out.println( k + " = " + map.get(k));
    }
    
    response.setContentType("text/html");
    response.getWriter().print(html.toString());
  }
}
