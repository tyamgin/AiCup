package org.apache.log4j;

import org.apache.log4j.spi.LoggerFactory;

class DefaultCategoryFactory
  implements LoggerFactory
{
  public Logger makeNewLoggerInstance(String paramString)
  {
    return new Logger(paramString);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\DefaultCategoryFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */