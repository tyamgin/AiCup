package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;

@GwtCompatible(serializable=true)
final class ReverseNaturalOrdering
  extends Ordering
  implements Serializable
{
  static final ReverseNaturalOrdering INSTANCE = new ReverseNaturalOrdering();
  private static final long serialVersionUID = 0L;
  
  public int compare(Comparable paramComparable1, Comparable paramComparable2)
  {
    Preconditions.checkNotNull(paramComparable1);
    if (paramComparable1 == paramComparable2) {
      return 0;
    }
    return paramComparable2.compareTo(paramComparable1);
  }
  
  public Ordering reverse()
  {
    return Ordering.natural();
  }
  
  public Comparable min(Comparable paramComparable1, Comparable paramComparable2)
  {
    return (Comparable)NaturalOrdering.INSTANCE.max(paramComparable1, paramComparable2);
  }
  
  public Comparable min(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3, Comparable... paramVarArgs)
  {
    return (Comparable)NaturalOrdering.INSTANCE.max(paramComparable1, paramComparable2, paramComparable3, paramVarArgs);
  }
  
  public Comparable min(Iterator paramIterator)
  {
    return (Comparable)NaturalOrdering.INSTANCE.max(paramIterator);
  }
  
  public Comparable min(Iterable paramIterable)
  {
    return (Comparable)NaturalOrdering.INSTANCE.max(paramIterable);
  }
  
  public Comparable max(Comparable paramComparable1, Comparable paramComparable2)
  {
    return (Comparable)NaturalOrdering.INSTANCE.min(paramComparable1, paramComparable2);
  }
  
  public Comparable max(Comparable paramComparable1, Comparable paramComparable2, Comparable paramComparable3, Comparable... paramVarArgs)
  {
    return (Comparable)NaturalOrdering.INSTANCE.min(paramComparable1, paramComparable2, paramComparable3, paramVarArgs);
  }
  
  public Comparable max(Iterator paramIterator)
  {
    return (Comparable)NaturalOrdering.INSTANCE.min(paramIterator);
  }
  
  public Comparable max(Iterable paramIterable)
  {
    return (Comparable)NaturalOrdering.INSTANCE.min(paramIterable);
  }
  
  private Object readResolve()
  {
    return INSTANCE;
  }
  
  public String toString()
  {
    return "Ordering.natural().reverse()";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ReverseNaturalOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */