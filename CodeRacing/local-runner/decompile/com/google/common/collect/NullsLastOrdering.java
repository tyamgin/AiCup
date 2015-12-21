package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible(serializable=true)
final class NullsLastOrdering
  extends Ordering
  implements Serializable
{
  final Ordering ordering;
  private static final long serialVersionUID = 0L;
  
  NullsLastOrdering(Ordering paramOrdering)
  {
    this.ordering = paramOrdering;
  }
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 == paramObject2) {
      return 0;
    }
    if (paramObject1 == null) {
      return 1;
    }
    if (paramObject2 == null) {
      return -1;
    }
    return this.ordering.compare(paramObject1, paramObject2);
  }
  
  public Ordering reverse()
  {
    return this.ordering.reverse().nullsFirst();
  }
  
  public Ordering nullsFirst()
  {
    return this.ordering.nullsFirst();
  }
  
  public Ordering nullsLast()
  {
    return this;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof NullsLastOrdering))
    {
      NullsLastOrdering localNullsLastOrdering = (NullsLastOrdering)paramObject;
      return this.ordering.equals(localNullsLastOrdering.ordering);
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.ordering.hashCode() ^ 0xC9177248;
  }
  
  public String toString()
  {
    return this.ordering + ".nullsLast()";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\NullsLastOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */