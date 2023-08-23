package l9g.webapp.keycloak.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@ToString
public class BuildProperties
{  
  private final static BuildProperties SINGLETON = new BuildProperties();
  private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(
    BuildProperties.class.getName());
  
  private BuildProperties()
  {
    Properties p = new Properties();
    InputStream is = this.getClass().getResourceAsStream("/build.properties");
    try
    {
      p.load(is);
      javaVersion = p.getProperty("build.java.version");
      javaVendor = p.getProperty("build.java.vendor");
      projectName = p.getProperty("build.project.name");
      projectVersion = p.getProperty("build.project.version");
      timestamp = p.getProperty("build.timestamp");
      profile = p.getProperty("build.profile");
    }
    catch (IOException ex)
    {
      LOGGER.error( "Can't load properties. ", ex );
    }
  }
  
  public static BuildProperties getInstance()
  {
    return SINGLETON;
  }
  
  @Getter
  private String javaVersion;

  @Getter
  private String javaVendor;

  @Getter
  private String projectName;

  @Getter
  private String projectVersion;

  @Getter
  private String timestamp;
  
  @Getter
  private String profile;
}
