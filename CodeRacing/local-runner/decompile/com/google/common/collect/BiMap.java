package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;
import java.util.Set;

@GwtCompatible
public abstract interface BiMap
  extends Map
{
  public abstract Object put(Object paramObject1, Object paramObject2);
  
  public abstract Object forcePut(Object paramObject1, Object paramObject2);
  
  public abstract void putAll(Map paramMap);
  
  public abstract Set values();
  
  public abstract BiMap inverse();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\BiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */