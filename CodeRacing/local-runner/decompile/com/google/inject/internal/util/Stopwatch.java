package com.google.inject.internal.util;

import java.util.logging.Logger;

public final class Stopwatch
{
  private static final Logger logger = Logger.getLogger(Stopwatch.class.getName());
  private long start = System.currentTimeMillis();
  
  public long reset()
  {
    long l1 = System.currentTimeMillis();
    try
    {
      long l2 = l1 - this.start;
      return l2;
    }
    finally
    {
      this.start = l1;
    }
  }
  
  public void resetAndLog(String paramString)
  {
    String str = String.valueOf(String.valueOf(paramString));
    long l = reset();
    logger.fine(24 + str.length() + str + ": " + l + "ms");
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\util\Stopwatch.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */