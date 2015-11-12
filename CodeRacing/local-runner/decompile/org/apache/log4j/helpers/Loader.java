package org.apache.log4j.helpers;

import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

public class Loader
{
  private static boolean java1 = true;
  private static boolean ignoreTCL = false;
  
  public static URL getResource(String paramString)
  {
    ClassLoader localClassLoader = null;
    URL localURL = null;
    try
    {
      if ((!java1) && (!ignoreTCL))
      {
        localClassLoader = getTCL();
        if (localClassLoader != null)
        {
          LogLog.debug("Trying to find [" + paramString + "] using context classloader " + localClassLoader + ".");
          localURL = localClassLoader.getResource(paramString);
          if (localURL != null) {
            return localURL;
          }
        }
      }
      localClassLoader = Loader.class.getClassLoader();
      if (localClassLoader != null)
      {
        LogLog.debug("Trying to find [" + paramString + "] using " + localClassLoader + " class loader.");
        localURL = localClassLoader.getResource(paramString);
        if (localURL != null) {
          return localURL;
        }
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if (((localInvocationTargetException.getTargetException() instanceof InterruptedException)) || ((localInvocationTargetException.getTargetException() instanceof InterruptedIOException))) {
        Thread.currentThread().interrupt();
      }
      LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", localInvocationTargetException);
    }
    catch (Throwable localThrowable)
    {
      LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", localThrowable);
    }
    LogLog.debug("Trying to find [" + paramString + "] using ClassLoader.getSystemResource().");
    return ClassLoader.getSystemResource(paramString);
  }
  
  private static ClassLoader getTCL()
    throws IllegalAccessException, InvocationTargetException
  {
    Method localMethod = null;
    try
    {
      localMethod = Thread.class.getMethod("getContextClassLoader", null);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      return null;
    }
    return (ClassLoader)localMethod.invoke(Thread.currentThread(), null);
  }
  
  public static Class loadClass(String paramString)
    throws ClassNotFoundException
  {
    if ((java1) || (ignoreTCL)) {
      return Class.forName(paramString);
    }
    try
    {
      return getTCL().loadClass(paramString);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if (((localInvocationTargetException.getTargetException() instanceof InterruptedException)) || ((localInvocationTargetException.getTargetException() instanceof InterruptedIOException))) {
        Thread.currentThread().interrupt();
      }
    }
    catch (Throwable localThrowable) {}
    return Class.forName(paramString);
  }
  
  static
  {
    String str1 = OptionConverter.getSystemProperty("java.version", null);
    if (str1 != null)
    {
      int i = str1.indexOf('.');
      if ((i != -1) && (str1.charAt(i + 1) != '1')) {
        java1 = false;
      }
    }
    String str2 = OptionConverter.getSystemProperty("log4j.ignoreTCL", null);
    if (str2 != null) {
      ignoreTCL = OptionConverter.toBoolean(str2, true);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\helpers\Loader.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */