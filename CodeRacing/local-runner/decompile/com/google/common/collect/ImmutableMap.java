package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
public abstract class ImmutableMap
  implements Serializable, Map
{
  private transient ImmutableSet entrySet;
  private transient ImmutableSet keySet;
  private transient ImmutableCollection values;
  private transient ImmutableSetMultimap multimapView;
  
  public static ImmutableMap of()
  {
    return ImmutableBiMap.of();
  }
  
  public static ImmutableMap of(Object paramObject1, Object paramObject2)
  {
    return ImmutableBiMap.of(paramObject1, paramObject2);
  }
  
  public static ImmutableMap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    return new RegularImmutableMap(new Map.Entry[] { entryOf(paramObject1, paramObject2), entryOf(paramObject3, paramObject4) });
  }
  
  public static ImmutableMap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6)
  {
    return new RegularImmutableMap(new Map.Entry[] { entryOf(paramObject1, paramObject2), entryOf(paramObject3, paramObject4), entryOf(paramObject5, paramObject6) });
  }
  
  public static ImmutableMap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8)
  {
    return new RegularImmutableMap(new Map.Entry[] { entryOf(paramObject1, paramObject2), entryOf(paramObject3, paramObject4), entryOf(paramObject5, paramObject6), entryOf(paramObject7, paramObject8) });
  }
  
  public static ImmutableMap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10)
  {
    return new RegularImmutableMap(new Map.Entry[] { entryOf(paramObject1, paramObject2), entryOf(paramObject3, paramObject4), entryOf(paramObject5, paramObject6), entryOf(paramObject7, paramObject8), entryOf(paramObject9, paramObject10) });
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  static Map.Entry entryOf(Object paramObject1, Object paramObject2)
  {
    Preconditions.checkNotNull(paramObject1, "null key in entry: null=%s", new Object[] { paramObject2 });
    Preconditions.checkNotNull(paramObject2, "null value in entry: %s=null", new Object[] { paramObject1 });
    return Maps.immutableEntry(paramObject1, paramObject2);
  }
  
  public static ImmutableMap copyOf(Map paramMap)
  {
    Object localObject3;
    if (((paramMap instanceof ImmutableMap)) && (!(paramMap instanceof ImmutableSortedMap)))
    {
      localObject1 = (ImmutableMap)paramMap;
      if (!((ImmutableMap)localObject1).isPartialView()) {
        return (ImmutableMap)localObject1;
      }
    }
    else if ((paramMap instanceof EnumMap))
    {
      localObject1 = (EnumMap)paramMap;
      Object localObject2 = ((EnumMap)localObject1).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Map.Entry)((Iterator)localObject2).next();
        Preconditions.checkNotNull(((Map.Entry)localObject3).getKey());
        Preconditions.checkNotNull(((Map.Entry)localObject3).getValue());
      }
      localObject2 = ImmutableEnumMap.asImmutable(new EnumMap((EnumMap)localObject1));
      return (ImmutableMap)localObject2;
    }
    Object localObject1 = (Map.Entry[])paramMap.entrySet().toArray(new Map.Entry[0]);
    switch (localObject1.length)
    {
    case 0: 
      return of();
    case 1: 
      return new SingletonImmutableBiMap(entryOf(localObject1[0].getKey(), localObject1[0].getValue()));
    }
    for (int i = 0; i < localObject1.length; i++)
    {
      localObject3 = localObject1[i].getKey();
      Object localObject4 = localObject1[i].getValue();
      localObject1[i] = entryOf(localObject3, localObject4);
    }
    return new RegularImmutableMap((Map.Entry[])localObject1);
  }
  
  @Deprecated
  public final Object put(Object paramObject1, Object paramObject2)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final Object remove(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final void putAll(Map paramMap)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public final void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean isEmpty()
  {
    return size() == 0;
  }
  
  public boolean containsKey(Object paramObject)
  {
    return get(paramObject) != null;
  }
  
  public boolean containsValue(Object paramObject)
  {
    return (paramObject != null) && (Maps.containsValueImpl(this, paramObject));
  }
  
  public abstract Object get(Object paramObject);
  
  public ImmutableSet entrySet()
  {
    ImmutableSet localImmutableSet = this.entrySet;
    return localImmutableSet == null ? (this.entrySet = createEntrySet()) : localImmutableSet;
  }
  
  abstract ImmutableSet createEntrySet();
  
  public ImmutableSet keySet()
  {
    ImmutableSet localImmutableSet = this.keySet;
    return localImmutableSet == null ? (this.keySet = createKeySet()) : localImmutableSet;
  }
  
  ImmutableSet createKeySet()
  {
    return new ImmutableMapKeySet(this);
  }
  
  public ImmutableCollection values()
  {
    ImmutableCollection localImmutableCollection = this.values;
    return localImmutableCollection == null ? (this.values = new ImmutableMapValues(this)) : localImmutableCollection;
  }
  
  @Beta
  public ImmutableSetMultimap asMultimap()
  {
    ImmutableSetMultimap localImmutableSetMultimap = this.multimapView;
    return localImmutableSetMultimap == null ? (this.multimapView = createMultimapView()) : localImmutableSetMultimap;
  }
  
  private ImmutableSetMultimap createMultimapView()
  {
    ImmutableMap localImmutableMap = viewMapValuesAsSingletonSets();
    return new ImmutableSetMultimap(localImmutableMap, localImmutableMap.size(), null);
  }
  
  private ImmutableMap viewMapValuesAsSingletonSets()
  {
    new ImmutableMap()
    {
      public int size()
      {
        return ImmutableMap.this.size();
      }
      
      public boolean containsKey(Object paramAnonymousObject)
      {
        return ImmutableMap.this.containsKey(paramAnonymousObject);
      }
      
      public ImmutableSet get(Object paramAnonymousObject)
      {
        Object localObject = ImmutableMap.this.get(paramAnonymousObject);
        return localObject == null ? null : ImmutableSet.of(localObject);
      }
      
      boolean isPartialView()
      {
        return false;
      }
      
      ImmutableSet createEntrySet()
      {
        new ImmutableMapEntrySet()
        {
          ImmutableMap map()
          {
            return ImmutableMap.1MapViewOfValuesAsSingletonSets.this;
          }
          
          public UnmodifiableIterator iterator()
          {
            final UnmodifiableIterator localUnmodifiableIterator = ImmutableMap.this.entrySet().iterator();
            new UnmodifiableIterator()
            {
              public boolean hasNext()
              {
                return localUnmodifiableIterator.hasNext();
              }
              
              public Map.Entry next()
              {
                final Map.Entry localEntry = (Map.Entry)localUnmodifiableIterator.next();
                new AbstractMapEntry()
                {
                  public Object getKey()
                  {
                    return localEntry.getKey();
                  }
                  
                  public ImmutableSet getValue()
                  {
                    return ImmutableSet.of(localEntry.getValue());
                  }
                };
              }
            };
          }
        };
      }
    };
  }
  
  public boolean equals(Object paramObject)
  {
    return Maps.equalsImpl(this, paramObject);
  }
  
  abstract boolean isPartialView();
  
  public int hashCode()
  {
    return entrySet().hashCode();
  }
  
  public String toString()
  {
    return Maps.toStringImpl(this);
  }
  
  Object writeReplace()
  {
    return new SerializedForm(this);
  }
  
  static class SerializedForm
    implements Serializable
  {
    private final Object[] keys;
    private final Object[] values;
    private static final long serialVersionUID = 0L;
    
    SerializedForm(ImmutableMap paramImmutableMap)
    {
      this.keys = new Object[paramImmutableMap.size()];
      this.values = new Object[paramImmutableMap.size()];
      int i = 0;
      Iterator localIterator = paramImmutableMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        this.keys[i] = localEntry.getKey();
        this.values[i] = localEntry.getValue();
        i++;
      }
    }
    
    Object readResolve()
    {
      ImmutableMap.Builder localBuilder = new ImmutableMap.Builder();
      return createMap(localBuilder);
    }
    
    Object createMap(ImmutableMap.Builder paramBuilder)
    {
      for (int i = 0; i < this.keys.length; i++) {
        paramBuilder.put(this.keys[i], this.values[i]);
      }
      return paramBuilder.build();
    }
  }
  
  public static class Builder
  {
    final ArrayList entries = Lists.newArrayList();
    
    public Builder put(Object paramObject1, Object paramObject2)
    {
      this.entries.add(ImmutableMap.entryOf(paramObject1, paramObject2));
      return this;
    }
    
    public Builder put(Map.Entry paramEntry)
    {
      Object localObject1 = paramEntry.getKey();
      Object localObject2 = paramEntry.getValue();
      if ((paramEntry instanceof ImmutableEntry))
      {
        Preconditions.checkNotNull(localObject1);
        Preconditions.checkNotNull(localObject2);
        Map.Entry localEntry = paramEntry;
        this.entries.add(localEntry);
      }
      else
      {
        this.entries.add(ImmutableMap.entryOf(localObject1, localObject2));
      }
      return this;
    }
    
    public Builder putAll(Map paramMap)
    {
      this.entries.ensureCapacity(this.entries.size() + paramMap.size());
      Iterator localIterator = paramMap.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        put(localEntry.getKey(), localEntry.getValue());
      }
      return this;
    }
    
    public ImmutableMap build()
    {
      return fromEntryList(this.entries);
    }
    
    private static ImmutableMap fromEntryList(List paramList)
    {
      int i = paramList.size();
      switch (i)
      {
      case 0: 
        return ImmutableMap.of();
      case 1: 
        return new SingletonImmutableBiMap((Map.Entry)Iterables.getOnlyElement(paramList));
      }
      Map.Entry[] arrayOfEntry = (Map.Entry[])paramList.toArray(new Map.Entry[paramList.size()]);
      return new RegularImmutableMap(arrayOfEntry);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */