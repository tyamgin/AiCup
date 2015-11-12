package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Beta
public final class Monitor
{
  private final boolean fair;
  private final ReentrantLock lock;
  private final ArrayList activeGuards = Lists.newArrayListWithCapacity(1);
  
  public Monitor()
  {
    this(false);
  }
  
  public Monitor(boolean paramBoolean)
  {
    this.fair = paramBoolean;
    this.lock = new ReentrantLock(paramBoolean);
  }
  
  public void enter()
  {
    this.lock.lock();
  }
  
  public void enterInterruptibly()
    throws InterruptedException
  {
    this.lock.lockInterruptibly();
  }
  
  public boolean enter(long paramLong, TimeUnit paramTimeUnit)
  {
    ReentrantLock localReentrantLock = this.lock;
    if ((!this.fair) && (localReentrantLock.tryLock())) {
      return true;
    }
    long l1 = System.nanoTime();
    long l2 = paramTimeUnit.toNanos(paramLong);
    long l3 = l2;
    int i = 0;
    try
    {
      boolean bool = localReentrantLock.tryLock(l3, TimeUnit.NANOSECONDS);
      return bool;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;)
      {
        i = 1;
        l3 = l2 - (System.nanoTime() - l1);
      }
    }
    finally
    {
      if (i != 0) {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  public boolean enterInterruptibly(long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    return this.lock.tryLock(paramLong, paramTimeUnit);
  }
  
  public boolean tryEnter()
  {
    return this.lock.tryLock();
  }
  
  public void enterWhen(Guard paramGuard)
    throws InterruptedException
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    ReentrantLock localReentrantLock = this.lock;
    boolean bool = localReentrantLock.isHeldByCurrentThread();
    int i = 0;
    localReentrantLock.lockInterruptibly();
    try
    {
      waitInterruptibly(paramGuard, bool);
      i = 1;
    }
    finally
    {
      if (i == 0) {
        localReentrantLock.unlock();
      }
    }
  }
  
  public void enterWhenUninterruptibly(Guard paramGuard)
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    ReentrantLock localReentrantLock = this.lock;
    boolean bool = localReentrantLock.isHeldByCurrentThread();
    int i = 0;
    localReentrantLock.lock();
    try
    {
      waitUninterruptibly(paramGuard, bool);
      i = 1;
    }
    finally
    {
      if (i == 0) {
        localReentrantLock.unlock();
      }
    }
  }
  
  public boolean enterWhen(Guard paramGuard, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    ReentrantLock localReentrantLock = this.lock;
    boolean bool1 = localReentrantLock.isHeldByCurrentThread();
    long l1;
    if ((!this.fair) && (localReentrantLock.tryLock()))
    {
      l1 = paramTimeUnit.toNanos(paramLong);
    }
    else
    {
      long l2 = System.nanoTime();
      if (!localReentrantLock.tryLock(paramLong, paramTimeUnit)) {
        return false;
      }
      l1 = paramTimeUnit.toNanos(paramLong) - (System.nanoTime() - l2);
    }
    boolean bool2 = false;
    try
    {
      bool2 = waitInterruptibly(paramGuard, l1, bool1);
    }
    finally
    {
      if (!bool2) {
        localReentrantLock.unlock();
      }
    }
    return bool2;
  }
  
  public boolean enterWhenUninterruptibly(Guard paramGuard, long paramLong, TimeUnit paramTimeUnit)
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    ReentrantLock localReentrantLock = this.lock;
    boolean bool1 = localReentrantLock.isHeldByCurrentThread();
    int i = 0;
    try
    {
      long l1;
      if ((!this.fair) && (localReentrantLock.tryLock()))
      {
        l1 = paramTimeUnit.toNanos(paramLong);
      }
      else
      {
        long l2 = System.nanoTime();
        long l3 = paramTimeUnit.toNanos(paramLong);
        l1 = l3;
        for (;;)
        {
          try
          {
            if (localReentrantLock.tryLock(l1, TimeUnit.NANOSECONDS))
            {
              l1 = l3 - (System.nanoTime() - l2);
              break;
            }
            boolean bool4 = false;
            l1 = l3 - (System.nanoTime() - l2);
            return bool4;
          }
          catch (InterruptedException localInterruptedException)
          {
            i = 1;
          }
          finally
          {
            l1 = l3 - (System.nanoTime() - l2);
          }
        }
      }
      boolean bool2 = false;
      try
      {
        bool2 = waitUninterruptibly(paramGuard, l1, bool1);
      }
      finally
      {
        if (!bool2) {
          localReentrantLock.unlock();
        }
      }
      boolean bool3 = bool2;
      return bool3;
    }
    finally
    {
      if (i != 0) {
        Thread.currentThread().interrupt();
      }
    }
  }
  
  public boolean enterIf(Guard paramGuard)
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    ReentrantLock localReentrantLock = this.lock;
    localReentrantLock.lock();
    boolean bool = false;
    try
    {
      bool = paramGuard.isSatisfied();
    }
    finally
    {
      if (!bool) {
        localReentrantLock.unlock();
      }
    }
    return bool;
  }
  
  public boolean enterIfInterruptibly(Guard paramGuard)
    throws InterruptedException
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    ReentrantLock localReentrantLock = this.lock;
    localReentrantLock.lockInterruptibly();
    boolean bool = false;
    try
    {
      bool = paramGuard.isSatisfied();
    }
    finally
    {
      if (!bool) {
        localReentrantLock.unlock();
      }
    }
    return bool;
  }
  
  public boolean enterIf(Guard paramGuard, long paramLong, TimeUnit paramTimeUnit)
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    ReentrantLock localReentrantLock = this.lock;
    if (!enter(paramLong, paramTimeUnit)) {
      return false;
    }
    boolean bool = false;
    try
    {
      bool = paramGuard.isSatisfied();
    }
    finally
    {
      if (!bool) {
        localReentrantLock.unlock();
      }
    }
    return bool;
  }
  
  public boolean enterIfInterruptibly(Guard paramGuard, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    ReentrantLock localReentrantLock = this.lock;
    if (!localReentrantLock.tryLock(paramLong, paramTimeUnit)) {
      return false;
    }
    boolean bool = false;
    try
    {
      bool = paramGuard.isSatisfied();
    }
    finally
    {
      if (!bool) {
        localReentrantLock.unlock();
      }
    }
    return bool;
  }
  
  public boolean tryEnterIf(Guard paramGuard)
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    ReentrantLock localReentrantLock = this.lock;
    if (!localReentrantLock.tryLock()) {
      return false;
    }
    boolean bool = false;
    try
    {
      bool = paramGuard.isSatisfied();
    }
    finally
    {
      if (!bool) {
        localReentrantLock.unlock();
      }
    }
    return bool;
  }
  
  public void waitFor(Guard paramGuard)
    throws InterruptedException
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    if (!this.lock.isHeldByCurrentThread()) {
      throw new IllegalMonitorStateException();
    }
    waitInterruptibly(paramGuard, true);
  }
  
  public void waitForUninterruptibly(Guard paramGuard)
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    if (!this.lock.isHeldByCurrentThread()) {
      throw new IllegalMonitorStateException();
    }
    waitUninterruptibly(paramGuard, true);
  }
  
  public boolean waitFor(Guard paramGuard, long paramLong, TimeUnit paramTimeUnit)
    throws InterruptedException
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    if (!this.lock.isHeldByCurrentThread()) {
      throw new IllegalMonitorStateException();
    }
    return waitInterruptibly(paramGuard, paramTimeUnit.toNanos(paramLong), true);
  }
  
  public boolean waitForUninterruptibly(Guard paramGuard, long paramLong, TimeUnit paramTimeUnit)
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    if (!this.lock.isHeldByCurrentThread()) {
      throw new IllegalMonitorStateException();
    }
    return waitUninterruptibly(paramGuard, paramTimeUnit.toNanos(paramLong), true);
  }
  
  public void leave()
  {
    ReentrantLock localReentrantLock = this.lock;
    if (!localReentrantLock.isHeldByCurrentThread()) {
      throw new IllegalMonitorStateException();
    }
    try
    {
      signalConditionsOfSatisfiedGuards(null);
    }
    finally
    {
      localReentrantLock.unlock();
    }
  }
  
  public boolean isFair()
  {
    return this.lock.isFair();
  }
  
  public boolean isOccupied()
  {
    return this.lock.isLocked();
  }
  
  public boolean isOccupiedByCurrentThread()
  {
    return this.lock.isHeldByCurrentThread();
  }
  
  public int getOccupiedDepth()
  {
    return this.lock.getHoldCount();
  }
  
  public int getQueueLength()
  {
    return this.lock.getQueueLength();
  }
  
  public boolean hasQueuedThreads()
  {
    return this.lock.hasQueuedThreads();
  }
  
  public boolean hasQueuedThread(Thread paramThread)
  {
    return this.lock.hasQueuedThread(paramThread);
  }
  
  public boolean hasWaiters(Guard paramGuard)
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    this.lock.lock();
    try
    {
      boolean bool = paramGuard.waiterCount > 0;
      return bool;
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  public int getWaitQueueLength(Guard paramGuard)
  {
    if (paramGuard.monitor != this) {
      throw new IllegalMonitorStateException();
    }
    this.lock.lock();
    try
    {
      int i = paramGuard.waiterCount;
      return i;
    }
    finally
    {
      this.lock.unlock();
    }
  }
  
  private void signalConditionsOfSatisfiedGuards(Guard paramGuard)
  {
    ArrayList localArrayList = this.activeGuards;
    int i = localArrayList.size();
    try
    {
      for (int j = 0; j < i; j++)
      {
        Guard localGuard1 = (Guard)localArrayList.get(j);
        if (((localGuard1 != paramGuard) || (localGuard1.waiterCount != 1)) && (localGuard1.isSatisfied()))
        {
          localGuard1.condition.signal();
          return;
        }
      }
    }
    catch (Throwable localThrowable)
    {
      for (int k = 0; k < i; k++)
      {
        Guard localGuard2 = (Guard)localArrayList.get(k);
        localGuard2.condition.signalAll();
      }
      throw Throwables.propagate(localThrowable);
    }
  }
  
  private void incrementWaiters(Guard paramGuard)
  {
    int i = paramGuard.waiterCount++;
    if (i == 0) {
      this.activeGuards.add(paramGuard);
    }
  }
  
  private void decrementWaiters(Guard paramGuard)
  {
    int i = --paramGuard.waiterCount;
    if (i == 0) {
      this.activeGuards.remove(paramGuard);
    }
  }
  
  private void waitInterruptibly(Guard paramGuard, boolean paramBoolean)
    throws InterruptedException
  {
    if (!paramGuard.isSatisfied())
    {
      if (paramBoolean) {
        signalConditionsOfSatisfiedGuards(null);
      }
      incrementWaiters(paramGuard);
      try
      {
        Condition localCondition = paramGuard.condition;
        do
        {
          try
          {
            localCondition.await();
          }
          catch (InterruptedException localInterruptedException)
          {
            try {}catch (Throwable localThrowable)
            {
              Thread.currentThread().interrupt();
              throw Throwables.propagate(localThrowable);
            }
            throw localInterruptedException;
          }
        } while (!paramGuard.isSatisfied());
      }
      finally
      {
        decrementWaiters(paramGuard);
      }
    }
  }
  
  private void waitUninterruptibly(Guard paramGuard, boolean paramBoolean)
  {
    if (!paramGuard.isSatisfied())
    {
      if (paramBoolean) {
        signalConditionsOfSatisfiedGuards(null);
      }
      incrementWaiters(paramGuard);
      try
      {
        Condition localCondition = paramGuard.condition;
        do
        {
          localCondition.awaitUninterruptibly();
        } while (!paramGuard.isSatisfied());
      }
      finally
      {
        decrementWaiters(paramGuard);
      }
    }
  }
  
  private boolean waitInterruptibly(Guard paramGuard, long paramLong, boolean paramBoolean)
    throws InterruptedException
  {
    if (!paramGuard.isSatisfied())
    {
      if (paramBoolean) {
        signalConditionsOfSatisfiedGuards(null);
      }
      incrementWaiters(paramGuard);
      try
      {
        Condition localCondition = paramGuard.condition;
        do
        {
          if (paramLong <= 0L)
          {
            boolean bool = false;
            return bool;
          }
          try
          {
            paramLong = localCondition.awaitNanos(paramLong);
          }
          catch (InterruptedException localInterruptedException)
          {
            try {}catch (Throwable localThrowable)
            {
              Thread.currentThread().interrupt();
              throw Throwables.propagate(localThrowable);
            }
            throw localInterruptedException;
          }
        } while (!paramGuard.isSatisfied());
      }
      finally
      {
        decrementWaiters(paramGuard);
      }
    }
    return true;
  }
  
  private boolean waitUninterruptibly(Guard paramGuard, long paramLong, boolean paramBoolean)
  {
    if (!paramGuard.isSatisfied())
    {
      long l1 = System.nanoTime();
      if (paramBoolean) {
        signalConditionsOfSatisfiedGuards(null);
      }
      int i = 0;
      try
      {
        incrementWaiters(paramGuard);
        try
        {
          Condition localCondition = paramGuard.condition;
          long l2 = paramLong;
          do
          {
            if (l2 <= 0L)
            {
              boolean bool = false;
              decrementWaiters(paramGuard);
              return bool;
            }
            try
            {
              l2 = localCondition.awaitNanos(l2);
            }
            catch (InterruptedException localInterruptedException)
            {
              try {}catch (Throwable localThrowable)
              {
                Thread.currentThread().interrupt();
                throw Throwables.propagate(localThrowable);
              }
              i = 1;
              l2 = paramLong - (System.nanoTime() - l1);
            }
          } while (!paramGuard.isSatisfied());
        }
        finally
        {
          decrementWaiters(paramGuard);
        }
      }
      finally
      {
        if (i != 0) {
          Thread.currentThread().interrupt();
        }
      }
    }
    return true;
  }
  
  @Beta
  public static abstract class Guard
  {
    final Monitor monitor;
    final Condition condition;
    int waiterCount = 0;
    
    protected Guard(Monitor paramMonitor)
    {
      this.monitor = ((Monitor)Preconditions.checkNotNull(paramMonitor, "monitor"));
      this.condition = paramMonitor.lock.newCondition();
    }
    
    public abstract boolean isSatisfied();
    
    public final boolean equals(Object paramObject)
    {
      return this == paramObject;
    }
    
    public final int hashCode()
    {
      return super.hashCode();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\Monitor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */