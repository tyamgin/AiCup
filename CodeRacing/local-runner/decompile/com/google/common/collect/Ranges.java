package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;

@GwtCompatible
@Beta
@Deprecated
public final class Ranges
{
  public static Range open(Comparable paramComparable1, Comparable paramComparable2)
  {
    return Range.open(paramComparable1, paramComparable2);
  }
  
  public static Range closed(Comparable paramComparable1, Comparable paramComparable2)
  {
    return Range.closed(paramComparable1, paramComparable2);
  }
  
  public static Range closedOpen(Comparable paramComparable1, Comparable paramComparable2)
  {
    return Range.closedOpen(paramComparable1, paramComparable2);
  }
  
  public static Range openClosed(Comparable paramComparable1, Comparable paramComparable2)
  {
    return Range.openClosed(paramComparable1, paramComparable2);
  }
  
  public static Range range(Comparable paramComparable1, BoundType paramBoundType1, Comparable paramComparable2, BoundType paramBoundType2)
  {
    return Range.range(paramComparable1, paramBoundType1, paramComparable2, paramBoundType2);
  }
  
  public static Range lessThan(Comparable paramComparable)
  {
    return Range.lessThan(paramComparable);
  }
  
  public static Range atMost(Comparable paramComparable)
  {
    return Range.atMost(paramComparable);
  }
  
  public static Range upTo(Comparable paramComparable, BoundType paramBoundType)
  {
    return Range.upTo(paramComparable, paramBoundType);
  }
  
  public static Range greaterThan(Comparable paramComparable)
  {
    return Range.greaterThan(paramComparable);
  }
  
  public static Range atLeast(Comparable paramComparable)
  {
    return Range.atLeast(paramComparable);
  }
  
  public static Range downTo(Comparable paramComparable, BoundType paramBoundType)
  {
    return Range.downTo(paramComparable, paramBoundType);
  }
  
  public static Range all()
  {
    return Range.all();
  }
  
  public static Range singleton(Comparable paramComparable)
  {
    return Range.singleton(paramComparable);
  }
  
  public static Range encloseAll(Iterable paramIterable)
  {
    return Range.encloseAll(paramIterable);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Ranges.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */