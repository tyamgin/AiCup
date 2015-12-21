package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@GwtCompatible
public abstract class ForwardingTable
  extends ForwardingObject
  implements Table
{
  protected abstract Table delegate();
  
  public Set cellSet()
  {
    return delegate().cellSet();
  }
  
  public void clear()
  {
    delegate().clear();
  }
  
  public Map column(Object paramObject)
  {
    return delegate().column(paramObject);
  }
  
  public Set columnKeySet()
  {
    return delegate().columnKeySet();
  }
  
  public Map columnMap()
  {
    return delegate().columnMap();
  }
  
  public boolean contains(Object paramObject1, Object paramObject2)
  {
    return delegate().contains(paramObject1, paramObject2);
  }
  
  public boolean containsColumn(Object paramObject)
  {
    return delegate().containsColumn(paramObject);
  }
  
  public boolean containsRow(Object paramObject)
  {
    return delegate().containsRow(paramObject);
  }
  
  public boolean containsValue(Object paramObject)
  {
    return delegate().containsValue(paramObject);
  }
  
  public Object get(Object paramObject1, Object paramObject2)
  {
    return delegate().get(paramObject1, paramObject2);
  }
  
  public boolean isEmpty()
  {
    return delegate().isEmpty();
  }
  
  public Object put(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return delegate().put(paramObject1, paramObject2, paramObject3);
  }
  
  public void putAll(Table paramTable)
  {
    delegate().putAll(paramTable);
  }
  
  public Object remove(Object paramObject1, Object paramObject2)
  {
    return delegate().remove(paramObject1, paramObject2);
  }
  
  public Map row(Object paramObject)
  {
    return delegate().row(paramObject);
  }
  
  public Set rowKeySet()
  {
    return delegate().rowKeySet();
  }
  
  public Map rowMap()
  {
    return delegate().rowMap();
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


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingTable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */