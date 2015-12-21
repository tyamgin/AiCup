package org.apache.log4j.spi;

import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public final class NOPLogger
  extends Logger
{
  public NOPLogger(NOPLoggerRepository paramNOPLoggerRepository, String paramString)
  {
    super(paramString);
    this.repository = paramNOPLoggerRepository;
    this.level = Level.OFF;
    this.parent = this;
  }
  
  public void addAppender(Appender paramAppender) {}
  
  public void callAppenders(LoggingEvent paramLoggingEvent) {}
  
  void closeNestedAppenders() {}
  
  public void debug(Object paramObject) {}
  
  public void error(Object paramObject) {}
  
  public void error(Object paramObject, Throwable paramThrowable) {}
  
  public void fatal(Object paramObject, Throwable paramThrowable) {}
  
  public Enumeration getAllAppenders()
  {
    return new Vector().elements();
  }
  
  public Level getEffectiveLevel()
  {
    return Level.OFF;
  }
  
  public void info(Object paramObject) {}
  
  public void info(Object paramObject, Throwable paramThrowable) {}
  
  public boolean isDebugEnabled()
  {
    return false;
  }
  
  public boolean isEnabledFor(Priority paramPriority)
  {
    return false;
  }
  
  public void log(String paramString, Priority paramPriority, Object paramObject, Throwable paramThrowable) {}
  
  public void removeAllAppenders() {}
  
  public void setLevel(Level paramLevel) {}
  
  public void setResourceBundle(ResourceBundle paramResourceBundle) {}
  
  public void warn(Object paramObject) {}
  
  public void warn(Object paramObject, Throwable paramThrowable) {}
  
  public boolean isTraceEnabled()
  {
    return false;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\spi\NOPLogger.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */