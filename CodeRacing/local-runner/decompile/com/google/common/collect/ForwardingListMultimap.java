package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.List;

@GwtCompatible
public abstract class ForwardingListMultimap
  extends ForwardingMultimap
  implements ListMultimap
{
  protected abstract ListMultimap delegate();
  
  public List get(Object paramObject)
  {
    return delegate().get(paramObject);
  }
  
  public List removeAll(Object paramObject)
  {
    return delegate().removeAll(paramObject);
  }
  
  public List replaceValues(Object paramObject, Iterable paramIterable)
  {
    return delegate().replaceValues(paramObject, paramIterable);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingListMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */