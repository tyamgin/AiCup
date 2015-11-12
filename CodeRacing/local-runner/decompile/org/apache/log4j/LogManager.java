package org.apache.log4j;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.NOPLoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;

public class LogManager
{
  private static Object guard = null;
  private static RepositorySelector repositorySelector;
  
  private static boolean isLikelySafeScenario(Exception paramException)
  {
    StringWriter localStringWriter = new StringWriter();
    paramException.printStackTrace(new PrintWriter(localStringWriter));
    String str = localStringWriter.toString();
    return str.indexOf("org.apache.catalina.loader.WebappClassLoader.stop") != -1;
  }
  
  public static LoggerRepository getLoggerRepository()
  {
    if (repositorySelector == null)
    {
      repositorySelector = new DefaultRepositorySelector(new NOPLoggerRepository());
      guard = null;
      IllegalStateException localIllegalStateException = new IllegalStateException("Class invariant violation");
      String str = "log4j called after unloading, see http://logging.apache.org/log4j/1.2/faq.html#unload.";
      if (isLikelySafeScenario(localIllegalStateException)) {
        LogLog.debug(str, localIllegalStateException);
      } else {
        LogLog.error(str, localIllegalStateException);
      }
    }
    return repositorySelector.getLoggerRepository();
  }
  
  public static Logger getRootLogger()
  {
    return getLoggerRepository().getRootLogger();
  }
  
  public static Logger getLogger(String paramString)
  {
    return getLoggerRepository().getLogger(paramString);
  }
  
  static
  {
    Hierarchy localHierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
    repositorySelector = new DefaultRepositorySelector(localHierarchy);
    String str1 = OptionConverter.getSystemProperty("log4j.defaultInitOverride", null);
    if ((str1 == null) || ("false".equalsIgnoreCase(str1)))
    {
      String str2 = OptionConverter.getSystemProperty("log4j.configuration", null);
      String str3 = OptionConverter.getSystemProperty("log4j.configuratorClass", null);
      URL localURL = null;
      if (str2 == null)
      {
        localURL = Loader.getResource("log4j.xml");
        if (localURL == null) {
          localURL = Loader.getResource("log4j.properties");
        }
      }
      else
      {
        try
        {
          localURL = new URL(str2);
        }
        catch (MalformedURLException localMalformedURLException)
        {
          localURL = Loader.getResource(str2);
        }
      }
      if (localURL != null)
      {
        LogLog.debug("Using URL [" + localURL + "] for automatic log4j configuration.");
        try
        {
          OptionConverter.selectAndConfigure(localURL, str3, getLoggerRepository());
        }
        catch (NoClassDefFoundError localNoClassDefFoundError)
        {
          LogLog.warn("Error during default initialization", localNoClassDefFoundError);
        }
      }
      else
      {
        LogLog.debug("Could not find resource: [" + str2 + "].");
      }
    }
    else
    {
      LogLog.debug("Default initialization of overridden by log4j.defaultInitOverrideproperty.");
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\LogManager.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */