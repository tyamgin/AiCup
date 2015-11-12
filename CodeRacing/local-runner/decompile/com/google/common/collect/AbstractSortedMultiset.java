package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;

@GwtCompatible(emulated=true)
abstract class AbstractSortedMultiset
  extends AbstractMultiset
  implements SortedMultiset
{
  @GwtTransient
  final Comparator comparator;
  private transient SortedMultiset descendingMultiset;
  
  AbstractSortedMultiset()
  {
    this(Ordering.natural());
  }
  
  AbstractSortedMultiset(Comparator paramComparator)
  {
    this.comparator = ((Comparator)Preconditions.checkNotNull(paramComparator));
  }
  
  public NavigableSet elementSet()
  {
    return (NavigableSet)super.elementSet();
  }
  
  NavigableSet createElementSet()
  {
    return new SortedMultisets.NavigableElementSet(this);
  }
  
  public Comparator comparator()
  {
    return this.comparator;
  }
  
  public Multiset.Entry firstEntry()
  {
    Iterator localIterator = entryIterator();
    return localIterator.hasNext() ? (Multiset.Entry)localIterator.next() : null;
  }
  
  public Multiset.Entry lastEntry()
  {
    Iterator localIterator = descendingEntryIterator();
    return localIterator.hasNext() ? (Multiset.Entry)localIterator.next() : null;
  }
  
  public Multiset.Entry pollFirstEntry()
  {
    Iterator localIterator = entryIterator();
    if (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      localEntry = Multisets.immutableEntry(localEntry.getElement(), localEntry.getCount());
      localIterator.remove();
      return localEntry;
    }
    return null;
  }
  
  public Multiset.Entry pollLastEntry()
  {
    Iterator localIterator = descendingEntryIterator();
    if (localIterator.hasNext())
    {
      Multiset.Entry localEntry = (Multiset.Entry)localIterator.next();
      localEntry = Multisets.immutableEntry(localEntry.getElement(), localEntry.getCount());
      localIterator.remove();
      return localEntry;
    }
    return null;
  }
  
  public SortedMultiset subMultiset(Object paramObject1, BoundType paramBoundType1, Object paramObject2, BoundType paramBoundType2)
  {
    Preconditions.checkNotNull(paramBoundType1);
    Preconditions.checkNotNull(paramBoundType2);
    return tailMultiset(paramObject1, paramBoundType1).headMultiset(paramObject2, paramBoundType2);
  }
  
  abstract Iterator descendingEntryIterator();
  
  Iterator descendingIterator()
  {
    return Multisets.iteratorImpl(descendingMultiset());
  }
  
  public SortedMultiset descendingMultiset()
  {
    SortedMultiset localSortedMultiset = this.descendingMultiset;
    return localSortedMultiset == null ? (this.descendingMultiset = createDescendingMultiset()) : localSortedMultiset;
  }
  
  SortedMultiset createDescendingMultiset()
  {
    new DescendingMultiset()
    {
      SortedMultiset forwardMultiset()
      {
        return AbstractSortedMultiset.this;
      }
      
      Iterator entryIterator()
      {
        return AbstractSortedMultiset.this.descendingEntryIterator();
      }
      
      public Iterator iterator()
      {
        return AbstractSortedMultiset.this.descendingIterator();
      }
    };
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractSortedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */