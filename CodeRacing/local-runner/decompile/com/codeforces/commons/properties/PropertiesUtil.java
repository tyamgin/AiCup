package com.codeforces.commons.properties;

import com.codeforces.commons.resource.CantReadResourceException;
import com.codeforces.commons.text.Patterns;
import com.codeforces.commons.text.StringUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class PropertiesUtil
{
  private static final Logger logger = Logger.getLogger(PropertiesUtil.class);
  private static final ConcurrentMap propertiesByResourceName = new ConcurrentHashMap();
  
  private PropertiesUtil()
  {
    throw new UnsupportedOperationException();
  }
  
  public static String getProperty(boolean paramBoolean, String paramString1, String paramString2, String... paramVarArgs)
    throws CantReadResourceException
  {
    for (String str1 : paramVarArgs)
    {
      Properties localProperties;
      try
      {
        localProperties = ensurePropertiesByResourceName(str1);
      }
      catch (IOException localIOException)
      {
        String str3 = String.format("Can't read properties from resource '%s'.", new Object[] { str1 });
        if (paramBoolean)
        {
          logger.error(str3, localIOException);
          throw new CantReadResourceException(str3, localIOException);
        }
        logger.warn(str3, localIOException);
        continue;
      }
      String str2 = localProperties.getProperty(paramString1);
      if (str2 != null) {
        return str2;
      }
    }
    return paramString2;
  }
  
  public static String getPropertyQuietly(String paramString1, String paramString2, String... paramVarArgs)
  {
    return getProperty(false, paramString1, paramString2, paramVarArgs);
  }
  
  public static List getListProperty(boolean paramBoolean, String paramString1, String paramString2, String... paramVarArgs)
  {
    String str = getProperty(paramBoolean, paramString1, paramString2, paramVarArgs);
    if (StringUtil.isBlank(str)) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(Arrays.asList(Patterns.SEMICOLON_PATTERN.split(str)));
  }
  
  public static List getListPropertyQuietly(String paramString1, String paramString2, String... paramVarArgs)
  {
    return getListProperty(false, paramString1, paramString2, paramVarArgs);
  }
  
  private static Properties ensurePropertiesByResourceName(String paramString)
    throws IOException
  {
    Properties localProperties = (Properties)propertiesByResourceName.get(paramString);
    if (localProperties == null)
    {
      localProperties = new Properties();
      InputStream localInputStream = PropertiesUtil.class.getResourceAsStream(paramString);
      Object localObject1 = null;
      try
      {
        if (localInputStream != null) {
          localProperties.load(new InputStreamReader(localInputStream, StandardCharsets.UTF_8));
        } else {
          logger.warn(String.format("Can't find resource file '%s'.", new Object[] { paramString }));
        }
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = localThrowable2;
        throw localThrowable2;
      }
      finally
      {
        if (localInputStream != null) {
          if (localObject1 != null) {
            try
            {
              localInputStream.close();
            }
            catch (Throwable localThrowable3)
            {
              ((Throwable)localObject1).addSuppressed(localThrowable3);
            }
          } else {
            localInputStream.close();
          }
        }
      }
      propertiesByResourceName.putIfAbsent(paramString, localProperties);
      localProperties = (Properties)propertiesByResourceName.get(paramString);
    }
    return localProperties;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\properties\PropertiesUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */