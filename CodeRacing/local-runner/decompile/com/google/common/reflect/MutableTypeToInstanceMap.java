package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Maps;
import java.util.Map;

@Beta
public final class MutableTypeToInstanceMap
  extends ForwardingMap
  implements TypeToInstanceMap
{
  private final Map backingMap = Maps.newHashMap();
  
  public Object getInstance(Class paramClass)
  {
    return trustedGet(TypeToken.of(paramClass));
  }
  
  public Object putInstance(Class paramClass, Object paramObject)
  {
    return trustedPut(TypeToken.of(paramClass), paramObject);
  }
  
  public Object getInstance(TypeToken paramTypeToken)
  {
    return trustedGet(paramTypeToken.rejectTypeVariables());
  }
  
  public Object putInstance(TypeToken paramTypeToken, Object paramObject)
  {
    return trustedPut(paramTypeToken.rejectTypeVariables(), paramObject);
  }
  
  public Object put(TypeToken paramTypeToken, Object paramObject)
  {
    throw new UnsupportedOperationException("Please use putInstance() instead.");
  }
  
  public void putAll(Map paramMap)
  {
    throw new UnsupportedOperationException("Please use putInstance() instead.");
  }
  
  protected Map delegate()
  {
    return this.backingMap;
  }
  
  private Object trustedPut(TypeToken paramTypeToken, Object paramObject)
  {
    return this.backingMap.put(paramTypeToken, paramObject);
  }
  
  private Object trustedGet(TypeToken paramTypeToken)
  {
    return this.backingMap.get(paramTypeToken);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\MutableTypeToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */