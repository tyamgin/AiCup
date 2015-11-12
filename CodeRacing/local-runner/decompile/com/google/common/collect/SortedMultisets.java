package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.SortedSet;

@GwtCompatible(emulated=true)
final class SortedMultisets
{
  private static Object getElementOrThrow(Multiset.Entry paramEntry)
  {
    if (paramEntry == null) {
      throw new NoSuchElementException();
    }
    return paramEntry.getElement();
  }
  
  private static Object getElementOrNull(Multiset.Entry paramEntry)
  {
    return paramEntry == null ? null : paramEntry.getElement();
  }
  
  @GwtIncompatible("Navigable")
  static class NavigableElementSet
    extends SortedMultisets.ElementSet
    implements NavigableSet
  {
    NavigableElementSet(SortedMultiset paramSortedMultiset)
    {
      super();
    }
    
    public Object lower(Object paramObject)
    {
      return SortedMultisets.getElementOrNull(multiset().headMultiset(paramObject, BoundType.OPEN).lastEntry());
    }
    
    public Object floor(Object paramObject)
    {
      return SortedMultisets.getElementOrNull(multiset().headMultiset(paramObject, BoundType.CLOSED).lastEntry());
    }
    
    public Object ceiling(Object paramObject)
    {
      return SortedMultisets.getElementOrNull(multiset().tailMultiset(paramObject, BoundType.CLOSED).firstEntry());
    }
    
    public Object higher(Object paramObject)
    {
      return SortedMultisets.getElementOrNull(multiset().tailMultiset(paramObject, BoundType.OPEN).firstEntry());
    }
    
    public NavigableSet descendingSet()
    {
      return new NavigableElementSet(multiset().descendingMultiset());
    }
    
    public Iterator descendingIterator()
    {
      return descendingSet().iterator();
    }
    
    public Object pollFirst()
    {
      return SortedMultisets.getElementOrNull(multiset().pollFirstEntry());
    }
    
    public Object pollLast()
    {
      return SortedMultisets.getElementOrNull(multiset().pollLastEntry());
    }
    
    public NavigableSet subSet(Object paramObject1, boolean paramBoolean1, Object paramObject2, boolean paramBoolean2)
    {
      return new NavigableElementSet(multiset().subMultiset(paramObject1, BoundType.forBoolean(paramBoolean1), paramObject2, BoundType.forBoolean(paramBoolean2)));
    }
    
    public NavigableSet headSet(Object paramObject, boolean paramBoolean)
    {
      return new NavigableElementSet(multiset().headMultiset(paramObject, BoundType.forBoolean(paramBoolean)));
    }
    
    public NavigableSet tailSet(Object paramObject, boolean paramBoolean)
    {
      return new NavigableElementSet(multiset().tailMultiset(paramObject, BoundType.forBoolean(paramBoolean)));
    }
  }
  
  static class ElementSet
    extends Multisets.ElementSet
    implements SortedSet
  {
    private final SortedMultiset multiset;
    
    ElementSet(SortedMultiset paramSortedMultiset)
    {
      this.multiset = paramSortedMultiset;
    }
    
    final SortedMultiset multiset()
    {
      return this.multiset;
    }
    
    public Comparator comparator()
    {
      return multiset().comparator();
    }
    
    public SortedSet subSet(Object paramObject1, Object paramObject2)
    {
      return multiset().subMultiset(paramObject1, BoundType.CLOSED, paramObject2, BoundType.OPEN).elementSet();
    }
    
    public SortedSet headSet(Object paramObject)
    {
      return multiset().headMultiset(paramObject, BoundType.OPEN).elementSet();
    }
    
    public SortedSet tailSet(Object paramObject)
    {
      return multiset().tailMultiset(paramObject, BoundType.CLOSED).elementSet();
    }
    
    public Object first()
    {
      return SortedMultisets.getElementOrThrow(multiset().firstEntry());
    }
    
    public Object last()
    {
      return SortedMultisets.getElementOrThrow(multiset().lastEntry());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SortedMultisets.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */