package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Iterator;

@GwtCompatible(serializable=true)
final class ReverseOrdering
  extends Ordering
  implements Serializable
{
  final Ordering forwardOrder;
  private static final long serialVersionUID = 0L;
  
  ReverseOrdering(Ordering paramOrdering)
  {
    this.forwardOrder = ((Ordering)Preconditions.checkNotNull(paramOrdering));
  }
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    return this.forwardOrder.compare(paramObject2, paramObject1);
  }
  
  public Ordering reverse()
  {
    return this.forwardOrder;
  }
  
  public Object min(Object paramObject1, Object paramObject2)
  {
    return this.forwardOrder.max(paramObject1, paramObject2);
  }
  
  public Object min(Object paramObject1, Object paramObject2, Object paramObject3, Object... paramVarArgs)
  {
    return this.forwardOrder.max(paramObject1, paramObject2, paramObject3, paramVarArgs);
  }
  
  public Object min(Iterator paramIterator)
  {
    return this.forwardOrder.max(paramIterator);
  }
  
  public Object min(Iterable paramIterable)
  {
    return this.forwardOrder.max(paramIterable);
  }
  
  public Object max(Object paramObject1, Object paramObject2)
  {
    return this.forwardOrder.min(paramObject1, paramObject2);
  }
  
  public Object max(Object paramObject1, Object paramObject2, Object paramObject3, Object... paramVarArgs)
  {
    return this.forwardOrder.min(paramObject1, paramObject2, paramObject3, paramVarArgs);
  }
  
  public Object max(Iterator paramIterator)
  {
    return this.forwardOrder.min(paramIterator);
  }
  
  public Object max(Iterable paramIterable)
  {
    return this.forwardOrder.min(paramIterable);
  }
  
  public int hashCode()
  {
    return -this.forwardOrder.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof ReverseOrdering))
    {
      ReverseOrdering localReverseOrdering = (ReverseOrdering)paramObject;
      return this.forwardOrder.equals(localReverseOrdering.forwardOrder);
    }
    return false;
  }
  
  public String toString()
  {
    return this.forwardOrder + ".reverse()";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ReverseOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */