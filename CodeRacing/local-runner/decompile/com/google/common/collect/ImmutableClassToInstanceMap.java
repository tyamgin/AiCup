package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Primitives;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class ImmutableClassToInstanceMap
  extends ForwardingMap
  implements ClassToInstanceMap
{
  private final ImmutableMap delegate;
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static ImmutableClassToInstanceMap copyOf(Map paramMap)
  {
    if ((paramMap instanceof ImmutableClassToInstanceMap))
    {
      ImmutableClassToInstanceMap localImmutableClassToInstanceMap = (ImmutableClassToInstanceMap)paramMap;
      return localImmutableClassToInstanceMap;
    }
    return new Builder().putAll(paramMap).build();
  }
  
  private ImmutableClassToInstanceMap(ImmutableMap paramImmutableMap)
  {
    this.delegate = paramImmutableMap;
  }
  
  protected Map delegate()
  {
    return this.delegate;
  }
  
  public Object getInstance(Class paramClass)
  {
    return this.delegate.get(Preconditions.checkNotNull(paramClass));
  }
  
  @Deprecated
  public Object putInstance(Class paramClass, Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public static final class Builder
  {
    private final ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
    
    public Builder put(Class paramClass, Object paramObject)
    {
      this.mapBuilder.put(paramClass, paramObject);
      return this;
    }
    
    public Builder putAll(Map paramMap)
    {
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        Class localClass = (Class)localEntry.getKey();
        Object localObject = localEntry.getValue();
        this.mapBuilder.put(localClass, cast(localClass, localObject));
      }
      return this;
    }
    
    private static Object cast(Class paramClass, Object paramObject)
    {
      return Primitives.wrap(paramClass).cast(paramObject);
    }
    
    public ImmutableClassToInstanceMap build()
    {
      return new ImmutableClassToInstanceMap(this.mapBuilder.build(), null);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableClassToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */