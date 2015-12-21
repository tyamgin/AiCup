package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.NoSuchElementException;
import java.util.Queue;

@GwtCompatible
public abstract class ForwardingQueue
  extends ForwardingCollection
  implements Queue
{
  protected abstract Queue delegate();
  
  public boolean offer(Object paramObject)
  {
    return delegate().offer(paramObject);
  }
  
  public Object poll()
  {
    return delegate().poll();
  }
  
  public Object remove()
  {
    return delegate().remove();
  }
  
  public Object peek()
  {
    return delegate().peek();
  }
  
  public Object element()
  {
    return delegate().element();
  }
  
  protected boolean standardOffer(Object paramObject)
  {
    try
    {
      return add(paramObject);
    }
    catch (IllegalStateException localIllegalStateException) {}
    return false;
  }
  
  protected Object standardPeek()
  {
    try
    {
      return element();
    }
    catch (NoSuchElementException localNoSuchElementException) {}
    return null;
  }
  
  protected Object standardPoll()
  {
    try
    {
      return remove();
    }
    catch (NoSuchElementException localNoSuchElementException) {}
    return null;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */