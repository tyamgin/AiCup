package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

public abstract interface ErrorHandler
  extends OptionHandler
{
  public abstract void setLogger(Logger paramLogger);
  
  public abstract void setBackupAppender(Appender paramAppender);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\spi\ErrorHandler.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */