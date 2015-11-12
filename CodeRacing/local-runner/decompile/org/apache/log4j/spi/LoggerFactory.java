package org.apache.log4j.spi;

import org.apache.log4j.Logger;

public abstract interface LoggerFactory
{
  public abstract Logger makeNewLoggerInstance(String paramString);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\spi\LoggerFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */