package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableBiMap
  extends ImmutableMap
  implements BiMap
{
  public static ImmutableBiMap of()
  {
    return EmptyImmutableBiMap.INSTANCE;
  }
  
  public static ImmutableBiMap of(Object paramObject1, Object paramObject2)
  {
    Preconditions.checkNotNull(paramObject1, "null key in entry: null=%s", new Object[] { paramObject2 });
    Preconditions.checkNotNull(paramObject2, "null value in entry: %s=null", new Object[] { paramObject1 });
    return new SingletonImmutableBiMap(paramObject1, paramObject2);
  }
  
  public static ImmutableBiMap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    return new Builder().put(paramObject1, paramObject2).put(paramObject3, paramObject4).build();
  }
  
  public static ImmutableBiMap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6)
  {
    return new Builder().put(paramObject1, paramObject2).put(paramObject3, paramObject4).put(paramObject5, paramObject6).build();
  }
  
  public static ImmutableBiMap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8)
  {
    return new Builder().put(paramObject1, paramObject2).put(paramObject3, paramObject4).put(paramObject5, paramObject6).put(paramObject7, paramObject8).build();
  }
  
  public static ImmutableBiMap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10)
  {
    return new Builder().put(paramObject1, paramObject2).put(paramObject3, paramObject4).put(paramObject5, paramObject6).put(paramObject7, paramObject8).put(paramObject9, paramObject10).build();
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static ImmutableBiMap copyOf(Map paramMap)
  {
    if ((paramMap instanceof ImmutableBiMap))
    {
      ImmutableBiMap localImmutableBiMap = (ImmutableBiMap)paramMap;
      if (!localImmutableBiMap.isPartialView()) {
        return localImmutableBiMap;
      }
    }
    return fromEntries(ImmutableList.copyOf(paramMap.entrySet()));
  }
  
  static ImmutableBiMap fromEntries(Collection paramCollection)
  {
    switch (paramCollection.size())
    {
    case 0: 
      return of();
    case 1: 
      Map.Entry localEntry = (Map.Entry)Iterables.getOnlyElement(paramCollection);
      return new SingletonImmutableBiMap(localEntry.getKey(), localEntry.getValue());
    }
    return new RegularImmutableBiMap(paramCollection);
  }
  
  public abstract ImmutableBiMap inverse();
  
  public ImmutableSet values()
  {
    return inverse().keySet();
  }
  
  @Deprecated
  public Object forcePut(Object paramObject1, Object paramObject2)
  {
    throw new UnsupportedOperationException();
  }
  
  Object writeReplace()
  {
    return new SerializedForm(this);
  }
  
  private static class SerializedForm
    extends ImmutableMap.SerializedForm
  {
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableBiMap paramImmutableBiMap)
    {
      super();
    }
    
    Object readResolve()
    {
      ImmutableBiMap.Builder localBuilder = new ImmutableBiMap.Builder();
      return createMap(localBuilder);
    }
  }
  
  public static final class Builder
    extends ImmutableMap.Builder
  {
    public Builder put(Object paramObject1, Object paramObject2)
    {
      super.put(paramObject1, paramObject2);
      return this;
    }
    
    public Builder putAll(Map paramMap)
    {
      super.putAll(paramMap);
      return this;
    }
    
    public ImmutableBiMap build()
    {
      return ImmutableBiMap.fromEntries(this.entries);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableBiMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */