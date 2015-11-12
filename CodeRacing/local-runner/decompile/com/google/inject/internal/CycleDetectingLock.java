package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

abstract interface CycleDetectingLock
{
  public abstract ListMultimap lockOrDetectPotentialLocksCycle();
  
  public abstract void unlock();
  
  public static class CycleDetectingLockFactory
  {
    private Map lockThreadIsWaitingOn = Maps.newHashMap();
    private final Multimap locksOwnedByThread = LinkedHashMultimap.create();
    
    CycleDetectingLock create(Object paramObject)
    {
      return new ReentrantCycleDetectingLock(paramObject, new ReentrantLock());
    }
    
    class ReentrantCycleDetectingLock
      implements CycleDetectingLock
    {
      private final Lock lockImplementation;
      private final Object userLockId;
      private Long lockOwnerThreadId = null;
      private int lockReentranceCount = 0;
      
      ReentrantCycleDetectingLock(Object paramObject, Lock paramLock)
      {
        this.userLockId = Preconditions.checkNotNull(paramObject, "userLockId");
        this.lockImplementation = ((Lock)Preconditions.checkNotNull(paramLock, "lockImplementation"));
      }
      
      public ListMultimap lockOrDetectPotentialLocksCycle()
      {
        long l = Thread.currentThread().getId();
        synchronized (CycleDetectingLock.CycleDetectingLockFactory.this)
        {
          checkState();
          ListMultimap localListMultimap = detectPotentialLocksCycle();
          if (!localListMultimap.isEmpty()) {
            return localListMultimap;
          }
          CycleDetectingLock.CycleDetectingLockFactory.this.lockThreadIsWaitingOn.put(Long.valueOf(l), this);
        }
        this.lockImplementation.lock();
        synchronized (CycleDetectingLock.CycleDetectingLockFactory.this)
        {
          CycleDetectingLock.CycleDetectingLockFactory.this.lockThreadIsWaitingOn.remove(Long.valueOf(l));
          checkState();
          this.lockOwnerThreadId = Long.valueOf(l);
          this.lockReentranceCount += 1;
          CycleDetectingLock.CycleDetectingLockFactory.this.locksOwnedByThread.put(Long.valueOf(l), this);
        }
        return ImmutableListMultimap.of();
      }
      
      public void unlock()
      {
        long l = Thread.currentThread().getId();
        synchronized (CycleDetectingLock.CycleDetectingLockFactory.this)
        {
          checkState();
          Preconditions.checkState(this.lockOwnerThreadId != null, "Thread is trying to unlock a lock that is not locked");
          Preconditions.checkState(this.lockOwnerThreadId.longValue() == l, "Thread is trying to unlock a lock owned by another thread");
          this.lockImplementation.unlock();
          this.lockReentranceCount -= 1;
          if (this.lockReentranceCount == 0)
          {
            this.lockOwnerThreadId = null;
            Preconditions.checkState(CycleDetectingLock.CycleDetectingLockFactory.this.locksOwnedByThread.remove(Long.valueOf(l), this), "Internal error: Can not find this lock in locks owned by a current thread");
            if (CycleDetectingLock.CycleDetectingLockFactory.this.locksOwnedByThread.get(Long.valueOf(l)).isEmpty()) {
              CycleDetectingLock.CycleDetectingLockFactory.this.locksOwnedByThread.removeAll(Long.valueOf(l));
            }
          }
        }
      }
      
      void checkState()
        throws IllegalStateException
      {
        long l = Thread.currentThread().getId();
        Preconditions.checkState(!CycleDetectingLock.CycleDetectingLockFactory.this.lockThreadIsWaitingOn.containsKey(Long.valueOf(l)), "Internal error: Thread should not be in a waiting thread on a lock now");
        if (this.lockOwnerThreadId != null)
        {
          Preconditions.checkState(this.lockReentranceCount >= 0, "Internal error: Lock ownership and reentrance count internal states do not match");
          Preconditions.checkState(CycleDetectingLock.CycleDetectingLockFactory.this.locksOwnedByThread.get(this.lockOwnerThreadId).contains(this), "Internal error: Set of locks owned by a current thread and lock ownership status do not match");
        }
        else
        {
          Preconditions.checkState(this.lockReentranceCount == 0, "Internal error: Reentrance count of a non locked lock is expect to be zero");
          Preconditions.checkState(!CycleDetectingLock.CycleDetectingLockFactory.this.locksOwnedByThread.values().contains(this), "Internal error: Non locked lock should not be owned by any thread");
        }
      }
      
      private ListMultimap detectPotentialLocksCycle()
      {
        long l = Thread.currentThread().getId();
        if ((this.lockOwnerThreadId == null) || (this.lockOwnerThreadId.longValue() == l)) {
          return ImmutableListMultimap.of();
        }
        ListMultimap localListMultimap = Multimaps.newListMultimap(new LinkedHashMap(), new Supplier()
        {
          public List get()
          {
            return Lists.newArrayList();
          }
        });
        Long localLong;
        for (ReentrantCycleDetectingLock localReentrantCycleDetectingLock = this; (localReentrantCycleDetectingLock != null) && (localReentrantCycleDetectingLock.lockOwnerThreadId != null); localReentrantCycleDetectingLock = (ReentrantCycleDetectingLock)CycleDetectingLock.CycleDetectingLockFactory.this.lockThreadIsWaitingOn.get(localLong))
        {
          localLong = localReentrantCycleDetectingLock.lockOwnerThreadId;
          localListMultimap.putAll(localLong, getAllLockIdsAfter(localLong.longValue(), localReentrantCycleDetectingLock));
          if (localLong.longValue() == l) {
            return localListMultimap;
          }
        }
        return ImmutableListMultimap.of();
      }
      
      private List getAllLockIdsAfter(long paramLong, ReentrantCycleDetectingLock paramReentrantCycleDetectingLock)
      {
        ArrayList localArrayList = Lists.newArrayList();
        boolean bool = false;
        Collection localCollection = CycleDetectingLock.CycleDetectingLockFactory.this.locksOwnedByThread.get(Long.valueOf(paramLong));
        Preconditions.checkNotNull(localCollection, "Internal error: No locks were found taken by a thread");
        Iterator localIterator = localCollection.iterator();
        while (localIterator.hasNext())
        {
          ReentrantCycleDetectingLock localReentrantCycleDetectingLock = (ReentrantCycleDetectingLock)localIterator.next();
          if (localReentrantCycleDetectingLock == paramReentrantCycleDetectingLock) {
            bool = true;
          }
          if (bool) {
            localArrayList.add(localReentrantCycleDetectingLock.userLockId);
          }
        }
        Preconditions.checkState(bool, "Internal error: We can not find locks that created a cycle that we detected");
        return localArrayList;
      }
      
      public String toString()
      {
        Long localLong = this.lockOwnerThreadId;
        if (localLong != null) {
          return String.format("CycleDetectingLock[%s][locked by %s]", new Object[] { this.userLockId, localLong });
        }
        return String.format("CycleDetectingLock[%s][unlocked]", new Object[] { this.userLockId });
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\CycleDetectingLock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */