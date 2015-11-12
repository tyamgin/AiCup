package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.NavigableSet;

@GwtCompatible(emulated=true)
final class UnmodifiableSortedMultiset
  extends Multisets.UnmodifiableMultiset
  implements SortedMultiset
{
  private transient UnmodifiableSortedMultiset descendingMultiset;
  private static final long serialVersionUID = 0L;
  
  UnmodifiableSortedMultiset(SortedMultiset paramSortedMultiset)
  {
    super(paramSortedMultiset);
  }
  
  protected SortedMultiset delegate()
  {
    return (SortedMultiset)super.delegate();
  }
  
  public Comparator comparator()
  {
    return delegate().comparator();
  }
  
  NavigableSet createElementSet()
  {
    return Sets.unmodifiableNavigableSet(delegate().elementSet());
  }
  
  public NavigableSet elementSet()
  {
    return (NavigableSet)super.elementSet();
  }
  
  public SortedMultiset descendingMultiset()
  {
    UnmodifiableSortedMultiset localUnmodifiableSortedMultiset = this.descendingMultiset;
    if (localUnmodifiableSortedMultiset == null)
    {
      localUnmodifiableSortedMultiset = new UnmodifiableSortedMultiset(delegate().descendingMultiset());
      localUnmodifiableSortedMultiset.descendingMultiset = this;
      return this.descendingMultiset = localUnmodifiableSortedMultiset;
    }
    return localUnmodifiableSortedMultiset;
  }
  
  public Multiset.Entry firstEntry()
  {
    return delegate().firstEntry();
  }
  
  public Multiset.Entry lastEntry()
  {
    return delegate().lastEntry();
  }
  
  public Multiset.Entry pollFirstEntry()
  {
    throw new UnsupportedOperationException();
  }
  
  public Multiset.Entry pollLastEntry()
  {
    throw new UnsupportedOperationException();
  }
  
  public SortedMultiset headMultiset(Object paramObject, BoundType paramBoundType)
  {
    return Multisets.unmodifiableSortedMultiset(delegate().headMultiset(paramObject, paramBoundType));
  }
  
  public SortedMultiset subMultiset(Object paramObject1, BoundType paramBoundType1, Object paramObject2, BoundType paramBoundType2)
  {
    return Multisets.unmodifiableSortedMultiset(delegate().subMultiset(paramObject1, paramBoundType1, paramObject2, paramBoundType2));
  }
  
  public SortedMultiset tailMultiset(Object paramObject, BoundType paramBoundType)
  {
    return Multisets.unmodifiableSortedMultiset(delegate().tailMultiset(paramObject, paramBoundType));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\UnmodifiableSortedMultiset.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */