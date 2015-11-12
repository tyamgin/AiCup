package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
public abstract interface ClassToInstanceMap
  extends Map
{
  public abstract Object getInstance(Class paramClass);
  
  public abstract Object putInstance(Class paramClass, Object paramObject);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ClassToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */