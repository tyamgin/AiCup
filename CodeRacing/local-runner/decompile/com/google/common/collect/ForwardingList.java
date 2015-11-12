package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

@GwtCompatible
public abstract class ForwardingList
  extends ForwardingCollection
  implements List
{
  protected abstract List delegate();
  
  public void add(int paramInt, Object paramObject)
  {
    delegate().add(paramInt, paramObject);
  }
  
  public boolean addAll(int paramInt, Collection paramCollection)
  {
    return delegate().addAll(paramInt, paramCollection);
  }
  
  public Object get(int paramInt)
  {
    return delegate().get(paramInt);
  }
  
  public int indexOf(Object paramObject)
  {
    return delegate().indexOf(paramObject);
  }
  
  public int lastIndexOf(Object paramObject)
  {
    return delegate().lastIndexOf(paramObject);
  }
  
  public ListIterator listIterator()
  {
    return delegate().listIterator();
  }
  
  public ListIterator listIterator(int paramInt)
  {
    return delegate().listIterator(paramInt);
  }
  
  public Object remove(int paramInt)
  {
    return delegate().remove(paramInt);
  }
  
  public Object set(int paramInt, Object paramObject)
  {
    return delegate().set(paramInt, paramObject);
  }
  
  public List subList(int paramInt1, int paramInt2)
  {
    return delegate().subList(paramInt1, paramInt2);
  }
  
  public boolean equals(Object paramObject)
  {
    return (paramObject == this) || (delegate().equals(paramObject));
  }
  
  public int hashCode()
  {
    return delegate().hashCode();
  }
  
  protected boolean standardAdd(Object paramObject)
  {
    add(size(), paramObject);
    return true;
  }
  
  protected boolean standardAddAll(int paramInt, Iterable paramIterable)
  {
    return Lists.addAllImpl(this, paramInt, paramIterable);
  }
  
  protected int standardIndexOf(Object paramObject)
  {
    return Lists.indexOfImpl(this, paramObject);
  }
  
  protected int standardLastIndexOf(Object paramObject)
  {
    return Lists.lastIndexOfImpl(this, paramObject);
  }
  
  protected Iterator standardIterator()
  {
    return listIterator();
  }
  
  protected ListIterator standardListIterator()
  {
    return listIterator(0);
  }
  
  @Beta
  protected ListIterator standardListIterator(int paramInt)
  {
    return Lists.listIteratorImpl(this, paramInt);
  }
  
  @Beta
  protected List standardSubList(int paramInt1, int paramInt2)
  {
    return Lists.subListImpl(this, paramInt1, paramInt2);
  }
  
  @Beta
  protected boolean standardEquals(Object paramObject)
  {
    return Lists.equalsImpl(this, paramObject);
  }
  
  @Beta
  protected int standardHashCode()
  {
    return Lists.hashCodeImpl(this);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */