package l9g.webapp.sb3oauth2client.config;

import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@HttpExchange(accept = "application/json", contentType = "application/json")
public interface WelcomeClient
{
  @GetExchange("/")
  String getWelcome();

  @GetExchange("/n/{name}")
  String getName(@PathVariable("name") String name);

  @GetExchange("/p")
  String getParamName(@RequestParam("name") String name);

  @GetExchange("/buildinfo")
  public Map<String, String> getBuildinfo();

  @GetExchange("/rscs-principal")
  public String getPrincipal();
}
