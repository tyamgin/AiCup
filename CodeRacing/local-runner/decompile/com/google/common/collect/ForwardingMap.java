package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@GwtCompatible
public abstract class ForwardingMap
  extends ForwardingObject
  implements Map
{
  protected abstract Map delegate();
  
  public int size()
  {
    return delegate().size();
  }
  
  public boolean isEmpty()
  {
    return delegate().isEmpty();
  }
  
  public Object remove(Object paramObject)
  {
    return delegate().remove(paramObject);
  }
  
  public void clear()
  {
    delegate().clear();
  }
  
  public boolean containsKey(Object paramObject)
  {
    return delegate().containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return delegate().containsValue(paramObject);
  }
  
  public Object get(Object paramObject)
  {
    return delegate().get(paramObject);
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    return delegate().put(paramObject1, paramObject2);
  }
  
  public void putAll(Map paramMap)
  {
    delegate().putAll(paramMap);
  }
  
  public Set keySet()
  {
    return delegate().keySet();
  }
  
  public Collection values()
  {
    return delegate().values();
  }
  
  public Set entrySet()
  {
    return delegate().entrySet();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject == this) || (delegate().equals(paramObject));
  }
  
  public int hashCode()
  {
    return delegate().hashCode();
  }
  
  protected void standardPutAll(Map paramMap)
  {
    Maps.putAllImpl(this, paramMap);
  }
  
  @Beta
  protected Object standardRemove(Object paramObject)
  {
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (Objects.equal(localEntry.getKey(), paramObject))
      {
        Object localObject = localEntry.getValue();
        localIterator.remove();
        return localObject;
      }
    }
    return null;
  }
  
  protected void standardClear()
  {
    Iterators.clear(entrySet().iterator());
  }
  
  @Beta
  protected boolean standardContainsKey(Object paramObject)
  {
    return Maps.containsKeyImpl(this, paramObject);
  }
  
  protected boolean standardContainsValue(Object paramObject)
  {
    return Maps.containsValueImpl(this, paramObject);
  }
  
  protected boolean standardIsEmpty()
  {
    return !entrySet().iterator().hasNext();
  }
  
  protected boolean standardEquals(Object paramObject)
  {
    return Maps.equalsImpl(this, paramObject);
  }
  
  protected int standardHashCode()
  {
    return Sets.hashCodeImpl(entrySet());
  }
  
  protected String standardToString()
  {
    return Maps.toStringImpl(this);
  }
  
  @Beta
  protected abstract class StandardEntrySet
    extends Maps.EntrySet
  {
    public StandardEntrySet() {}
    
    Map map()
    {
      return ForwardingMap.this;
    }
  }
  
  @Beta
  protected class StandardValues
    extends Maps.Values
  {
    public StandardValues() {}
    
    Map map()
    {
      return ForwardingMap.this;
    }
  }
  
  @Beta
  protected class StandardKeySet
    extends Maps.KeySet
  {
    public StandardKeySet() {}
    
    Map map()
    {
      return ForwardingMap.this;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */