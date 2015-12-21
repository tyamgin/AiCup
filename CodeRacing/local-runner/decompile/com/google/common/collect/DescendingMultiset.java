package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;

@GwtCompatible(emulated=true)
abstract class DescendingMultiset
  extends ForwardingMultiset
  implements SortedMultiset
{
  private transient Comparator comparator;
  private transient NavigableSet elementSet;
  private transient Set entrySet;
  
  abstract SortedMultiset forwardMultiset();
  
  public Comparator comparator()
  {
    Comparator localComparator = this.comparator;
    if (localComparator == null) {
      return this.comparator = Ordering.from(forwardMultiset().comparator()).reverse();
    }
    return localComparator;
  }
  
  public NavigableSet elementSet()
  {
    NavigableSet localNavigableSet = this.elementSet;
    if (localNavigableSet == null) {
      return this.elementSet = new SortedMultisets.NavigableElementSet(this);
    }
    return localNavigableSet;
  }
  
  public Multiset.Entry pollFirstEntry()
  {
    return forwardMultiset().pollLastEntry();
  }
  
  public Multiset.Entry pollLastEntry()
  {
    return forwardMultiset().pollFirstEntry();
  }
  
  public SortedMultiset headMultiset(Object paramObject, BoundType paramBoundType)
  {
    return forwardMultiset().tailMultiset(paramObject, paramBoundType).descendingMultiset();
  }
  
  public SortedMultiset subMultiset(Object paramObject1, BoundType paramBoundType1, Object paramObject2, BoundType paramBoundType2)
  {
    return forwardMultiset().subMultiset(paramObject2, paramBoundType2, paramObject1, paramBoundType1).descendingMultiset();
  }
  
  public SortedMultiset tailMultiset(Object paramObject, BoundType paramBoundType)
  {
    return forwardMultiset().headMultiset(paramObject, paramBoundType).descendingMultiset();
  }
  
  protected Multiset delegate()
  {
    return forwardMultiset();
  }
  
  public SortedMultiset descendingMultiset()
  {
    return forwardMultiset();
  }
  
  public Multiset.Entry firstEntry()
  {
    return forwardMultiset().lastEntry();
  }
  
  public Multiset.Entry lastEntry()
  {
    return forwardMultiset().firstEntry();
  }
  
  abstract Iterator entryIterator();
  
  public Set entrySet()
  {
    Set localSet = this.entrySet;
    return localSet == null ? (this.entrySet = createEntrySet()) : localSet;
  }
  
  Set createEntrySet()
  {
    new Multisets.EntrySet()
    {
      Multiset multiset()
      {
        return DescendingMultiset.this;
      }
      
      public Iterator iterator()
      {
        return DescendingMultiset.this.entryIterator();
      }
      
      public int size()
      {
        return DescendingMultiset.this.forwardMultiset().entrySet().size();
      }
    };
  }
  
  public Iterator iterator()
  {
    return Multisets.iteratorImpl(this);
  }
  
  public Object[] toArray()
  {
    return standardToArray();
  }
  
  public Object[] toArray(Object[] paramArrayOfObject)
  {
    return standardToArray(paramArrayOfObject);
  }
  
  public String toString()
  {
    return entrySet().toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\DescendingMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */