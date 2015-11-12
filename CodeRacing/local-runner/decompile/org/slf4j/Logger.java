package org.slf4j;

public abstract interface Logger
{
  public abstract String getName();
  
  public abstract void debug(String paramString);
  
  public abstract void info(String paramString);
  
  public abstract void warn(String paramString);
  
  public abstract void warn(String paramString, Throwable paramThrowable);
  
  public abstract void error(String paramString);
  
  public abstract void error(String paramString, Throwable paramThrowable);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\slf4j\Logger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */