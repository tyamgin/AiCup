package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

@GwtCompatible
abstract class AbstractMultiset
  extends AbstractCollection
  implements Multiset
{
  private transient Set elementSet;
  private transient Set entrySet;
  
  public int size()
  {
    return Multisets.sizeImpl(this);
  }
  
  public boolean isEmpty()
  {
    return entrySet().isEmpty();
  }
  
  public boolean contains(Object paramObject)
  {
    return count(paramObject) > 0;
  }
  
  public Iterator iterator()
  {
    return Multisets.iteratorImpl(this);
  }
  
  public int count(Object paramObject)
  {
    Iterator localIterator = entrySet().iterator();
    while (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      if (Objects.equal(localEntry.getElement(), paramObject)) {
        return localEntry.getCount();
      }
    }
    return 0;
  }
  
  public boolean add(Object paramObject)
  {
    add(paramObject, 1);
    return true;
  }
  
  public int add(Object paramObject, int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  public boolean remove(Object paramObject)
  {
    return remove(paramObject, 1) > 0;
  }
  
  public int remove(Object paramObject, int paramInt)
  {
    throw new UnsupportedOperationException();
  }
  
  public int setCount(Object paramObject, int paramInt)
  {
    return Multisets.setCountImpl(this, paramObject, paramInt);
  }
  
  public boolean setCount(Object paramObject, int paramInt1, int paramInt2)
  {
    return Multisets.setCountImpl(this, paramObject, paramInt1, paramInt2);
  }
  
  public boolean addAll(Collection paramCollection)
  {
    return Multisets.addAllImpl(this, paramCollection);
  }
  
  public boolean removeAll(Collection paramCollection)
  {
    return Multisets.removeAllImpl(this, paramCollection);
  }
  
  public boolean retainAll(Collection paramCollection)
  {
    return Multisets.retainAllImpl(this, paramCollection);
  }
  
  public void clear()
  {
    Iterators.clear(entryIterator());
  }
  
  public Set elementSet()
  {
    Set localSet = this.elementSet;
    if (localSet == null) {
      this.elementSet = (localSet = createElementSet());
    }
    return localSet;
  }
  
  Set createElementSet()
  {
    return new ElementSet();
  }
  
  abstract Iterator entryIterator();
  
  abstract int distinctElements();
  
  public Set entrySet()
  {
    Set localSet = this.entrySet;
    return localSet == null ? (this.entrySet = createEntrySet()) : localSet;
  }
  
  Set createEntrySet()
  {
    return new EntrySet();
  }
  
  public boolean equals(Object paramObject)
  {
    return Multisets.equalsImpl(this, paramObject);
  }
  
  public int hashCode()
  {
    return entrySet().hashCode();
  }
  
  public String toString()
  {
    return entrySet().toString();
  }
  
  class EntrySet
    extends Multisets.EntrySet
  {
    EntrySet() {}
    
    Multiset multiset()
    {
      return AbstractMultiset.this;
    }
    
    public Iterator iterator()
    {
      return AbstractMultiset.this.entryIterator();
    }
    
    public int size()
    {
      return AbstractMultiset.this.distinctElements();
    }
  }
  
  class ElementSet
    extends Multisets.ElementSet
  {
    ElementSet() {}
    
    Multiset multiset()
    {
      return AbstractMultiset.this;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */