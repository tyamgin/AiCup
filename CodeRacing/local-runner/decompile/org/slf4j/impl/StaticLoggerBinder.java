package org.slf4j.impl;

import org.apache.log4j.Level;
import org.slf4j.ILoggerFactory;
import org.slf4j.helpers.Util;

public class StaticLoggerBinder
{
  private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();
  public static String REQUESTED_API_VERSION = "1.6.99";
  private static final String loggerFactoryClassStr = Log4jLoggerFactory.class.getName();
  private final ILoggerFactory loggerFactory = new Log4jLoggerFactory();
  
  public static final StaticLoggerBinder getSingleton()
  {
    return SINGLETON;
  }
  
  private StaticLoggerBinder()
  {
    try
    {
      Level localLevel = Level.TRACE;
    }
    catch (NoSuchFieldError localNoSuchFieldError)
    {
      Util.report("This version of SLF4J requires log4j version 1.2.12 or later. See also http://www.slf4j.org/codes.html#log4j_version");
    }
  }
  
  public ILoggerFactory getLoggerFactory()
  {
    return this.loggerFactory;
  }
  
  public String getLoggerFactoryClassStr()
  {
    return loggerFactoryClassStr;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\slf4j\impl\StaticLoggerBinder.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */