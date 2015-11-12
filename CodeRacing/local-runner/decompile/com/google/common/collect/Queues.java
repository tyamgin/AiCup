package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public final class Queues
{
  public static ArrayBlockingQueue newArrayBlockingQueue(int paramInt)
  {
    return new ArrayBlockingQueue(paramInt);
  }
  
  public static ArrayDeque newArrayDeque()
  {
    return new ArrayDeque();
  }
  
  public static ArrayDeque newArrayDeque(Iterable paramIterable)
  {
    if ((paramIterable instanceof Collection)) {
      return new ArrayDeque(Collections2.cast(paramIterable));
    }
    ArrayDeque localArrayDeque = new ArrayDeque();
    Iterables.addAll(localArrayDeque, paramIterable);
    return localArrayDeque;
  }
  
  public static ConcurrentLinkedQueue newConcurrentLinkedQueue()
  {
    return new ConcurrentLinkedQueue();
  }
  
  public static ConcurrentLinkedQueue newConcurrentLinkedQueue(Iterable paramIterable)
  {
    if ((paramIterable instanceof Collection)) {
      return new ConcurrentLinkedQueue(Collections2.cast(paramIterable));
    }
    ConcurrentLinkedQueue localConcurrentLinkedQueue = new ConcurrentLinkedQueue();
    Iterables.addAll(localConcurrentLinkedQueue, paramIterable);
    return localConcurrentLinkedQueue;
  }
  
  public static LinkedBlockingDeque newLinkedBlockingDeque()
  {
    return new LinkedBlockingDeque();
  }
  
  public static LinkedBlockingDeque newLinkedBlockingDeque(int paramInt)
  {
    return new LinkedBlockingDeque(paramInt);
  }
  
  public static LinkedBlockingDeque newLinkedBlockingDeque(Iterable paramIterable)
  {
    if ((paramIterable instanceof Collection)) {
      return new LinkedBlockingDeque(Collections2.cast(paramIterable));
    }
    LinkedBlockingDeque localLinkedBlockingDeque = new LinkedBlockingDeque();
    Iterables.addAll(localLinkedBlockingDeque, paramIterable);
    return localLinkedBlockingDeque;
  }
  
  public static LinkedBlockingQueue newLinkedBlockingQueue()
  {
    return new LinkedBlockingQueue();
  }
  
  public static LinkedBlockingQueue newLinkedBlockingQueue(int paramInt)
  {
    return new LinkedBlockingQueue(paramInt);
  }
  
  public static LinkedBlockingQueue newLinkedBlockingQueue(Iterable paramIterable)
  {
    if ((paramIterable instanceof Collection)) {
      return new LinkedBlockingQueue(Collections2.cast(paramIterable));
    }
    LinkedBlockingQueue localLinkedBlockingQueue = new LinkedBlockingQueue();
    Iterables.addAll(localLinkedBlockingQueue, paramIterable);
    return localLinkedBlockingQueue;
  }
  
  public static PriorityBlockingQueue newPriorityBlockingQueue()
  {
    return new PriorityBlockingQueue();
  }
  
  public static PriorityBlockingQueue newPriorityBlockingQueue(Iterable paramIterable)
  {
    if ((paramIterable instanceof Collection)) {
      return new PriorityBlockingQueue(Collections2.cast(paramIterable));
    }
    PriorityBlockingQueue localPriorityBlockingQueue = new PriorityBlockingQueue();
    Iterables.addAll(localPriorityBlockingQueue, paramIterable);
    return localPriorityBlockingQueue;
  }
  
  public static PriorityQueue newPriorityQueue()
  {
    return new PriorityQueue();
  }
  
  public static PriorityQueue newPriorityQueue(Iterable paramIterable)
  {
    if ((paramIterable instanceof Collection)) {
      return new PriorityQueue(Collections2.cast(paramIterable));
    }
    PriorityQueue localPriorityQueue = new PriorityQueue();
    Iterables.addAll(localPriorityQueue, paramIterable);
    return localPriorityQueue;
  }
  
  public static SynchronousQueue newSynchronousQueue()
  {
    return new SynchronousQueue();
  }
  
  @Beta
  public static int drain(BlockingQueue paramBlockingQueue, Collection paramCollection, int paramInt, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    Preconditions.checkNotNull(paramCollection);
    long l = System.nanoTime() + paramTimeUnit.toNanos(paramLong);
    int i = 0;
    while (i < paramInt)
    {
      i += paramBlockingQueue.drainTo(paramCollection, paramInt - i);
      if (i < paramInt)
      {
        Object localObject = paramBlockingQueue.poll(l - System.nanoTime(), TimeUnit.NANOSECONDS);
        if (localObject == null) {
          break;
        }
        paramCollection.add(localObject);
        i++;
      }
    }
    return i;
  }
  
  @Beta
  public static int drainUninterruptibly(BlockingQueue paramBlockingQueue, Collection paramCollection, int paramInt, long paramLong, TimeUnit paramTimeUnit)
  {
    Preconditions.checkNotNull(paramCollection);
    long l = System.nanoTime() + paramTimeUnit.toNanos(paramLong);
    int i = 0;
    int j = 0;
    try
    {
      while (i < paramInt)
      {
        i += paramBlockingQueue.drainTo(paramCollection, paramInt - i);
        if (i < paramInt)
        {
          Object localObject1;
          for (;;)
          {
            try
            {
              localObject1 = paramBlockingQueue.poll(l - System.nanoTime(), TimeUnit.NANOSECONDS);
            }
            catch (InterruptedException localInterruptedException)
            {
              j = 1;
            }
          }
          if (localObject1 == null) {
            break;
          }
          paramCollection.add(localObject1);
          i++;
        }
      }
    }
    finally
    {
      if (j != 0) {
        Thread.currentThread().interrupt();
      }
    }
    return i;
  }
  
  @Beta
  public static Queue synchronizedQueue(Queue paramQueue)
  {
    return Synchronized.queue(paramQueue, null);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Queues.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */