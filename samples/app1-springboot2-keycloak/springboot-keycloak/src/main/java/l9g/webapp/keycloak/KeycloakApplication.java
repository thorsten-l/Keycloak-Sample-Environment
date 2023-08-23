package l9g.webapp.keycloak;

import l9g.webapp.keycloak.config.BuildProperties;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import l9g.webapp.keycloak.config.Options;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KeycloakApplication
{
  private final static String CONFIG_NAME = "config.properties";

  private final static Logger LOGGER = LoggerFactory.getLogger(
    KeycloakApplication.class.getName());

  private final static Options OPTIONS = new Options();

  private static void loadConfig(String configFileName) throws IOException
  {
    Properties configProperties = new Properties();
    configProperties.load(new FileReader(configFileName));
    configProperties.forEach((k, v) ->
    {
      LOGGER.debug("{}={}", k, v);
      System.getProperties().setProperty(k.toString(), v.toString());
    });
  }

  public static void main(String[] args) throws IOException
  {
    BuildProperties build = BuildProperties.getInstance();

    CmdLineParser parser;

    parser = new CmdLineParser(OPTIONS);

    try
    {
      parser.parseArgument(args);
    }
    catch (CmdLineException ex)
    {
      LOGGER.error("Command line error\n");
      parser.printUsage(System.out);
      System.exit(-1);
    }

    if (OPTIONS.isDisplayHelp())
    {
      System.out.println("\nUsage: ./springboot-keycloak.jar [options]\n");
      parser.printUsage(System.out);
      System.exit(0);
    }

    if (OPTIONS.isDisplayVersion())
    {
      System.out.println("Project Name    : " + build.getProjectName());
      System.out.println("Project Version : " + build.getProjectVersion());
      System.out.println("Build Timestamp : " + build.getTimestamp());
      System.out.println("Build Profile   : " + build.getProfile() + "\n");
      System.exit(0);
    }

    if (OPTIONS.getConfigFilename() != null
      && OPTIONS.getConfigFilename().trim().length() > 0)
    {
      loadConfig(OPTIONS.getConfigFilename());
    }
    else
    {
      loadConfig(CONFIG_NAME);
    }

    SpringApplication.run(KeycloakApplication.class, args);
  }

}
