package l9g.webapp.keycloak.config;

//~--- non-JDK imports --------------------------------------------------------

import lombok.Getter;
import lombok.Setter;

import org.kohsuke.args4j.Option;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
public class Options
{

  /**
   * Field description
   */
  @Option(
    name = "--help",
    aliases = "-h",
    usage = "Displays this help"
  )
  @Getter
  @Setter
  private boolean displayHelp = false;

  /**
   * Field description
   */
  @Option(
    name = "--version",
    aliases = "-v",
    usage = "Display programm version"
  )
  @Getter
  @Setter
  private boolean displayVersion = false;


  
  /**
   * Field description
   */
  @Option(
    name = "--config",
    aliases = "-c",
    usage = "config.properties filename"
  )
  @Getter
  @Setter
  private String configFilename = null;
}
