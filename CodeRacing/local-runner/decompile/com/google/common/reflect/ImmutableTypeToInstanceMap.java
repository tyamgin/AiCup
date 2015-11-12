package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Map;

@Beta
public final class ImmutableTypeToInstanceMap
  extends ForwardingMap
  implements TypeToInstanceMap
{
  private final ImmutableMap delegate;
  
  public static ImmutableTypeToInstanceMap of()
  {
    return new ImmutableTypeToInstanceMap(ImmutableMap.of());
  }
  
  public static Builder builder()
  {
    return new Builder(null);
  }
  
  private ImmutableTypeToInstanceMap(ImmutableMap paramImmutableMap)
  {
    this.delegate = paramImmutableMap;
  }
  
  public Object getInstance(TypeToken paramTypeToken)
  {
    return trustedGet(paramTypeToken.rejectTypeVariables());
  }
  
  public Object putInstance(TypeToken paramTypeToken, Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public Object getInstance(Class paramClass)
  {
    return trustedGet(TypeToken.of(paramClass));
  }
  
  public Object putInstance(Class paramClass, Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  protected Map delegate()
  {
    return this.delegate;
  }
  
  private Object trustedGet(TypeToken paramTypeToken)
  {
    return this.delegate.get(paramTypeToken);
  }
  
  @Beta
  public static final class Builder
  {
    private final ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
    
    public Builder put(Class paramClass, Object paramObject)
    {
      this.mapBuilder.put(TypeToken.of(paramClass), paramObject);
      return this;
    }
    
    public Builder put(TypeToken paramTypeToken, Object paramObject)
    {
      this.mapBuilder.put(paramTypeToken.rejectTypeVariables(), paramObject);
      return this;
    }
    
    public ImmutableTypeToInstanceMap build()
    {
      return new ImmutableTypeToInstanceMap(this.mapBuilder.build(), null);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\ImmutableTypeToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */