package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Comparator;

@GwtCompatible(serializable=true)
final class CompoundOrdering
  extends Ordering
  implements Serializable
{
  final ImmutableList comparators;
  private static final long serialVersionUID = 0L;
  
  CompoundOrdering(Comparator paramComparator1, Comparator paramComparator2)
  {
    this.comparators = ImmutableList.of(paramComparator1, paramComparator2);
  }
  
  CompoundOrdering(Iterable paramIterable)
  {
    this.comparators = ImmutableList.copyOf(paramIterable);
  }
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    int i = this.comparators.size();
    for (int j = 0; j < i; j++)
    {
      int k = ((Comparator)this.comparators.get(j)).compare(paramObject1, paramObject2);
      if (k != 0) {
        return k;
      }
    }
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof CompoundOrdering))
    {
      CompoundOrdering localCompoundOrdering = (CompoundOrdering)paramObject;
      return this.comparators.equals(localCompoundOrdering.comparators);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.comparators.hashCode();
  }
  
  public String toString()
  {
    return "Ordering.compound(" + this.comparators + ")";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\CompoundOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */