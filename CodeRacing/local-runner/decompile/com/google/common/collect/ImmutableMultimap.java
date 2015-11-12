package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible(emulated=true)
public abstract class ImmutableMultimap
  extends AbstractMultimap
  implements Serializable
{
  final transient ImmutableMap map;
  final transient int size;
  private static final long serialVersionUID = 0L;
  
  public static ImmutableMultimap of()
  {
    return ImmutableListMultimap.of();
  }
  
  public static ImmutableMultimap of(Object paramObject1, Object paramObject2)
  {
    return ImmutableListMultimap.of(paramObject1, paramObject2);
  }
  
  public static ImmutableMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    return ImmutableListMultimap.of(paramObject1, paramObject2, paramObject3, paramObject4);
  }
  
  public static ImmutableMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6)
  {
    return ImmutableListMultimap.of(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6);
  }
  
  public static ImmutableMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8)
  {
    return ImmutableListMultimap.of(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8);
  }
  
  public static ImmutableMultimap of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10)
  {
    return ImmutableListMultimap.of(paramObject1, paramObject2, paramObject3, paramObject4, paramObject5, paramObject6, paramObject7, paramObject8, paramObject9, paramObject10);
  }
  
  public static Builder builder()
  {
    return new Builder();
  }
  
  public static ImmutableMultimap copyOf(Multimap paramMultimap)
  {
    if ((paramMultimap instanceof ImmutableMultimap))
    {
      ImmutableMultimap localImmutableMultimap = (ImmutableMultimap)paramMultimap;
      if (!localImmutableMultimap.isPartialView()) {
        return localImmutableMultimap;
      }
    }
    return ImmutableListMultimap.copyOf(paramMultimap);
  }
  
  ImmutableMultimap(ImmutableMap paramImmutableMap, int paramInt)
  {
    this.map = paramImmutableMap;
    this.size = paramInt;
  }
  
  @Deprecated
  public ImmutableCollection removeAll(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public ImmutableCollection replaceValues(Object paramObject, Iterable paramIterable)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public void clear()
  {
    throw new UnsupportedOperationException();
  }
  
  public abstract ImmutableCollection get(Object paramObject);
  
  public abstract ImmutableMultimap inverse();
  
  @Deprecated
  public boolean put(Object paramObject1, Object paramObject2)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public boolean putAll(Object paramObject, Iterable paramIterable)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public boolean putAll(Multimap paramMultimap)
  {
    throw new UnsupportedOperationException();
  }
  
  @Deprecated
  public boolean remove(Object paramObject1, Object paramObject2)
  {
    throw new UnsupportedOperationException();
  }
  
  boolean isPartialView()
  {
    return this.map.isPartialView();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return this.map.containsKey(paramObject);
  }
  
  public int size()
  {
    return this.size;
  }
  
  public ImmutableSet keySet()
  {
    return this.map.keySet();
  }
  
  public ImmutableMap asMap()
  {
    return this.map;
  }
  
  Map createAsMap()
  {
    throw new AssertionError("should never be called");
  }
  
  public ImmutableCollection entries()
  {
    return (ImmutableCollection)super.entries();
  }
  
  ImmutableCollection createEntries()
  {
    return new EntryCollection(this);
  }
  
  UnmodifiableIterator entryIterator()
  {
    final UnmodifiableIterator localUnmodifiableIterator = this.map.entrySet().iterator();
    new UnmodifiableIterator()
    {
      Object key;
      Iterator valueIterator;
      
      public boolean hasNext()
      {
        return ((this.key != null) && (this.valueIterator.hasNext())) || (localUnmodifiableIterator.hasNext());
      }
      
      public Map.Entry next()
      {
        if ((this.key == null) || (!this.valueIterator.hasNext()))
        {
          Map.Entry localEntry = (Map.Entry)localUnmodifiableIterator.next();
          this.key = localEntry.getKey();
          this.valueIterator = ((ImmutableCollection)localEntry.getValue()).iterator();
        }
        return Maps.immutableEntry(this.key, this.valueIterator.next());
      }
    };
  }
  
  public ImmutableMultiset keys()
  {
    return (ImmutableMultiset)super.keys();
  }
  
  ImmutableMultiset createKeys()
  {
    return new Keys();
  }
  
  public ImmutableCollection values()
  {
    return (ImmutableCollection)super.values();
  }
  
  ImmutableCollection createValues()
  {
    return new Values(this);
  }
  
  private static class Values
    extends ImmutableCollection
  {
    final ImmutableMultimap multimap;
    private static final long serialVersionUID = 0L;
    
    Values(ImmutableMultimap paramImmutableMultimap)
    {
      this.multimap = paramImmutableMultimap;
    }
    
    public UnmodifiableIterator iterator()
    {
      return Maps.valueIterator(this.multimap.entries().iterator());
    }
    
    public int size()
    {
      return this.multimap.size();
    }
    
    boolean isPartialView()
    {
      return true;
    }
  }
  
  class Keys
    extends ImmutableMultiset
  {
    Keys() {}
    
    public boolean contains(Object paramObject)
    {
      return ImmutableMultimap.this.containsKey(paramObject);
    }
    
    public int count(Object paramObject)
    {
      Collection localCollection = (Collection)ImmutableMultimap.this.map.get(paramObject);
      return localCollection == null ? 0 : localCollection.size();
    }
    
    public Set elementSet()
    {
      return ImmutableMultimap.this.keySet();
    }
    
    public int size()
    {
      return ImmutableMultimap.this.size();
    }
    
    ImmutableSet createEntrySet()
    {
      return new KeysEntrySet(null);
    }
    
    boolean isPartialView()
    {
      return true;
    }
    
    private class KeysEntrySet
      extends ImmutableMultiset.EntrySet
    {
      private KeysEntrySet()
      {
        super();
      }
      
      public int size()
      {
        return ImmutableMultimap.this.keySet().size();
      }
      
      public UnmodifiableIterator iterator()
      {
        return asList().iterator();
      }
      
      ImmutableList createAsList()
      {
        final ImmutableList localImmutableList = ImmutableMultimap.this.map.entrySet().asList();
        new ImmutableAsList()
        {
          public Multiset.Entry get(int paramAnonymousInt)
          {
            Map.Entry localEntry = (Map.Entry)localImmutableList.get(paramAnonymousInt);
            return Multisets.immutableEntry(localEntry.getKey(), ((Collection)localEntry.getValue()).size());
          }
          
          ImmutableCollection delegateCollection()
          {
            return ImmutableMultimap.Keys.KeysEntrySet.this;
          }
        };
      }
    }
  }
  
  private static class EntryCollection
    extends ImmutableCollection
  {
    final ImmutableMultimap multimap;
    private static final long serialVersionUID = 0L;
    
    EntryCollection(ImmutableMultimap paramImmutableMultimap)
    {
      this.multimap = paramImmutableMultimap;
    }
    
    public UnmodifiableIterator iterator()
    {
      return this.multimap.entryIterator();
    }
    
    boolean isPartialView()
    {
      return this.multimap.isPartialView();
    }
    
    public int size()
    {
      return this.multimap.size();
    }
    
    public boolean contains(Object paramObject)
    {
      if ((paramObject instanceof Map.Entry))
      {
        Map.Entry localEntry = (Map.Entry)paramObject;
        return this.multimap.containsEntry(localEntry.getKey(), localEntry.getValue());
      }
      return false;
    }
  }
  
  @GwtIncompatible("java serialization is not supported")
  static class FieldSettersHolder
  {
    static final Serialization.FieldSetter MAP_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "map");
    static final Serialization.FieldSetter SIZE_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "size");
  }
  
  public static class Builder
  {
    Multimap builderMultimap = new ImmutableMultimap.BuilderMultimap();
    Comparator keyComparator;
    Comparator valueComparator;
    
    public Builder put(Object paramObject1, Object paramObject2)
    {
      this.builderMultimap.put(Preconditions.checkNotNull(paramObject1), Preconditions.checkNotNull(paramObject2));
      return this;
    }
    
    public Builder put(Map.Entry paramEntry)
    {
      this.builderMultimap.put(Preconditions.checkNotNull(paramEntry.getKey()), Preconditions.checkNotNull(paramEntry.getValue()));
      return this;
    }
    
    public Builder putAll(Object paramObject, Iterable paramIterable)
    {
      Collection localCollection = this.builderMultimap.get(Preconditions.checkNotNull(paramObject));
      Iterator localIterator = paramIterable.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = localIterator.next();
        localCollection.add(Preconditions.checkNotNull(localObject));
      }
      return this;
    }
    
    public Builder putAll(Object paramObject, Object... paramVarArgs)
    {
      return putAll(paramObject, Arrays.asList(paramVarArgs));
    }
    
    public Builder putAll(Multimap paramMultimap)
    {
      Iterator localIterator = paramMultimap.asMap().entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        putAll(localEntry.getKey(), (Iterable)localEntry.getValue());
      }
      return this;
    }
    
    public Builder orderKeysBy(Comparator paramComparator)
    {
      this.keyComparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
      return this;
    }
    
    public Builder orderValuesBy(Comparator paramComparator)
    {
      this.valueComparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
      return this;
    }
    
    public ImmutableMultimap build()
    {
      Object localObject1;
      Object localObject2;
      Object localObject3;
      if (this.valueComparator != null)
      {
        localObject1 = this.builderMultimap.asMap().values().iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (Collection)((Iterator)localObject1).next();
          localObject3 = (List)localObject2;
          Collections.sort((List)localObject3, this.valueComparator);
        }
      }
      if (this.keyComparator != null)
      {
        localObject1 = new ImmutableMultimap.BuilderMultimap();
        localObject2 = Lists.newArrayList(this.builderMultimap.asMap().entrySet());
        Collections.sort((List)localObject2, Ordering.from(this.keyComparator).onResultOf(new Function()
        {
          public Object apply(Map.Entry paramAnonymousEntry)
          {
            return paramAnonymousEntry.getKey();
          }
        }));
        localObject3 = ((List)localObject2).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          Map.Entry localEntry = (Map.Entry)((Iterator)localObject3).next();
          ((Multimap)localObject1).putAll(localEntry.getKey(), (Iterable)localEntry.getValue());
        }
        this.builderMultimap = ((Multimap)localObject1);
      }
      return ImmutableMultimap.copyOf(this.builderMultimap);
    }
  }
  
  private static class BuilderMultimap
    extends AbstractMapBasedMultimap
  {
    private static final long serialVersionUID = 0L;
    
    BuilderMultimap()
    {
      super();
    }
    
    Collection createCollection()
    {
      return Lists.newArrayList();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */