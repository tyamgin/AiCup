package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public class CycleDetectingLockFactory
{
  private static final Map lockGraphNodesPerType = new MapMaker().weakKeys().makeComputingMap(new OrderedLockGraphNodesCreator());
  private static final Logger logger = Logger.getLogger(CycleDetectingLockFactory.class.getName());
  final Policy policy;
  private static final ThreadLocal acquiredLocks = new ThreadLocal()
  {
    protected ArrayList initialValue()
    {
      return Lists.newArrayListWithCapacity(3);
    }
  };
  
  public static CycleDetectingLockFactory newInstance(Policy paramPolicy)
  {
    return new CycleDetectingLockFactory(paramPolicy);
  }
  
  public ReentrantLock newReentrantLock(String paramString)
  {
    return newReentrantLock(paramString, false);
  }
  
  public ReentrantLock newReentrantLock(String paramString, boolean paramBoolean)
  {
    return this.policy == Policies.DISABLED ? new ReentrantLock(paramBoolean) : new CycleDetectingReentrantLock(new LockGraphNode(paramString), paramBoolean, null);
  }
  
  public ReentrantReadWriteLock newReentrantReadWriteLock(String paramString)
  {
    return newReentrantReadWriteLock(paramString, false);
  }
  
  public ReentrantReadWriteLock newReentrantReadWriteLock(String paramString, boolean paramBoolean)
  {
    return this.policy == Policies.DISABLED ? new ReentrantReadWriteLock(paramBoolean) : new CycleDetectingReentrantReadWriteLock(new LockGraphNode(paramString), paramBoolean, null);
  }
  
  public static WithExplicitOrdering newInstanceWithExplicitOrdering(Class paramClass, Policy paramPolicy)
  {
    Preconditions.checkNotNull(paramClass);
    Preconditions.checkNotNull(paramPolicy);
    Map localMap = (Map)lockGraphNodesPerType.get(paramClass);
    return new WithExplicitOrdering(paramPolicy, localMap);
  }
  
  private CycleDetectingLockFactory(Policy paramPolicy)
  {
    this.policy = ((Policy)Preconditions.checkNotNull(paramPolicy));
  }
  
  private void aboutToAcquire(CycleDetectingLock paramCycleDetectingLock)
  {
    if (!paramCycleDetectingLock.isAcquiredByCurrentThread())
    {
      ArrayList localArrayList = (ArrayList)acquiredLocks.get();
      LockGraphNode localLockGraphNode = paramCycleDetectingLock.getLockGraphNode();
      localLockGraphNode.checkAcquiredLocks(this.policy, localArrayList);
      localArrayList.add(localLockGraphNode);
    }
  }
  
  private void lockStateChanged(CycleDetectingLock paramCycleDetectingLock)
  {
    if (!paramCycleDetectingLock.isAcquiredByCurrentThread())
    {
      ArrayList localArrayList = (ArrayList)acquiredLocks.get();
      LockGraphNode localLockGraphNode = paramCycleDetectingLock.getLockGraphNode();
      for (int i = localArrayList.size() - 1; i >= 0; i--) {
        if (localArrayList.get(i) == localLockGraphNode)
        {
          localArrayList.remove(i);
          break;
        }
      }
    }
  }
  
  private class CycleDetectingReentrantWriteLock
    extends ReentrantReadWriteLock.WriteLock
  {
    final CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock readWriteLock;
    
    CycleDetectingReentrantWriteLock(CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock paramCycleDetectingReentrantReadWriteLock)
    {
      super();
      this.readWriteLock = paramCycleDetectingReentrantReadWriteLock;
    }
    
    public void lock()
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
      try
      {
        super.lock();
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
    
    public void lockInterruptibly()
      throws InterruptedException
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
      try
      {
        super.lockInterruptibly();
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
    
    public boolean tryLock()
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
      try
      {
        boolean bool = super.tryLock();
        return bool;
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
    
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
      try
      {
        boolean bool = super.tryLock(paramLong, paramTimeUnit);
        return bool;
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
    
    public void unlock()
    {
      try
      {
        super.unlock();
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
  }
  
  private class CycleDetectingReentrantReadLock
    extends ReentrantReadWriteLock.ReadLock
  {
    final CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock readWriteLock;
    
    CycleDetectingReentrantReadLock(CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock paramCycleDetectingReentrantReadWriteLock)
    {
      super();
      this.readWriteLock = paramCycleDetectingReentrantReadWriteLock;
    }
    
    public void lock()
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
      try
      {
        super.lock();
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
    
    public void lockInterruptibly()
      throws InterruptedException
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
      try
      {
        super.lockInterruptibly();
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
    
    public boolean tryLock()
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
      try
      {
        boolean bool = super.tryLock();
        return bool;
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
    
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this.readWriteLock);
      try
      {
        boolean bool = super.tryLock(paramLong, paramTimeUnit);
        return bool;
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
    
    public void unlock()
    {
      try
      {
        super.unlock();
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this.readWriteLock);
      }
    }
  }
  
  final class CycleDetectingReentrantReadWriteLock
    extends ReentrantReadWriteLock
    implements CycleDetectingLockFactory.CycleDetectingLock
  {
    private final CycleDetectingLockFactory.CycleDetectingReentrantReadLock readLock = new CycleDetectingLockFactory.CycleDetectingReentrantReadLock(CycleDetectingLockFactory.this, this);
    private final CycleDetectingLockFactory.CycleDetectingReentrantWriteLock writeLock = new CycleDetectingLockFactory.CycleDetectingReentrantWriteLock(CycleDetectingLockFactory.this, this);
    private final CycleDetectingLockFactory.LockGraphNode lockGraphNode;
    
    private CycleDetectingReentrantReadWriteLock(CycleDetectingLockFactory.LockGraphNode paramLockGraphNode, boolean paramBoolean)
    {
      super();
      this.lockGraphNode = ((CycleDetectingLockFactory.LockGraphNode)Preconditions.checkNotNull(paramLockGraphNode));
    }
    
    public ReentrantReadWriteLock.ReadLock readLock()
    {
      return this.readLock;
    }
    
    public ReentrantReadWriteLock.WriteLock writeLock()
    {
      return this.writeLock;
    }
    
    public CycleDetectingLockFactory.LockGraphNode getLockGraphNode()
    {
      return this.lockGraphNode;
    }
    
    public boolean isAcquiredByCurrentThread()
    {
      return (isWriteLockedByCurrentThread()) || (getReadHoldCount() > 0);
    }
  }
  
  final class CycleDetectingReentrantLock
    extends ReentrantLock
    implements CycleDetectingLockFactory.CycleDetectingLock
  {
    private final CycleDetectingLockFactory.LockGraphNode lockGraphNode;
    
    private CycleDetectingReentrantLock(CycleDetectingLockFactory.LockGraphNode paramLockGraphNode, boolean paramBoolean)
    {
      super();
      this.lockGraphNode = ((CycleDetectingLockFactory.LockGraphNode)Preconditions.checkNotNull(paramLockGraphNode));
    }
    
    public CycleDetectingLockFactory.LockGraphNode getLockGraphNode()
    {
      return this.lockGraphNode;
    }
    
    public boolean isAcquiredByCurrentThread()
    {
      return isHeldByCurrentThread();
    }
    
    public void lock()
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this);
      try
      {
        super.lock();
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this);
      }
    }
    
    public void lockInterruptibly()
      throws InterruptedException
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this);
      try
      {
        super.lockInterruptibly();
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this);
      }
    }
    
    public boolean tryLock()
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this);
      try
      {
        boolean bool = super.tryLock();
        return bool;
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this);
      }
    }
    
    public boolean tryLock(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      CycleDetectingLockFactory.this.aboutToAcquire(this);
      try
      {
        boolean bool = super.tryLock(paramLong, paramTimeUnit);
        return bool;
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this);
      }
    }
    
    public void unlock()
    {
      try
      {
        super.unlock();
      }
      finally
      {
        CycleDetectingLockFactory.this.lockStateChanged(this);
      }
    }
  }
  
  private static class LockGraphNode
  {
    final Map allowedPriorLocks = new MapMaker().weakKeys().makeMap();
    final Map disallowedPriorLocks = new MapMaker().weakKeys().makeMap();
    final String lockName;
    
    LockGraphNode(String paramString)
    {
      this.lockName = ((String)Preconditions.checkNotNull(paramString));
    }
    
    String getLockName()
    {
      return this.lockName;
    }
    
    void checkAcquiredLocks(CycleDetectingLockFactory.Policy paramPolicy, List paramList)
    {
      int i = 0;
      int j = paramList.size();
      while (i < j)
      {
        checkAcquiredLock(paramPolicy, (LockGraphNode)paramList.get(i));
        i++;
      }
    }
    
    void checkAcquiredLock(CycleDetectingLockFactory.Policy paramPolicy, LockGraphNode paramLockGraphNode)
    {
      Preconditions.checkState(this != paramLockGraphNode, "Attempted to acquire multiple locks with the same rank " + paramLockGraphNode.getLockName());
      if (this.allowedPriorLocks.containsKey(paramLockGraphNode)) {
        return;
      }
      CycleDetectingLockFactory.PotentialDeadlockException localPotentialDeadlockException1 = (CycleDetectingLockFactory.PotentialDeadlockException)this.disallowedPriorLocks.get(paramLockGraphNode);
      if (localPotentialDeadlockException1 != null)
      {
        localObject = new CycleDetectingLockFactory.PotentialDeadlockException(paramLockGraphNode, this, localPotentialDeadlockException1.getConflictingStackTrace(), null);
        paramPolicy.handlePotentialDeadlock((CycleDetectingLockFactory.PotentialDeadlockException)localObject);
        return;
      }
      Object localObject = Sets.newIdentityHashSet();
      CycleDetectingLockFactory.ExampleStackTrace localExampleStackTrace = paramLockGraphNode.findPathTo(this, (Set)localObject);
      if (localExampleStackTrace == null)
      {
        this.allowedPriorLocks.put(paramLockGraphNode, new CycleDetectingLockFactory.ExampleStackTrace(paramLockGraphNode, this));
      }
      else
      {
        CycleDetectingLockFactory.PotentialDeadlockException localPotentialDeadlockException2 = new CycleDetectingLockFactory.PotentialDeadlockException(paramLockGraphNode, this, localExampleStackTrace, null);
        this.disallowedPriorLocks.put(paramLockGraphNode, localPotentialDeadlockException2);
        paramPolicy.handlePotentialDeadlock(localPotentialDeadlockException2);
      }
    }
    
    private CycleDetectingLockFactory.ExampleStackTrace findPathTo(LockGraphNode paramLockGraphNode, Set paramSet)
    {
      if (!paramSet.add(this)) {
        return null;
      }
      CycleDetectingLockFactory.ExampleStackTrace localExampleStackTrace1 = (CycleDetectingLockFactory.ExampleStackTrace)this.allowedPriorLocks.get(paramLockGraphNode);
      if (localExampleStackTrace1 != null) {
        return localExampleStackTrace1;
      }
      Iterator localIterator = this.allowedPriorLocks.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        LockGraphNode localLockGraphNode = (LockGraphNode)localEntry.getKey();
        localExampleStackTrace1 = localLockGraphNode.findPathTo(paramLockGraphNode, paramSet);
        if (localExampleStackTrace1 != null)
        {
          CycleDetectingLockFactory.ExampleStackTrace localExampleStackTrace2 = new CycleDetectingLockFactory.ExampleStackTrace(localLockGraphNode, this);
          localExampleStackTrace2.setStackTrace(((CycleDetectingLockFactory.ExampleStackTrace)localEntry.getValue()).getStackTrace());
          localExampleStackTrace2.initCause(localExampleStackTrace1);
          return localExampleStackTrace2;
        }
      }
      return null;
    }
  }
  
  private static abstract interface CycleDetectingLock
  {
    public abstract CycleDetectingLockFactory.LockGraphNode getLockGraphNode();
    
    public abstract boolean isAcquiredByCurrentThread();
  }
  
  @Beta
  public static final class PotentialDeadlockException
    extends CycleDetectingLockFactory.ExampleStackTrace
  {
    private final CycleDetectingLockFactory.ExampleStackTrace conflictingStackTrace;
    
    private PotentialDeadlockException(CycleDetectingLockFactory.LockGraphNode paramLockGraphNode1, CycleDetectingLockFactory.LockGraphNode paramLockGraphNode2, CycleDetectingLockFactory.ExampleStackTrace paramExampleStackTrace)
    {
      super(paramLockGraphNode2);
      this.conflictingStackTrace = paramExampleStackTrace;
      initCause(paramExampleStackTrace);
    }
    
    public CycleDetectingLockFactory.ExampleStackTrace getConflictingStackTrace()
    {
      return this.conflictingStackTrace;
    }
    
    public String getMessage()
    {
      StringBuilder localStringBuilder = new StringBuilder(super.getMessage());
      for (Object localObject = this.conflictingStackTrace; localObject != null; localObject = ((Throwable)localObject).getCause()) {
        localStringBuilder.append(", ").append(((Throwable)localObject).getMessage());
      }
      return localStringBuilder.toString();
    }
  }
  
  private static class ExampleStackTrace
    extends IllegalStateException
  {
    static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
    static Set EXCLUDED_CLASS_NAMES = ImmutableSet.of(CycleDetectingLockFactory.class.getName(), ExampleStackTrace.class.getName(), CycleDetectingLockFactory.LockGraphNode.class.getName());
    
    ExampleStackTrace(CycleDetectingLockFactory.LockGraphNode paramLockGraphNode1, CycleDetectingLockFactory.LockGraphNode paramLockGraphNode2)
    {
      super();
      StackTraceElement[] arrayOfStackTraceElement = getStackTrace();
      int i = 0;
      int j = arrayOfStackTraceElement.length;
      while (i < j)
      {
        if (CycleDetectingLockFactory.WithExplicitOrdering.class.getName().equals(arrayOfStackTraceElement[i].getClassName()))
        {
          setStackTrace(EMPTY_STACK_TRACE);
          break;
        }
        if (!EXCLUDED_CLASS_NAMES.contains(arrayOfStackTraceElement[i].getClassName()))
        {
          setStackTrace((StackTraceElement[])Arrays.copyOfRange(arrayOfStackTraceElement, i, j));
          break;
        }
        i++;
      }
    }
  }
  
  @VisibleForTesting
  static class OrderedLockGraphNodesCreator
    implements Function
  {
    public Map apply(Class paramClass)
    {
      return createNodesFor(paramClass);
    }
    
    Map createNodesFor(Class paramClass)
    {
      EnumMap localEnumMap = Maps.newEnumMap(paramClass);
      Enum[] arrayOfEnum1 = (Enum[])paramClass.getEnumConstants();
      int i = arrayOfEnum1.length;
      ArrayList localArrayList = Lists.newArrayListWithCapacity(i);
      for (Enum localEnum : arrayOfEnum1)
      {
        CycleDetectingLockFactory.LockGraphNode localLockGraphNode = new CycleDetectingLockFactory.LockGraphNode(getLockName(localEnum));
        localArrayList.add(localLockGraphNode);
        localEnumMap.put(localEnum, localLockGraphNode);
      }
      for (int j = 1; j < i; j++) {
        ((CycleDetectingLockFactory.LockGraphNode)localArrayList.get(j)).checkAcquiredLocks(CycleDetectingLockFactory.Policies.THROW, localArrayList.subList(0, j));
      }
      for (j = 0; j < i - 1; j++) {
        ((CycleDetectingLockFactory.LockGraphNode)localArrayList.get(j)).checkAcquiredLocks(CycleDetectingLockFactory.Policies.DISABLED, localArrayList.subList(j + 1, i));
      }
      return Collections.unmodifiableMap(localEnumMap);
    }
    
    private String getLockName(Enum paramEnum)
    {
      return paramEnum.getDeclaringClass().getSimpleName() + "." + paramEnum.name();
    }
  }
  
  @Beta
  public static final class WithExplicitOrdering
    extends CycleDetectingLockFactory
  {
    private final Map lockGraphNodes;
    
    @VisibleForTesting
    WithExplicitOrdering(CycleDetectingLockFactory.Policy paramPolicy, Map paramMap)
    {
      super(null);
      this.lockGraphNodes = paramMap;
    }
    
    public ReentrantLock newReentrantLock(Enum paramEnum)
    {
      return newReentrantLock(paramEnum, false);
    }
    
    public ReentrantLock newReentrantLock(Enum paramEnum, boolean paramBoolean)
    {
      return this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantLock(paramBoolean) : new CycleDetectingLockFactory.CycleDetectingReentrantLock(this, (CycleDetectingLockFactory.LockGraphNode)this.lockGraphNodes.get(paramEnum), paramBoolean, null);
    }
    
    public ReentrantReadWriteLock newReentrantReadWriteLock(Enum paramEnum)
    {
      return newReentrantReadWriteLock(paramEnum, false);
    }
    
    public ReentrantReadWriteLock newReentrantReadWriteLock(Enum paramEnum, boolean paramBoolean)
    {
      return this.policy == CycleDetectingLockFactory.Policies.DISABLED ? new ReentrantReadWriteLock(paramBoolean) : new CycleDetectingLockFactory.CycleDetectingReentrantReadWriteLock(this, (CycleDetectingLockFactory.LockGraphNode)this.lockGraphNodes.get(paramEnum), paramBoolean, null);
    }
  }
  
  @Beta
  public static abstract enum Policies
    implements CycleDetectingLockFactory.Policy
  {
    THROW,  WARN,  DISABLED;
  }
  
  @Beta
  public static abstract interface Policy
  {
    public abstract void handlePotentialDeadlock(CycleDetectingLockFactory.PotentialDeadlockException paramPotentialDeadlockException);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\CycleDetectingLockFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */