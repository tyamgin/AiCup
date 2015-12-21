package org.apache.log4j;

public class Priority
{
  transient int level;
  transient String levelStr;
  transient int syslogEquivalent;
  public static final Priority FATAL = new Level(50000, "FATAL", 0);
  public static final Priority ERROR = new Level(40000, "ERROR", 3);
  public static final Priority WARN = new Level(30000, "WARN", 4);
  public static final Priority INFO = new Level(20000, "INFO", 6);
  public static final Priority DEBUG = new Level(10000, "DEBUG", 7);
  
  protected Priority()
  {
    this.level = 10000;
    this.levelStr = "DEBUG";
    this.syslogEquivalent = 7;
  }
  
  protected Priority(int paramInt1, String paramString, int paramInt2)
  {
    this.level = paramInt1;
    this.levelStr = paramString;
    this.syslogEquivalent = paramInt2;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Priority))
    {
      Priority localPriority = (Priority)paramObject;
      return this.level == localPriority.level;
    }
    return false;
  }
  
  public boolean isGreaterOrEqual(Priority paramPriority)
  {
    return this.level >= paramPriority.level;
  }
  
  public final String toString()
  {
    return this.levelStr;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\Priority.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */