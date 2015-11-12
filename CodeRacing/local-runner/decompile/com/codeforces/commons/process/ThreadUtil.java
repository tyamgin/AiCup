package com.codeforces.commons.process;

import org.apache.log4j.Logger;

public class ThreadUtil
{
  private static final Logger logger = Logger.getLogger(ThreadUtil.class);
  
  private ThreadUtil()
  {
    throw new UnsupportedOperationException();
  }
  
  public static void sleep(long paramLong)
  {
    try
    {
      Thread.sleep(paramLong);
    }
    catch (InterruptedException localInterruptedException) {}
  }
  
  public static Object execute(Operation paramOperation, int paramInt, ExecutionStrategy paramExecutionStrategy)
    throws Throwable
  {
    ensureArguments(paramOperation, paramInt, paramExecutionStrategy);
    for (int i = 1; i <= paramInt; i++) {
      try
      {
        return paramOperation.run();
      }
      catch (Throwable localThrowable)
      {
        if (paramExecutionStrategy.getUnsuccessHandler() != null) {
          paramExecutionStrategy.getUnsuccessHandler().handle(i, localThrowable);
        }
        if (i < paramInt)
        {
          if (i == 1) {
            logger.info("Iteration #1 has been failed: " + localThrowable.getMessage(), localThrowable);
          } else {
            logger.warn("Iteration #" + i + " has been failed: " + localThrowable.getMessage(), localThrowable);
          }
          sleep(paramExecutionStrategy.getDelayTimeMillis(i));
        }
        else
        {
          logger.error("Iteration #" + i + " has been failed: " + localThrowable.getMessage(), localThrowable);
          throw localThrowable;
        }
      }
    }
    throw new RuntimeException("This line shouldn't be executed.");
  }
  
  private static void ensureArguments(Operation paramOperation, int paramInt, ExecutionStrategy paramExecutionStrategy)
  {
    if (paramOperation == null) {
      throw new IllegalArgumentException("Argument 'operation' can't be 'null'.");
    }
    if (paramInt < 1) {
      throw new IllegalArgumentException("Argument 'attemptCount' should be positive.");
    }
    if (paramExecutionStrategy == null) {
      throw new IllegalArgumentException("Argument 'strategy' can't be 'null'.");
    }
  }
  
  public static class ExecutionStrategy
  {
    private final long delayTimeMillis;
    private final Type type;
    private final ThreadUtil.UnsuccessHandler unsuccessHandler;
    
    public ExecutionStrategy(long paramLong, Type paramType)
    {
      this(paramLong, paramType, null);
    }
    
    public ExecutionStrategy(long paramLong, Type paramType, ThreadUtil.UnsuccessHandler paramUnsuccessHandler)
    {
      ensureArguments(paramLong, paramType);
      this.delayTimeMillis = paramLong;
      this.type = paramType;
      this.unsuccessHandler = paramUnsuccessHandler;
    }
    
    private static void ensureArguments(long paramLong, Type paramType)
    {
      if (paramLong < 1L) {
        throw new IllegalArgumentException("Argument 'delayTimeMillis' should be positive.");
      }
      if (paramType == null) {
        throw new IllegalArgumentException("Argument 'type' can't be 'null'.");
      }
    }
    
    public long getDelayTimeMillis(int paramInt)
    {
      if (paramInt < 1) {
        throw new IllegalArgumentException("Argument 'attemptNumber' should be positive.");
      }
      switch (ThreadUtil.3.$SwitchMap$com$codeforces$commons$process$ThreadUtil$ExecutionStrategy$Type[this.type.ordinal()])
      {
      case 1: 
        return this.delayTimeMillis;
      case 2: 
        return this.delayTimeMillis * paramInt;
      case 3: 
        return this.delayTimeMillis * paramInt * paramInt;
      }
      throw new IllegalArgumentException("Unknown strategy type '" + this.type + "'.");
    }
    
    public ThreadUtil.UnsuccessHandler getUnsuccessHandler()
    {
      return this.unsuccessHandler;
    }
    
    public static enum Type
    {
      CONSTANT,  LINEAR,  SQUARE;
    }
  }
  
  public static abstract interface UnsuccessHandler
  {
    public abstract void handle(int paramInt, Throwable paramThrowable);
  }
  
  public static abstract interface Operation
  {
    public abstract Object run()
      throws Throwable;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\process\ThreadUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */