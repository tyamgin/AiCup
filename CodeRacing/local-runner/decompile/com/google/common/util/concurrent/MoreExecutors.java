package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MoreExecutors
{
  @Beta
  public static ExecutorService getExitingExecutorService(ThreadPoolExecutor paramThreadPoolExecutor, long paramLong, TimeUnit paramTimeUnit)
  {
    return new Application().getExitingExecutorService(paramThreadPoolExecutor, paramLong, paramTimeUnit);
  }
  
  @Beta
  public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor paramScheduledThreadPoolExecutor, long paramLong, TimeUnit paramTimeUnit)
  {
    return new Application().getExitingScheduledExecutorService(paramScheduledThreadPoolExecutor, paramLong, paramTimeUnit);
  }
  
  @Beta
  public static void addDelayedShutdownHook(ExecutorService paramExecutorService, long paramLong, TimeUnit paramTimeUnit)
  {
    new Application().addDelayedShutdownHook(paramExecutorService, paramLong, paramTimeUnit);
  }
  
  @Beta
  public static ExecutorService getExitingExecutorService(ThreadPoolExecutor paramThreadPoolExecutor)
  {
    return new Application().getExitingExecutorService(paramThreadPoolExecutor);
  }
  
  @Beta
  public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor paramScheduledThreadPoolExecutor)
  {
    return new Application().getExitingScheduledExecutorService(paramScheduledThreadPoolExecutor);
  }
  
  private static void useDaemonThreadFactory(ThreadPoolExecutor paramThreadPoolExecutor)
  {
    paramThreadPoolExecutor.setThreadFactory(new ThreadFactoryBuilder().setDaemon(true).setThreadFactory(paramThreadPoolExecutor.getThreadFactory()).build());
  }
  
  public static ListeningExecutorService sameThreadExecutor()
  {
    return new SameThreadExecutorService(null);
  }
  
  public static ListeningExecutorService listeningDecorator(ExecutorService paramExecutorService)
  {
    return (paramExecutorService instanceof ScheduledExecutorService) ? new ScheduledListeningDecorator((ScheduledExecutorService)paramExecutorService) : (paramExecutorService instanceof ListeningExecutorService) ? (ListeningExecutorService)paramExecutorService : new ListeningDecorator(paramExecutorService);
  }
  
  public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService paramScheduledExecutorService)
  {
    return (paramScheduledExecutorService instanceof ListeningScheduledExecutorService) ? (ListeningScheduledExecutorService)paramScheduledExecutorService : new ScheduledListeningDecorator(paramScheduledExecutorService);
  }
  
  static Object invokeAnyImpl(ListeningExecutorService paramListeningExecutorService, Collection paramCollection, boolean paramBoolean, long paramLong)
    throws InterruptedException, ExecutionException, TimeoutException
  {
    Preconditions.checkNotNull(paramListeningExecutorService);
    int i = paramCollection.size();
    Preconditions.checkArgument(i > 0);
    ArrayList localArrayList = Lists.newArrayListWithCapacity(i);
    LinkedBlockingQueue localLinkedBlockingQueue = Queues.newLinkedBlockingQueue();
    try
    {
      Object localObject1 = null;
      long l1 = paramBoolean ? System.nanoTime() : 0L;
      Iterator localIterator1 = paramCollection.iterator();
      localArrayList.add(submitAndAddQueueListener(paramListeningExecutorService, (Callable)localIterator1.next(), localLinkedBlockingQueue));
      i--;
      int j = 1;
      for (;;)
      {
        Future localFuture1 = (Future)localLinkedBlockingQueue.poll();
        if (localFuture1 == null) {
          if (i > 0)
          {
            i--;
            localArrayList.add(submitAndAddQueueListener(paramListeningExecutorService, (Callable)localIterator1.next(), localLinkedBlockingQueue));
            j++;
          }
          else
          {
            if (j == 0) {
              break;
            }
            if (paramBoolean)
            {
              localFuture1 = (Future)localLinkedBlockingQueue.poll(paramLong, TimeUnit.NANOSECONDS);
              if (localFuture1 == null) {
                throw new TimeoutException();
              }
              long l2 = System.nanoTime();
              paramLong -= l2 - l1;
              l1 = l2;
            }
            else
            {
              localFuture1 = (Future)localLinkedBlockingQueue.take();
            }
          }
        }
        if (localFuture1 != null)
        {
          j--;
          try
          {
            Object localObject2 = localFuture1.get();
            Iterator localIterator2;
            Future localFuture2;
            return localObject2;
          }
          catch (ExecutionException localExecutionException)
          {
            localObject1 = localExecutionException;
          }
          catch (RuntimeException localRuntimeException)
          {
            localObject1 = new ExecutionException(localRuntimeException);
          }
        }
      }
      if (localObject1 == null) {
        localObject1 = new ExecutionException(null);
      }
      throw ((Throwable)localObject1);
    }
    finally
    {
      Iterator localIterator3 = localArrayList.iterator();
      while (localIterator3.hasNext())
      {
        Future localFuture3 = (Future)localIterator3.next();
        localFuture3.cancel(true);
      }
    }
  }
  
  private static ListenableFuture submitAndAddQueueListener(ListeningExecutorService paramListeningExecutorService, Callable paramCallable, BlockingQueue paramBlockingQueue)
  {
    final ListenableFuture localListenableFuture = paramListeningExecutorService.submit(paramCallable);
    localListenableFuture.addListener(new Runnable()
    {
      public void run()
      {
        this.val$queue.add(localListenableFuture);
      }
    }, sameThreadExecutor());
    return localListenableFuture;
  }
  
  @Beta
  public static ThreadFactory platformThreadFactory()
  {
    if (!isAppEngine()) {
      return Executors.defaultThreadFactory();
    }
    try
    {
      return (ThreadFactory)Class.forName("com.google.appengine.api.ThreadManager").getMethod("currentRequestThreadFactory", new Class[0]).invoke(null, new Object[0]);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", localIllegalAccessException);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", localClassNotFoundException);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new RuntimeException("Couldn't invoke ThreadManager.currentRequestThreadFactory", localNoSuchMethodException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw Throwables.propagate(localInvocationTargetException.getCause());
    }
  }
  
  private static boolean isAppEngine()
  {
    if (System.getProperty("com.google.appengine.runtime.environment") == null) {
      return false;
    }
    try
    {
      return Class.forName("com.google.apphosting.api.ApiProxy").getMethod("getCurrentEnvironment", new Class[0]).invoke(null, new Object[0]) != null;
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      return false;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      return false;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      return false;
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return false;
  }
  
  static Thread newThread(String paramString, Runnable paramRunnable)
  {
    Preconditions.checkNotNull(paramString);
    Preconditions.checkNotNull(paramRunnable);
    Thread localThread = platformThreadFactory().newThread(paramRunnable);
    try
    {
      localThread.setName(paramString);
    }
    catch (SecurityException localSecurityException) {}
    return localThread;
  }
  
  private static class ScheduledListeningDecorator
    extends MoreExecutors.ListeningDecorator
    implements ListeningScheduledExecutorService
  {
    final ScheduledExecutorService delegate;
    
    ScheduledListeningDecorator(ScheduledExecutorService paramScheduledExecutorService)
    {
      super();
      this.delegate = ((ScheduledExecutorService)Preconditions.checkNotNull(paramScheduledExecutorService));
    }
    
    public ScheduledFuture schedule(Runnable paramRunnable, long paramLong, TimeUnit paramTimeUnit)
    {
      return this.delegate.schedule(paramRunnable, paramLong, paramTimeUnit);
    }
    
    public ScheduledFuture schedule(Callable paramCallable, long paramLong, TimeUnit paramTimeUnit)
    {
      return this.delegate.schedule(paramCallable, paramLong, paramTimeUnit);
    }
    
    public ScheduledFuture scheduleAtFixedRate(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit)
    {
      return this.delegate.scheduleAtFixedRate(paramRunnable, paramLong1, paramLong2, paramTimeUnit);
    }
    
    public ScheduledFuture scheduleWithFixedDelay(Runnable paramRunnable, long paramLong1, long paramLong2, TimeUnit paramTimeUnit)
    {
      return this.delegate.scheduleWithFixedDelay(paramRunnable, paramLong1, paramLong2, paramTimeUnit);
    }
  }
  
  private static class ListeningDecorator
    extends AbstractListeningExecutorService
  {
    final ExecutorService delegate;
    
    ListeningDecorator(ExecutorService paramExecutorService)
    {
      this.delegate = ((ExecutorService)Preconditions.checkNotNull(paramExecutorService));
    }
    
    public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      return this.delegate.awaitTermination(paramLong, paramTimeUnit);
    }
    
    public boolean isShutdown()
    {
      return this.delegate.isShutdown();
    }
    
    public boolean isTerminated()
    {
      return this.delegate.isTerminated();
    }
    
    public void shutdown()
    {
      this.delegate.shutdown();
    }
    
    public List shutdownNow()
    {
      return this.delegate.shutdownNow();
    }
    
    public void execute(Runnable paramRunnable)
    {
      this.delegate.execute(paramRunnable);
    }
  }
  
  private static class SameThreadExecutorService
    extends AbstractListeningExecutorService
  {
    private final Lock lock = new ReentrantLock();
    private final Condition termination = this.lock.newCondition();
    private int runningTasks = 0;
    private boolean shutdown = false;
    
    public void execute(Runnable paramRunnable)
    {
      startTask();
      try
      {
        paramRunnable.run();
      }
      finally
      {
        endTask();
      }
    }
    
    public boolean isShutdown()
    {
      this.lock.lock();
      try
      {
        boolean bool = this.shutdown;
        return bool;
      }
      finally
      {
        this.lock.unlock();
      }
    }
    
    public void shutdown()
    {
      this.lock.lock();
      try
      {
        this.shutdown = true;
      }
      finally
      {
        this.lock.unlock();
      }
    }
    
    public List shutdownNow()
    {
      shutdown();
      return Collections.emptyList();
    }
    
    public boolean isTerminated()
    {
      this.lock.lock();
      try
      {
        boolean bool = (this.shutdown) && (this.runningTasks == 0);
        return bool;
      }
      finally
      {
        this.lock.unlock();
      }
    }
    
    /* Error */
    public boolean awaitTermination(long paramLong, TimeUnit paramTimeUnit)
      throws InterruptedException
    {
      // Byte code:
      //   0: aload_3
      //   1: lload_1
      //   2: invokevirtual 28	java/util/concurrent/TimeUnit:toNanos	(J)J
      //   5: lstore 4
      //   7: aload_0
      //   8: getfield 15	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:lock	Ljava/util/concurrent/locks/Lock;
      //   11: invokeinterface 33 1 0
      //   16: aload_0
      //   17: invokevirtual 23	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:isTerminated	()Z
      //   20: ifeq +18 -> 38
      //   23: iconst_1
      //   24: istore 6
      //   26: aload_0
      //   27: getfield 15	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:lock	Ljava/util/concurrent/locks/Lock;
      //   30: invokeinterface 35 1 0
      //   35: iload 6
      //   37: ireturn
      //   38: lload 4
      //   40: lconst_0
      //   41: lcmp
      //   42: ifgt +18 -> 60
      //   45: iconst_0
      //   46: istore 6
      //   48: aload_0
      //   49: getfield 15	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:lock	Ljava/util/concurrent/locks/Lock;
      //   52: invokeinterface 35 1 0
      //   57: iload 6
      //   59: ireturn
      //   60: aload_0
      //   61: getfield 18	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:termination	Ljava/util/concurrent/locks/Condition;
      //   64: lload 4
      //   66: invokeinterface 31 3 0
      //   71: lstore 4
      //   73: goto -57 -> 16
      //   76: astore 7
      //   78: aload_0
      //   79: getfield 15	com/google/common/util/concurrent/MoreExecutors$SameThreadExecutorService:lock	Ljava/util/concurrent/locks/Lock;
      //   82: invokeinterface 35 1 0
      //   87: aload 7
      //   89: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	90	0	this	SameThreadExecutorService
      //   0	90	1	paramLong	long
      //   0	90	3	paramTimeUnit	TimeUnit
      //   5	67	4	l	long
      //   24	34	6	bool	boolean
      //   76	12	7	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   16	26	76	finally
      //   38	48	76	finally
      //   60	78	76	finally
    }
    
    private void startTask()
    {
      this.lock.lock();
      try
      {
        if (isShutdown()) {
          throw new RejectedExecutionException("Executor already shutdown");
        }
        this.runningTasks += 1;
      }
      finally
      {
        this.lock.unlock();
      }
    }
    
    private void endTask()
    {
      this.lock.lock();
      try
      {
        this.runningTasks -= 1;
        if (isTerminated()) {
          this.termination.signalAll();
        }
      }
      finally
      {
        this.lock.unlock();
      }
    }
  }
  
  @VisibleForTesting
  static class Application
  {
    final ExecutorService getExitingExecutorService(ThreadPoolExecutor paramThreadPoolExecutor, long paramLong, TimeUnit paramTimeUnit)
    {
      MoreExecutors.useDaemonThreadFactory(paramThreadPoolExecutor);
      ExecutorService localExecutorService = Executors.unconfigurableExecutorService(paramThreadPoolExecutor);
      addDelayedShutdownHook(localExecutorService, paramLong, paramTimeUnit);
      return localExecutorService;
    }
    
    final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor paramScheduledThreadPoolExecutor, long paramLong, TimeUnit paramTimeUnit)
    {
      MoreExecutors.useDaemonThreadFactory(paramScheduledThreadPoolExecutor);
      ScheduledExecutorService localScheduledExecutorService = Executors.unconfigurableScheduledExecutorService(paramScheduledThreadPoolExecutor);
      addDelayedShutdownHook(localScheduledExecutorService, paramLong, paramTimeUnit);
      return localScheduledExecutorService;
    }
    
    final void addDelayedShutdownHook(final ExecutorService paramExecutorService, final long paramLong, TimeUnit paramTimeUnit)
    {
      Preconditions.checkNotNull(paramExecutorService);
      Preconditions.checkNotNull(paramTimeUnit);
      addShutdownHook(MoreExecutors.newThread("DelayedShutdownHook-for-" + paramExecutorService, new Runnable()
      {
        public void run()
        {
          try
          {
            paramExecutorService.shutdown();
            paramExecutorService.awaitTermination(paramLong, this.val$timeUnit);
          }
          catch (InterruptedException localInterruptedException) {}
        }
      }));
    }
    
    final ExecutorService getExitingExecutorService(ThreadPoolExecutor paramThreadPoolExecutor)
    {
      return getExitingExecutorService(paramThreadPoolExecutor, 120L, TimeUnit.SECONDS);
    }
    
    final ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor paramScheduledThreadPoolExecutor)
    {
      return getExitingScheduledExecutorService(paramScheduledThreadPoolExecutor, 120L, TimeUnit.SECONDS);
    }
    
    @VisibleForTesting
    void addShutdownHook(Thread paramThread)
    {
      Runtime.getRuntime().addShutdownHook(paramThread);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\MoreExecutors.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */