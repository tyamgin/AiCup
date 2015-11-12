package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible(serializable=true, emulated=true)
final class ImmutableEnumMap
  extends ImmutableMap
{
  private final transient EnumMap delegate;
  
  static ImmutableMap asImmutable(EnumMap paramEnumMap)
  {
    switch (paramEnumMap.size())
    {
    case 0: 
      return ImmutableMap.of();
    case 1: 
      Map.Entry localEntry = (Map.Entry)Iterables.getOnlyElement(paramEnumMap.entrySet());
      return ImmutableMap.of(localEntry.getKey(), localEntry.getValue());
    }
    return new ImmutableEnumMap(paramEnumMap);
  }
  
  private ImmutableEnumMap(EnumMap paramEnumMap)
  {
    this.delegate = paramEnumMap;
    Preconditions.checkArgument(!paramEnumMap.isEmpty());
  }
  
  ImmutableSet createKeySet()
  {
    new ImmutableSet()
    {
      public boolean contains(Object paramAnonymousObject)
      {
        return ImmutableEnumMap.this.delegate.containsKey(paramAnonymousObject);
      }
      
      public int size()
      {
        return ImmutableEnumMap.this.size();
      }
      
      public UnmodifiableIterator iterator()
      {
        return Iterators.unmodifiableIterator(ImmutableEnumMap.this.delegate.keySet().iterator());
      }
      
      boolean isPartialView()
      {
        return true;
      }
    };
  }
  
  public int size()
  {
    return this.delegate.size();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return this.delegate.containsKey(paramObject);
  }
  
  public Object get(Object paramObject)
  {
    return this.delegate.get(paramObject);
  }
  
  ImmutableSet createEntrySet()
  {
    new ImmutableMapEntrySet()
    {
      ImmutableMap map()
      {
        return ImmutableEnumMap.this;
      }
      
      public UnmodifiableIterator iterator()
      {
        new UnmodifiableIterator()
        {
          private final Iterator backingIterator = ImmutableEnumMap.this.delegate.entrySet().iterator();
          
          public boolean hasNext()
          {
            return this.backingIterator.hasNext();
          }
          
          public Map.Entry next()
          {
            Map.Entry localEntry = (Map.Entry)this.backingIterator.next();
            return Maps.immutableEntry(localEntry.getKey(), localEntry.getValue());
          }
        };
      }
    };
  }
  
  boolean isPartialView()
  {
    return false;
  }
  
  Object writeReplace()
  {
    return new EnumSerializedForm(this.delegate);
  }
  
  private static class EnumSerializedForm
    implements Serializable
  {
    final EnumMap delegate;
    private static final long serialVersionUID = 0L;
    
    EnumSerializedForm(EnumMap paramEnumMap)
    {
      this.delegate = paramEnumMap;
    }
    
    Object readResolve()
    {
      return new ImmutableEnumMap(this.delegate, null);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableEnumMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */