package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.util.Comparator;

@GwtCompatible(emulated=true)
final class ImmutableSortedAsList
  extends RegularImmutableAsList
  implements SortedIterable
{
  ImmutableSortedAsList(ImmutableSortedSet paramImmutableSortedSet, ImmutableList paramImmutableList)
  {
    super(paramImmutableSortedSet, paramImmutableList);
  }
  
  ImmutableSortedSet delegateCollection()
  {
    return (ImmutableSortedSet)super.delegateCollection();
  }
  
  public Comparator comparator()
  {
    return delegateCollection().comparator();
  }
  
  @GwtIncompatible("ImmutableSortedSet.indexOf")
  public int indexOf(Object paramObject)
  {
    int i = delegateCollection().indexOf(paramObject);
    return (i >= 0) && (get(i).equals(paramObject)) ? i : -1;
  }
  
  @GwtIncompatible("ImmutableSortedSet.indexOf")
  public int lastIndexOf(Object paramObject)
  {
    return indexOf(paramObject);
  }
  
  public boolean contains(Object paramObject)
  {
    return indexOf(paramObject) >= 0;
  }
  
  @GwtIncompatible("super.subListUnchecked does not exist; inherited subList is valid if slow")
  ImmutableList subListUnchecked(int paramInt1, int paramInt2)
  {
    return new RegularImmutableSortedSet(super.subListUnchecked(paramInt1, paramInt2), comparator()).asList();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ImmutableSortedAsList.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */