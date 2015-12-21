package org.apache.log4j.helpers;

import java.io.PrintStream;

public class LogLog
{
  protected static boolean debugEnabled = false;
  private static boolean quietMode = false;
  
  public static void setInternalDebugging(boolean paramBoolean)
  {
    debugEnabled = paramBoolean;
  }
  
  public static void debug(String paramString)
  {
    if ((debugEnabled) && (!quietMode)) {
      System.out.println("log4j: " + paramString);
    }
  }
  
  public static void debug(String paramString, Throwable paramThrowable)
  {
    if ((debugEnabled) && (!quietMode))
    {
      System.out.println("log4j: " + paramString);
      if (paramThrowable != null) {
        paramThrowable.printStackTrace(System.out);
      }
    }
  }
  
  public static void error(String paramString)
  {
    if (quietMode) {
      return;
    }
    System.err.println("log4j:ERROR " + paramString);
  }
  
  public static void error(String paramString, Throwable paramThrowable)
  {
    if (quietMode) {
      return;
    }
    System.err.println("log4j:ERROR " + paramString);
    if (paramThrowable != null) {
      paramThrowable.printStackTrace();
    }
  }
  
  public static void warn(String paramString)
  {
    if (quietMode) {
      return;
    }
    System.err.println("log4j:WARN " + paramString);
  }
  
  public static void warn(String paramString, Throwable paramThrowable)
  {
    if (quietMode) {
      return;
    }
    System.err.println("log4j:WARN " + paramString);
    if (paramThrowable != null) {
      paramThrowable.printStackTrace();
    }
  }
  
  static
  {
    String str = OptionConverter.getSystemProperty("log4j.debug", null);
    if (str == null) {
      str = OptionConverter.getSystemProperty("log4j.configDebug", null);
    }
    if (str != null) {
      debugEnabled = OptionConverter.toBoolean(str, true);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\helpers\LogLog.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */