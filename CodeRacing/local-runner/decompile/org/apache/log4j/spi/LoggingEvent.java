package org.apache.log4j.spi;

import java.io.Serializable;
import java.util.Hashtable;
import org.apache.log4j.Category;
import org.apache.log4j.Priority;

public class LoggingEvent
  implements Serializable
{
  private static long startTime = ;
  public final transient String fqnOfCategoryClass;
  private transient Category logger;
  public final String categoryName;
  public transient Priority level;
  private boolean ndcLookupRequired = true;
  private boolean mdcCopyLookupRequired = true;
  private transient Object message;
  private ThrowableInformation throwableInfo;
  public final long timeStamp;
  static final Integer[] PARAM_ARRAY = new Integer[1];
  static final Class[] TO_LEVEL_PARAMS = { Integer.TYPE };
  static final Hashtable methodCache = new Hashtable(3);
  
  public LoggingEvent(String paramString, Category paramCategory, Priority paramPriority, Object paramObject, Throwable paramThrowable)
  {
    this.fqnOfCategoryClass = paramString;
    this.logger = paramCategory;
    this.categoryName = paramCategory.getName();
    this.level = paramPriority;
    this.message = paramObject;
    if (paramThrowable != null) {
      this.throwableInfo = new ThrowableInformation(paramThrowable, paramCategory);
    }
    this.timeStamp = System.currentTimeMillis();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\spi\LoggingEvent.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */