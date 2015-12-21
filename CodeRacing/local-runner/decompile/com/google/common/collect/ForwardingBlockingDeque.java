package com.google.common.collect;

import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

public abstract class ForwardingBlockingDeque
  extends ForwardingDeque
  implements BlockingDeque
{
  protected abstract BlockingDeque delegate();
  
  public int remainingCapacity()
  {
    return delegate().remainingCapacity();
  }
  
  public void putFirst(Object paramObject)
    throws InterruptedException
  {
    delegate().putFirst(paramObject);
  }
  
  public void putLast(Object paramObject)
    throws InterruptedException
  {
    delegate().putLast(paramObject);
  }
  
  public boolean offerFirst(Object paramObject, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate().offerFirst(paramObject, paramLong, paramTimeUnit);
  }
  
  public boolean offerLast(Object paramObject, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate().offerLast(paramObject, paramLong, paramTimeUnit);
  }
  
  public Object takeFirst()
    throws InterruptedException
  {
    return delegate().takeFirst();
  }
  
  public Object takeLast()
    throws InterruptedException
  {
    return delegate().takeLast();
  }
  
  public Object pollFirst(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate().pollFirst(paramLong, paramTimeUnit);
  }
  
  public Object pollLast(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate().pollLast(paramLong, paramTimeUnit);
  }
  
  public void put(Object paramObject)
    throws InterruptedException
  {
    delegate().put(paramObject);
  }
  
  public boolean offer(Object paramObject, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate().offer(paramObject, paramLong, paramTimeUnit);
  }
  
  public Object take()
    throws InterruptedException
  {
    return delegate().take();
  }
  
  public Object poll(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return delegate().poll(paramLong, paramTimeUnit);
  }
  
  public int drainTo(Collection paramCollection)
  {
    return delegate().drainTo(paramCollection);
  }
  
  public int drainTo(Collection paramCollection, int paramInt)
  {
    return delegate().drainTo(paramCollection, paramInt);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingBlockingDeque.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */