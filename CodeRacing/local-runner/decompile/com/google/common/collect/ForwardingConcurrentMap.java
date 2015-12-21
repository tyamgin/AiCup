package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.concurrent.ConcurrentMap;

@GwtCompatible
public abstract class ForwardingConcurrentMap
  extends ForwardingMap
  implements ConcurrentMap
{
  protected abstract ConcurrentMap delegate();
  
  public Object putIfAbsent(Object paramObject1, Object paramObject2)
  {
    return delegate().putIfAbsent(paramObject1, paramObject2);
  }
  
  public boolean remove(Object paramObject1, Object paramObject2)
  {
    return delegate().remove(paramObject1, paramObject2);
  }
  
  public Object replace(Object paramObject1, Object paramObject2)
  {
    return delegate().replace(paramObject1, paramObject2);
  }
  
  public boolean replace(Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return delegate().replace(paramObject1, paramObject2, paramObject3);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingConcurrentMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */