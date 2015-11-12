package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Set;

@GwtCompatible
public abstract class ForwardingSetMultimap
  extends ForwardingMultimap
  implements SetMultimap
{
  protected abstract SetMultimap delegate();
  
  public Set entries()
  {
    return delegate().entries();
  }
  
  public Set get(Object paramObject)
  {
    return delegate().get(paramObject);
  }
  
  public Set removeAll(Object paramObject)
  {
    return delegate().removeAll(paramObject);
  }
  
  public Set replaceValues(Object paramObject, Iterable paramIterable)
  {
    return delegate().replaceValues(paramObject, paramIterable);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingSetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */