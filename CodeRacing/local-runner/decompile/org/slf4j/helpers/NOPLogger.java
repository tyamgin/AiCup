package org.slf4j.helpers;

public class NOPLogger
  extends MarkerIgnoringBase
{
  public static final NOPLogger NOP_LOGGER = new NOPLogger();
  
  public String getName()
  {
    return "NOP";
  }
  
  public final void debug(String paramString) {}
  
  public final void info(String paramString) {}
  
  public final void warn(String paramString) {}
  
  public final void warn(String paramString, Throwable paramThrowable) {}
  
  public final void error(String paramString) {}
  
  public final void error(String paramString, Throwable paramThrowable) {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\slf4j\helpers\NOPLogger.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */