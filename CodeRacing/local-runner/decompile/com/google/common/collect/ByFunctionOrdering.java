package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;

@GwtCompatible(serializable=true)
final class ByFunctionOrdering
  extends Ordering
  implements Serializable
{
  final Function function;
  final Ordering ordering;
  private static final long serialVersionUID = 0L;
  
  ByFunctionOrdering(Function paramFunction, Ordering paramOrdering)
  {
    this.function = ((Function)Preconditions.checkNotNull(paramFunction));
    this.ordering = ((Ordering)Preconditions.checkNotNull(paramOrdering));
  }
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    return this.ordering.compare(this.function.apply(paramObject1), this.function.apply(paramObject2));
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof ByFunctionOrdering))
    {
      ByFunctionOrdering localByFunctionOrdering = (ByFunctionOrdering)paramObject;
      return (this.function.equals(localByFunctionOrdering.function)) && (this.ordering.equals(localByFunctionOrdering.ordering));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { this.function, this.ordering });
  }
  
  public String toString()
  {
    return this.ordering + ".onResultOf(" + this.function + ")";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ByFunctionOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */