package l9g.webapp.sb3oauth2client.controller;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import org.bouncycastle.jce.spec.IESParameterSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController
{
  private final static Logger LOGGER = LoggerFactory.getLogger(
    AppController.class.getName());

  private static final String PRIVATE_KEY_FILENAME = "l9g-encrypted-user-password-client.pirvatekey";

  private static final String[] CONFIG_DIRS =
  {
    ".", "config", "data/config", "data/app8/config"
  };

  private static PrivateKey privateKey;

  static
  {
    File privateKeyFile = null;
    boolean fileExists = false;

    for (int i = 0; i < CONFIG_DIRS.length && !fileExists; i++)
    {
      privateKeyFile = new File(CONFIG_DIRS[i], PRIVATE_KEY_FILENAME);
      fileExists = privateKeyFile.exists() && privateKeyFile.canRead();
    }

    if (privateKeyFile != null && fileExists)
    {
      LOGGER.info("Loading private key file : " + privateKeyFile.
        getAbsolutePath());
      try (FileInputStream fis = new FileInputStream(privateKeyFile))
      {
        byte[] privateKeyBytes = new byte[fis.available()];
        fis.read(privateKeyBytes);
        fis.close();
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
        privateKey = keyFactory.generatePrivate(spec);
      }
      catch (Exception ex)
      {
        LOGGER.error("Loading private key failed!", ex);
      }
    }
    else
    {
      LOGGER.error("No private key file found! <{}>", PRIVATE_KEY_FILENAME);
    }
  }

  @GetMapping("/app")
  public String appGET(Model model,
    @AuthenticationPrincipal OAuth2User principal)
  {
    model.addAttribute("fullname", ((DefaultOidcUser) principal).getFullName());
    
    String encryptedPassword = principal.getAttribute("ENCRYPTED_USER_PASSWORD");
    
    LOGGER.debug("encryptedPassword={}",encryptedPassword);
    
    String plainPassword = "";
    
    try
    {
      Cipher cipher = Cipher.getInstance("ECIES", "BC");
      cipher.init(Cipher.DECRYPT_MODE, privateKey, new IESParameterSpec( null, null, 256, 256, null, false ));
      plainPassword 
        = new String(cipher.doFinal(Base64.getDecoder()
          .decode(encryptedPassword)));
    }
    catch (Exception ex)
    {
      LOGGER.debug( "decrypt failed", ex);
    }
   
    model.addAttribute("PLAIN_PASSWORD", plainPassword );
    
    return "app";
  }
}
