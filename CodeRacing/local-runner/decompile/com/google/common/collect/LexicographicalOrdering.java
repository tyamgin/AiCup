package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Iterator;

@GwtCompatible(serializable=true)
final class LexicographicalOrdering
  extends Ordering
  implements Serializable
{
  final Ordering elementOrder;
  private static final long serialVersionUID = 0L;
  
  LexicographicalOrdering(Ordering paramOrdering)
  {
    this.elementOrder = paramOrdering;
  }
  
  public int compare(Iterable paramIterable1, Iterable paramIterable2)
  {
    Iterator localIterator1 = paramIterable1.iterator();
    Iterator localIterator2 = paramIterable2.iterator();
    while (localIterator1.hasNext())
    {
      if (!localIterator2.hasNext()) {
        return 1;
      }
      int i = this.elementOrder.compare(localIterator1.next(), localIterator2.next());
      if (i != 0) {
        return i;
      }
    }
    if (localIterator2.hasNext()) {
      return -1;
    }
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof LexicographicalOrdering))
    {
      LexicographicalOrdering localLexicographicalOrdering = (LexicographicalOrdering)paramObject;
      return this.elementOrder.equals(localLexicographicalOrdering.elementOrder);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.elementOrder.hashCode() ^ 0x7BB78CF5;
  }
  
  public String toString()
  {
    return this.elementOrder + ".lexicographical()";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\LexicographicalOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */