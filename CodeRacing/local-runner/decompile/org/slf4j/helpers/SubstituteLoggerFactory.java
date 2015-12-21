package org.slf4j.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class SubstituteLoggerFactory
  implements ILoggerFactory
{
  final ConcurrentMap loggers = new ConcurrentHashMap();
  
  public Logger getLogger(String paramString)
  {
    Object localObject = (SubstituteLogger)this.loggers.get(paramString);
    if (localObject == null)
    {
      localObject = new SubstituteLogger(paramString);
      SubstituteLogger localSubstituteLogger = (SubstituteLogger)this.loggers.putIfAbsent(paramString, localObject);
      if (localSubstituteLogger != null) {
        localObject = localSubstituteLogger;
      }
    }
    return (Logger)localObject;
  }
  
  public List getLoggers()
  {
    return new ArrayList(this.loggers.values());
  }
  
  public void clear()
  {
    this.loggers.clear();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\slf4j\helpers\SubstituteLoggerFactory.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */