package org.apache.log4j.helpers;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;

public class OptionConverter
{
  static String DELIM_START = "${";
  static char DELIM_STOP = '}';
  static int DELIM_START_LEN = 2;
  static int DELIM_STOP_LEN = 1;
  
  public static String getSystemProperty(String paramString1, String paramString2)
  {
    try
    {
      return System.getProperty(paramString1, paramString2);
    }
    catch (Throwable localThrowable)
    {
      LogLog.debug("Was not allowed to read system property \"" + paramString1 + "\".");
    }
    return paramString2;
  }
  
  public static Object instantiateByKey(Properties paramProperties, String paramString, Class paramClass, Object paramObject)
  {
    String str = findAndSubst(paramString, paramProperties);
    if (str == null)
    {
      LogLog.error("Could not find value for key " + paramString);
      return paramObject;
    }
    return instantiateByClassName(str.trim(), paramClass, paramObject);
  }
  
  public static boolean toBoolean(String paramString, boolean paramBoolean)
  {
    if (paramString == null) {
      return paramBoolean;
    }
    String str = paramString.trim();
    if ("true".equalsIgnoreCase(str)) {
      return true;
    }
    if ("false".equalsIgnoreCase(str)) {
      return false;
    }
    return paramBoolean;
  }
  
  public static Level toLevel(String paramString, Level paramLevel)
  {
    if (paramString == null) {
      return paramLevel;
    }
    paramString = paramString.trim();
    int i = paramString.indexOf('#');
    if (i == -1)
    {
      if ("NULL".equalsIgnoreCase(paramString)) {
        return null;
      }
      return Level.toLevel(paramString, paramLevel);
    }
    Level localLevel = paramLevel;
    String str1 = paramString.substring(i + 1);
    String str2 = paramString.substring(0, i);
    if ("NULL".equalsIgnoreCase(str2)) {
      return null;
    }
    LogLog.debug("toLevel:class=[" + str1 + "]" + ":pri=[" + str2 + "]");
    try
    {
      Class localClass = Loader.loadClass(str1);
      Class[] arrayOfClass = { String.class, Level.class };
      Method localMethod = localClass.getMethod("toLevel", arrayOfClass);
      Object[] arrayOfObject = { str2, paramLevel };
      Object localObject = localMethod.invoke(null, arrayOfObject);
      localLevel = (Level)localObject;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      LogLog.warn("custom level class [" + str1 + "] not found.");
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      LogLog.warn("custom level class [" + str1 + "]" + " does not have a class function toLevel(String, Level)", localNoSuchMethodException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if (((localInvocationTargetException.getTargetException() instanceof InterruptedException)) || ((localInvocationTargetException.getTargetException() instanceof InterruptedIOException))) {
        Thread.currentThread().interrupt();
      }
      LogLog.warn("custom level class [" + str1 + "]" + " could not be instantiated", localInvocationTargetException);
    }
    catch (ClassCastException localClassCastException)
    {
      LogLog.warn("class [" + str1 + "] is not a subclass of org.apache.log4j.Level", localClassCastException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      LogLog.warn("class [" + str1 + "] cannot be instantiated due to access restrictions", localIllegalAccessException);
    }
    catch (RuntimeException localRuntimeException)
    {
      LogLog.warn("class [" + str1 + "], level [" + str2 + "] conversion failed.", localRuntimeException);
    }
    return localLevel;
  }
  
  public static String findAndSubst(String paramString, Properties paramProperties)
  {
    String str = paramProperties.getProperty(paramString);
    if (str == null) {
      return null;
    }
    try
    {
      return substVars(str, paramProperties);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      LogLog.error("Bad option value [" + str + "].", localIllegalArgumentException);
    }
    return str;
  }
  
  public static Object instantiateByClassName(String paramString, Class paramClass, Object paramObject)
  {
    if (paramString != null) {
      try
      {
        Class localClass = Loader.loadClass(paramString);
        if (!paramClass.isAssignableFrom(localClass))
        {
          LogLog.error("A \"" + paramString + "\" object is not assignable to a \"" + paramClass.getName() + "\" variable.");
          LogLog.error("The class \"" + paramClass.getName() + "\" was loaded by ");
          LogLog.error("[" + paramClass.getClassLoader() + "] whereas object of type ");
          LogLog.error("\"" + localClass.getName() + "\" was loaded by [" + localClass.getClassLoader() + "].");
          return paramObject;
        }
        return localClass.newInstance();
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        LogLog.error("Could not instantiate class [" + paramString + "].", localClassNotFoundException);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        LogLog.error("Could not instantiate class [" + paramString + "].", localIllegalAccessException);
      }
      catch (InstantiationException localInstantiationException)
      {
        LogLog.error("Could not instantiate class [" + paramString + "].", localInstantiationException);
      }
      catch (RuntimeException localRuntimeException)
      {
        LogLog.error("Could not instantiate class [" + paramString + "].", localRuntimeException);
      }
    }
    return paramObject;
  }
  
  public static String substVars(String paramString, Properties paramProperties)
    throws IllegalArgumentException
  {
    StringBuffer localStringBuffer = new StringBuffer();
    int k;
    for (int i = 0;; i = k + DELIM_STOP_LEN)
    {
      int j = paramString.indexOf(DELIM_START, i);
      if (j == -1)
      {
        if (i == 0) {
          return paramString;
        }
        localStringBuffer.append(paramString.substring(i, paramString.length()));
        return localStringBuffer.toString();
      }
      localStringBuffer.append(paramString.substring(i, j));
      k = paramString.indexOf(DELIM_STOP, j);
      if (k == -1) {
        throw new IllegalArgumentException('"' + paramString + "\" has no closing brace. Opening brace at position " + j + '.');
      }
      j += DELIM_START_LEN;
      String str1 = paramString.substring(j, k);
      String str2 = getSystemProperty(str1, null);
      if ((str2 == null) && (paramProperties != null)) {
        str2 = paramProperties.getProperty(str1);
      }
      if (str2 != null)
      {
        String str3 = substVars(str2, paramProperties);
        localStringBuffer.append(str3);
      }
    }
  }
  
  public static void selectAndConfigure(URL paramURL, String paramString, LoggerRepository paramLoggerRepository)
  {
    Object localObject = null;
    String str = paramURL.getFile();
    if ((paramString == null) && (str != null) && (str.endsWith(".xml"))) {
      paramString = "org.apache.log4j.xml.DOMConfigurator";
    }
    if (paramString != null)
    {
      LogLog.debug("Preferred configurator class: " + paramString);
      localObject = (Configurator)instantiateByClassName(paramString, Configurator.class, null);
      if (localObject == null) {
        LogLog.error("Could not instantiate configurator [" + paramString + "].");
      }
    }
    else
    {
      localObject = new PropertyConfigurator();
    }
    ((Configurator)localObject).doConfigure(paramURL, paramLoggerRepository);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\helpers\OptionConverter.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */