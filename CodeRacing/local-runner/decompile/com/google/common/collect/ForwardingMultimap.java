package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@GwtCompatible
public abstract class ForwardingMultimap
  extends ForwardingObject
  implements Multimap
{
  protected abstract Multimap delegate();
  
  public Map asMap()
  {
    return delegate().asMap();
  }
  
  public void clear()
  {
    delegate().clear();
  }
  
  public boolean containsEntry(Object paramObject1, Object paramObject2)
  {
    return delegate().containsEntry(paramObject1, paramObject2);
  }
  
  public boolean containsKey(Object paramObject)
  {
    return delegate().containsKey(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return delegate().containsValue(paramObject);
  }
  
  public Collection entries()
  {
    return delegate().entries();
  }
  
  public Collection get(Object paramObject)
  {
    return delegate().get(paramObject);
  }
  
  public boolean isEmpty()
  {
    return delegate().isEmpty();
  }
  
  public Multiset keys()
  {
    return delegate().keys();
  }
  
  public Set keySet()
  {
    return delegate().keySet();
  }
  
  public boolean put(Object paramObject1, Object paramObject2)
  {
    return delegate().put(paramObject1, paramObject2);
  }
  
  public boolean putAll(Object paramObject, Iterable paramIterable)
  {
    return delegate().putAll(paramObject, paramIterable);
  }
  
  public boolean putAll(Multimap paramMultimap)
  {
    return delegate().putAll(paramMultimap);
  }
  
  public boolean remove(Object paramObject1, Object paramObject2)
  {
    return delegate().remove(paramObject1, paramObject2);
  }
  
  public Collection removeAll(Object paramObject)
  {
    return delegate().removeAll(paramObject);
  }
  
  public Collection replaceValues(Object paramObject, Iterable paramIterable)
  {
    return delegate().replaceValues(paramObject, paramIterable);
  }
  
  public int size()
  {
    return delegate().size();
  }
  
  public Collection values()
  {
    return delegate().values();
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject == this) || (delegate().equals(paramObject));
  }
  
  public int hashCode()
  {
    return delegate().hashCode();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */