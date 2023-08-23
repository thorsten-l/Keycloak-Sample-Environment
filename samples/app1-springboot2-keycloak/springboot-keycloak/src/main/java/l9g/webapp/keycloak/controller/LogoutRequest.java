package l9g.webapp.keycloak.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Getter
@Setter
@ToString
public class LogoutRequest
{
  String logout_token;
}
