package org.slf4j.helpers;

import org.slf4j.Logger;

public class SubstituteLogger
  implements Logger
{
  private final String name;
  private volatile Logger _delegate;
  
  public SubstituteLogger(String paramString)
  {
    this.name = paramString;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public void debug(String paramString)
  {
    delegate().debug(paramString);
  }
  
  public void info(String paramString)
  {
    delegate().info(paramString);
  }
  
  public void warn(String paramString)
  {
    delegate().warn(paramString);
  }
  
  public void warn(String paramString, Throwable paramThrowable)
  {
    delegate().warn(paramString, paramThrowable);
  }
  
  public void error(String paramString)
  {
    delegate().error(paramString);
  }
  
  public void error(String paramString, Throwable paramThrowable)
  {
    delegate().error(paramString, paramThrowable);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    SubstituteLogger localSubstituteLogger = (SubstituteLogger)paramObject;
    return this.name.equals(localSubstituteLogger.name);
  }
  
  public int hashCode()
  {
    return this.name.hashCode();
  }
  
  Logger delegate()
  {
    return this._delegate != null ? this._delegate : NOPLogger.NOP_LOGGER;
  }
  
  public void setDelegate(Logger paramLogger)
  {
    this._delegate = paramLogger;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\slf4j\helpers\SubstituteLogger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */