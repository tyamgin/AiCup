package com.google.common.reflect;

import com.google.common.annotations.Beta;
import java.util.Map;

@Beta
public abstract interface TypeToInstanceMap
  extends Map
{
  public abstract Object getInstance(Class paramClass);
  
  public abstract Object putInstance(Class paramClass, Object paramObject);
  
  public abstract Object getInstance(TypeToken paramTypeToken);
  
  public abstract Object putInstance(TypeToken paramTypeToken, Object paramObject);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\TypeToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */