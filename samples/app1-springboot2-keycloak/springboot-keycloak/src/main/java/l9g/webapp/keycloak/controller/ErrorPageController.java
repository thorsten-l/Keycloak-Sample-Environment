package l9g.webapp.keycloak.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@Controller
public class ErrorPageController implements ErrorController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    ErrorPageController.class.getName());

  @RequestMapping("/error")
  public String handleError(HttpServletRequest request, Model model)
  {
    ControllerUtil.addGeneralAttributes(model);
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
    
    LOGGER.debug("handleError = {}", status );

    if (status != null)
    {
      Integer statusCode = Integer.valueOf(status.toString());

      model.addAttribute("pageErrorException", request.getAttribute(
        RequestDispatcher.ERROR_EXCEPTION));
      model.addAttribute("pageErrorExceptionType", request.getAttribute(
        RequestDispatcher.ERROR_EXCEPTION_TYPE));
      model.addAttribute("pageErrorMessage", request.getAttribute(
        RequestDispatcher.ERROR_MESSAGE));
      model.addAttribute("pageErrorRequestUri", request.getAttribute(
        RequestDispatcher.ERROR_REQUEST_URI));
      model.addAttribute("pageErrorServletName", request.getAttribute(
        RequestDispatcher.ERROR_SERVLET_NAME));
      model.addAttribute("pageErrorStatusCode", request.getAttribute(
        RequestDispatcher.ERROR_STATUS_CODE));

      if (statusCode == HttpStatus.NOT_FOUND.value())
      {
        return "error/404";
      }
      else if (statusCode == HttpStatus.FORBIDDEN.value())
      {
        return "error/403";
      }
      else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value())
      {
        return "error/500";
      }
    }
    
    return "error/error";
  }
}
