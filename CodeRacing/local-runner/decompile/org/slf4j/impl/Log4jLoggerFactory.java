package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.log4j.LogManager;
import org.slf4j.ILoggerFactory;

public class Log4jLoggerFactory
  implements ILoggerFactory
{
  ConcurrentMap loggerMap = new ConcurrentHashMap();
  
  public org.slf4j.Logger getLogger(String paramString)
  {
    org.slf4j.Logger localLogger1 = (org.slf4j.Logger)this.loggerMap.get(paramString);
    if (localLogger1 != null) {
      return localLogger1;
    }
    org.apache.log4j.Logger localLogger;
    if (paramString.equalsIgnoreCase("ROOT")) {
      localLogger = LogManager.getRootLogger();
    } else {
      localLogger = LogManager.getLogger(paramString);
    }
    Log4jLoggerAdapter localLog4jLoggerAdapter = new Log4jLoggerAdapter(localLogger);
    org.slf4j.Logger localLogger2 = (org.slf4j.Logger)this.loggerMap.putIfAbsent(paramString, localLog4jLoggerAdapter);
    return localLogger2 == null ? localLog4jLoggerAdapter : localLogger2;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\slf4j\impl\Log4jLoggerFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */