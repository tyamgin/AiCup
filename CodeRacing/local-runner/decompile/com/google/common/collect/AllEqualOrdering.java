package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.List;

@GwtCompatible(serializable=true)
final class AllEqualOrdering
  extends Ordering
  implements Serializable
{
  static final AllEqualOrdering INSTANCE = new AllEqualOrdering();
  private static final long serialVersionUID = 0L;
  
  public int compare(Object paramObject1, Object paramObject2)
  {
    return 0;
  }
  
  public List sortedCopy(Iterable paramIterable)
  {
    return Lists.newArrayList(paramIterable);
  }
  
  public ImmutableList immutableSortedCopy(Iterable paramIterable)
  {
    return ImmutableList.copyOf(paramIterable);
  }
  
  public Ordering reverse()
  {
    return this;
  }
  
  private Object readResolve()
  {
    return INSTANCE;
  }
  
  public String toString()
  {
    return "Ordering.allEqual()";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AllEqualOrdering.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */