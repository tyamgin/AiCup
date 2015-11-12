package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public final class Futures
{
  private static final AsyncFunction DEREFERENCER = new AsyncFunction()
  {
    public ListenableFuture apply(ListenableFuture paramAnonymousListenableFuture)
    {
      return paramAnonymousListenableFuture;
    }
  };
  private static final Ordering WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf(new Function()
  {
    public Boolean apply(Constructor paramAnonymousConstructor)
    {
      return Boolean.valueOf(Arrays.asList(paramAnonymousConstructor.getParameterTypes()).contains(String.class));
    }
  }).reverse();
  
  public static CheckedFuture makeChecked(ListenableFuture paramListenableFuture, Function paramFunction)
  {
    return new MappingCheckedFuture((ListenableFuture)Preconditions.checkNotNull(paramListenableFuture), paramFunction);
  }
  
  public static ListenableFuture immediateFuture(Object paramObject)
  {
    return new ImmediateSuccessfulFuture(paramObject);
  }
  
  public static CheckedFuture immediateCheckedFuture(Object paramObject)
  {
    return new ImmediateSuccessfulCheckedFuture(paramObject);
  }
  
  public static ListenableFuture immediateFailedFuture(Throwable paramThrowable)
  {
    Preconditions.checkNotNull(paramThrowable);
    return new ImmediateFailedFuture(paramThrowable);
  }
  
  public static ListenableFuture immediateCancelledFuture()
  {
    return new ImmediateCancelledFuture();
  }
  
  public static CheckedFuture immediateFailedCheckedFuture(Exception paramException)
  {
    Preconditions.checkNotNull(paramException);
    return new ImmediateFailedCheckedFuture(paramException);
  }
  
  public static ListenableFuture withFallback(ListenableFuture paramListenableFuture, FutureFallback paramFutureFallback)
  {
    return withFallback(paramListenableFuture, paramFutureFallback, MoreExecutors.sameThreadExecutor());
  }
  
  public static ListenableFuture withFallback(ListenableFuture paramListenableFuture, FutureFallback paramFutureFallback, Executor paramExecutor)
  {
    Preconditions.checkNotNull(paramFutureFallback);
    return new FallbackFuture(paramListenableFuture, paramFutureFallback, paramExecutor);
  }
  
  public static ListenableFuture transform(ListenableFuture paramListenableFuture, AsyncFunction paramAsyncFunction)
  {
    return transform(paramListenableFuture, paramAsyncFunction, MoreExecutors.sameThreadExecutor());
  }
  
  public static ListenableFuture transform(ListenableFuture paramListenableFuture, AsyncFunction paramAsyncFunction, Executor paramExecutor)
  {
    ChainingListenableFuture localChainingListenableFuture = new ChainingListenableFuture(paramAsyncFunction, paramListenableFuture, null);
    paramListenableFuture.addListener(localChainingListenableFuture, paramExecutor);
    return localChainingListenableFuture;
  }
  
  public static ListenableFuture transform(ListenableFuture paramListenableFuture, Function paramFunction)
  {
    return transform(paramListenableFuture, paramFunction, MoreExecutors.sameThreadExecutor());
  }
  
  public static ListenableFuture transform(ListenableFuture paramListenableFuture, Function paramFunction, Executor paramExecutor)
  {
    Preconditions.checkNotNull(paramFunction);
    AsyncFunction local1 = new AsyncFunction()
    {
      public ListenableFuture apply(Object paramAnonymousObject)
      {
        Object localObject = this.val$function.apply(paramAnonymousObject);
        return Futures.immediateFuture(localObject);
      }
    };
    return transform(paramListenableFuture, local1, paramExecutor);
  }
  
  @Beta
  public static Future lazyTransform(Future paramFuture, final Function paramFunction)
  {
    Preconditions.checkNotNull(paramFuture);
    Preconditions.checkNotNull(paramFunction);
    new Future()
    {
      public boolean cancel(boolean paramAnonymousBoolean)
      {
        return this.val$input.cancel(paramAnonymousBoolean);
      }
      
      public boolean isCancelled()
      {
        return this.val$input.isCancelled();
      }
      
      public boolean isDone()
      {
        return this.val$input.isDone();
      }
      
      public Object get()
        throws InterruptedException, ExecutionException
      {
        return applyTransformation(this.val$input.get());
      }
      
      public Object get(long paramAnonymousLong, TimeUnit paramAnonymousTimeUnit)
        throws InterruptedException, ExecutionException, TimeoutException
      {
        return applyTransformation(this.val$input.get(paramAnonymousLong, paramAnonymousTimeUnit));
      }
      
      private Object applyTransformation(Object paramAnonymousObject)
        throws ExecutionException
      {
        try
        {
          return paramFunction.apply(paramAnonymousObject);
        }
        catch (Throwable localThrowable)
        {
          throw new ExecutionException(localThrowable);
        }
      }
    };
  }
  
  @Beta
  public static ListenableFuture dereference(ListenableFuture paramListenableFuture)
  {
    return transform(paramListenableFuture, DEREFERENCER);
  }
  
  @Beta
  public static ListenableFuture allAsList(ListenableFuture... paramVarArgs)
  {
    return listFuture(ImmutableList.copyOf(paramVarArgs), true, MoreExecutors.sameThreadExecutor());
  }
  
  @Beta
  public static ListenableFuture allAsList(Iterable paramIterable)
  {
    return listFuture(ImmutableList.copyOf(paramIterable), true, MoreExecutors.sameThreadExecutor());
  }
  
  @Beta
  public static ListenableFuture successfulAsList(ListenableFuture... paramVarArgs)
  {
    return listFuture(ImmutableList.copyOf(paramVarArgs), false, MoreExecutors.sameThreadExecutor());
  }
  
  @Beta
  public static ListenableFuture successfulAsList(Iterable paramIterable)
  {
    return listFuture(ImmutableList.copyOf(paramIterable), false, MoreExecutors.sameThreadExecutor());
  }
  
  public static void addCallback(ListenableFuture paramListenableFuture, FutureCallback paramFutureCallback)
  {
    addCallback(paramListenableFuture, paramFutureCallback, MoreExecutors.sameThreadExecutor());
  }
  
  public static void addCallback(ListenableFuture paramListenableFuture, final FutureCallback paramFutureCallback, Executor paramExecutor)
  {
    Preconditions.checkNotNull(paramFutureCallback);
    Runnable local4 = new Runnable()
    {
      public void run()
      {
        Object localObject;
        try
        {
          localObject = Uninterruptibles.getUninterruptibly(this.val$future);
        }
        catch (ExecutionException localExecutionException)
        {
          paramFutureCallback.onFailure(localExecutionException.getCause());
          return;
        }
        catch (RuntimeException localRuntimeException)
        {
          paramFutureCallback.onFailure(localRuntimeException);
          return;
        }
        catch (Error localError)
        {
          paramFutureCallback.onFailure(localError);
          return;
        }
        paramFutureCallback.onSuccess(localObject);
      }
    };
    paramListenableFuture.addListener(local4, paramExecutor);
  }
  
  @Beta
  public static Object get(Future paramFuture, Class paramClass)
    throws Exception
  {
    Preconditions.checkNotNull(paramFuture);
    Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(paramClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { paramClass });
    try
    {
      return paramFuture.get();
    }
    catch (InterruptedException localInterruptedException)
    {
      Thread.currentThread().interrupt();
      throw newWithCause(paramClass, localInterruptedException);
    }
    catch (ExecutionException localExecutionException)
    {
      wrapAndThrowExceptionOrError(localExecutionException.getCause(), paramClass);
      throw new AssertionError();
    }
  }
  
  @Beta
  public static Object get(Future paramFuture, long paramLong, TimeUnit paramTimeUnit, Class paramClass)
    throws Exception
  {
    Preconditions.checkNotNull(paramFuture);
    Preconditions.checkNotNull(paramTimeUnit);
    Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(paramClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[] { paramClass });
    try
    {
      return paramFuture.get(paramLong, paramTimeUnit);
    }
    catch (InterruptedException localInterruptedException)
    {
      Thread.currentThread().interrupt();
      throw newWithCause(paramClass, localInterruptedException);
    }
    catch (TimeoutException localTimeoutException)
    {
      throw newWithCause(paramClass, localTimeoutException);
    }
    catch (ExecutionException localExecutionException)
    {
      wrapAndThrowExceptionOrError(localExecutionException.getCause(), paramClass);
      throw new AssertionError();
    }
  }
  
  private static void wrapAndThrowExceptionOrError(Throwable paramThrowable, Class paramClass)
    throws Exception
  {
    if ((paramThrowable instanceof Error)) {
      throw new ExecutionError((Error)paramThrowable);
    }
    if ((paramThrowable instanceof RuntimeException)) {
      throw new UncheckedExecutionException(paramThrowable);
    }
    throw newWithCause(paramClass, paramThrowable);
  }
  
  @Beta
  public static Object getUnchecked(Future paramFuture)
  {
    Preconditions.checkNotNull(paramFuture);
    try
    {
      return Uninterruptibles.getUninterruptibly(paramFuture);
    }
    catch (ExecutionException localExecutionException)
    {
      wrapAndThrowUnchecked(localExecutionException.getCause());
      throw new AssertionError();
    }
  }
  
  private static void wrapAndThrowUnchecked(Throwable paramThrowable)
  {
    if ((paramThrowable instanceof Error)) {
      throw new ExecutionError((Error)paramThrowable);
    }
    throw new UncheckedExecutionException(paramThrowable);
  }
  
  private static Exception newWithCause(Class paramClass, Throwable paramThrowable)
  {
    List localList = Arrays.asList(paramClass.getConstructors());
    Iterator localIterator = preferringStrings(localList).iterator();
    while (localIterator.hasNext())
    {
      Constructor localConstructor = (Constructor)localIterator.next();
      Exception localException = (Exception)newFromConstructor(localConstructor, paramThrowable);
      if (localException != null)
      {
        if (localException.getCause() == null) {
          localException.initCause(paramThrowable);
        }
        return localException;
      }
    }
    throw new IllegalArgumentException("No appropriate constructor for exception of type " + paramClass + " in response to chained exception", paramThrowable);
  }
  
  private static List preferringStrings(List paramList)
  {
    return WITH_STRING_PARAM_FIRST.sortedCopy(paramList);
  }
  
  private static Object newFromConstructor(Constructor paramConstructor, Throwable paramThrowable)
  {
    Class[] arrayOfClass = paramConstructor.getParameterTypes();
    Object[] arrayOfObject = new Object[arrayOfClass.length];
    for (int i = 0; i < arrayOfClass.length; i++)
    {
      Class localClass = arrayOfClass[i];
      if (localClass.equals(String.class)) {
        arrayOfObject[i] = paramThrowable.toString();
      } else if (localClass.equals(Throwable.class)) {
        arrayOfObject[i] = paramThrowable;
      } else {
        return null;
      }
    }
    try
    {
      return paramConstructor.newInstance(arrayOfObject);
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      return null;
    }
    catch (InstantiationException localInstantiationException)
    {
      return null;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      return null;
    }
    catch (InvocationTargetException localInvocationTargetException) {}
    return null;
  }
  
  private static ListenableFuture listFuture(ImmutableList paramImmutableList, boolean paramBoolean, Executor paramExecutor)
  {
    new CombinedFuture(paramImmutableList, paramBoolean, paramExecutor, new FutureCombiner()
    {
      public List combine(List paramAnonymousList)
      {
        ArrayList localArrayList = Lists.newArrayList();
        Iterator localIterator = paramAnonymousList.iterator();
        while (localIterator.hasNext())
        {
          Optional localOptional = (Optional)localIterator.next();
          localArrayList.add(localOptional != null ? localOptional.orNull() : null);
        }
        return localArrayList;
      }
    });
  }
  
  private static class MappingCheckedFuture
    extends AbstractCheckedFuture
  {
    final Function mapper;
    
    MappingCheckedFuture(ListenableFuture paramListenableFuture, Function paramFunction)
    {
      super();
      this.mapper = ((Function)Preconditions.checkNotNull(paramFunction));
    }
    
    protected Exception mapException(Exception paramException)
    {
      return (Exception)this.mapper.apply(paramException);
    }
  }
  
  private static class CombinedFuture
    extends AbstractFuture
  {
    ImmutableCollection futures;
    final boolean allMustSucceed;
    final AtomicInteger remaining;
    Futures.FutureCombiner combiner;
    List values;
    
    CombinedFuture(ImmutableCollection paramImmutableCollection, boolean paramBoolean, Executor paramExecutor, Futures.FutureCombiner paramFutureCombiner)
    {
      this.futures = paramImmutableCollection;
      this.allMustSucceed = paramBoolean;
      this.remaining = new AtomicInteger(paramImmutableCollection.size());
      this.combiner = paramFutureCombiner;
      this.values = Lists.newArrayListWithCapacity(paramImmutableCollection.size());
      init(paramExecutor);
    }
    
    protected void init(Executor paramExecutor)
    {
      addListener(new Runnable()
      {
        public void run()
        {
          if (Futures.CombinedFuture.this.isCancelled())
          {
            Iterator localIterator = Futures.CombinedFuture.this.futures.iterator();
            while (localIterator.hasNext())
            {
              ListenableFuture localListenableFuture = (ListenableFuture)localIterator.next();
              localListenableFuture.cancel(Futures.CombinedFuture.this.wasInterrupted());
            }
          }
          Futures.CombinedFuture.this.futures = null;
          Futures.CombinedFuture.this.values = null;
          Futures.CombinedFuture.this.combiner = null;
        }
      }, MoreExecutors.sameThreadExecutor());
      if (this.futures.isEmpty())
      {
        set(this.combiner.combine(ImmutableList.of()));
        return;
      }
      for (int i = 0; i < this.futures.size(); i++) {
        this.values.add(null);
      }
      i = 0;
      Iterator localIterator = this.futures.iterator();
      while (localIterator.hasNext())
      {
        final ListenableFuture localListenableFuture = (ListenableFuture)localIterator.next();
        final int j = i++;
        localListenableFuture.addListener(new Runnable()
        {
          public void run()
          {
            Futures.CombinedFuture.this.setOneValue(j, localListenableFuture);
          }
        }, paramExecutor);
      }
    }
    
    private void setOneValue(int paramInt, Future paramFuture)
    {
      List localList = this.values;
      if ((isDone()) || (localList == null))
      {
        Preconditions.checkState((this.allMustSucceed) || (isCancelled()), "Future was done before all dependencies completed");
        return;
      }
      try
      {
        Preconditions.checkState(paramFuture.isDone(), "Tried to set value from future which is not done");
        Object localObject1 = Uninterruptibles.getUninterruptibly(paramFuture);
        localList.set(paramInt, Optional.fromNullable(localObject1));
      }
      catch (CancellationException localCancellationException)
      {
        int i;
        if (this.allMustSucceed) {
          cancel(false);
        }
      }
      catch (ExecutionException localExecutionException)
      {
        int j;
        if (this.allMustSucceed) {
          setException(localExecutionException.getCause());
        }
      }
      catch (RuntimeException localRuntimeException)
      {
        int k;
        if (this.allMustSucceed) {
          setException(localRuntimeException);
        }
      }
      catch (Error localError)
      {
        int m;
        setException(localError);
      }
      finally
      {
        Futures.FutureCombiner localFutureCombiner1;
        int n;
        int i1 = this.remaining.decrementAndGet();
        Preconditions.checkState(i1 >= 0, "Less than 0 remaining futures");
        if (i1 == 0)
        {
          Futures.FutureCombiner localFutureCombiner2 = this.combiner;
          if (localFutureCombiner2 != null) {
            set(localFutureCombiner2.combine(localList));
          } else {
            Preconditions.checkState(isDone());
          }
        }
      }
    }
  }
  
  private static abstract interface FutureCombiner
  {
    public abstract Object combine(List paramList);
  }
  
  private static class ChainingListenableFuture
    extends AbstractFuture
    implements Runnable
  {
    private AsyncFunction function;
    private ListenableFuture inputFuture;
    private volatile ListenableFuture outputFuture;
    private final CountDownLatch outputCreated = new CountDownLatch(1);
    
    private ChainingListenableFuture(AsyncFunction paramAsyncFunction, ListenableFuture paramListenableFuture)
    {
      this.function = ((AsyncFunction)Preconditions.checkNotNull(paramAsyncFunction));
      this.inputFuture = ((ListenableFuture)Preconditions.checkNotNull(paramListenableFuture));
    }
    
    public boolean cancel(boolean paramBoolean)
    {
      if (super.cancel(paramBoolean))
      {
        cancel(this.inputFuture, paramBoolean);
        cancel(this.outputFuture, paramBoolean);
        return true;
      }
      return false;
    }
    
    private void cancel(Future paramFuture, boolean paramBoolean)
    {
      if (paramFuture != null) {
        paramFuture.cancel(paramBoolean);
      }
    }
    
    public void run()
    {
      try
      {
        Object localObject1;
        try
        {
          localObject1 = Uninterruptibles.getUninterruptibly(this.inputFuture);
        }
        catch (CancellationException localCancellationException)
        {
          cancel(false);
          return;
        }
        catch (ExecutionException localExecutionException)
        {
          setException(localExecutionException.getCause());
          return;
        }
        final ListenableFuture localListenableFuture = this.outputFuture = this.function.apply(localObject1);
        if (isCancelled())
        {
          localListenableFuture.cancel(wasInterrupted());
          this.outputFuture = null;
          return;
        }
        localListenableFuture.addListener(new Runnable()
        {
          public void run()
          {
            try
            {
              Futures.ChainingListenableFuture.this.set(Uninterruptibles.getUninterruptibly(localListenableFuture));
            }
            catch (CancellationException localCancellationException)
            {
              Futures.ChainingListenableFuture.this.cancel(false);
              return;
            }
            catch (ExecutionException localExecutionException)
            {
              Futures.ChainingListenableFuture.this.setException(localExecutionException.getCause());
            }
            finally
            {
              Futures.ChainingListenableFuture.this.outputFuture = null;
            }
          }
        }, MoreExecutors.sameThreadExecutor());
      }
      catch (UndeclaredThrowableException localUndeclaredThrowableException)
      {
        setException(localUndeclaredThrowableException.getCause());
      }
      catch (Exception localException)
      {
        setException(localException);
      }
      catch (Error localError)
      {
        setException(localError);
      }
      finally
      {
        this.function = null;
        this.inputFuture = null;
        this.outputCreated.countDown();
      }
    }
  }
  
  private static class FallbackFuture
    extends AbstractFuture
  {
    private volatile ListenableFuture running;
    
    FallbackFuture(ListenableFuture paramListenableFuture, final FutureFallback paramFutureFallback, Executor paramExecutor)
    {
      this.running = paramListenableFuture;
      Futures.addCallback(this.running, new FutureCallback()
      {
        public void onSuccess(Object paramAnonymousObject)
        {
          Futures.FallbackFuture.this.set(paramAnonymousObject);
        }
        
        public void onFailure(Throwable paramAnonymousThrowable)
        {
          if (Futures.FallbackFuture.this.isCancelled()) {
            return;
          }
          try
          {
            Futures.FallbackFuture.this.running = paramFutureFallback.create(paramAnonymousThrowable);
            if (Futures.FallbackFuture.this.isCancelled())
            {
              Futures.FallbackFuture.this.running.cancel(Futures.FallbackFuture.this.wasInterrupted());
              return;
            }
            Futures.addCallback(Futures.FallbackFuture.this.running, new FutureCallback()
            {
              public void onSuccess(Object paramAnonymous2Object)
              {
                Futures.FallbackFuture.this.set(paramAnonymous2Object);
              }
              
              public void onFailure(Throwable paramAnonymous2Throwable)
              {
                if (Futures.FallbackFuture.this.running.isCancelled()) {
                  Futures.FallbackFuture.this.cancel(false);
                } else {
                  Futures.FallbackFuture.this.setException(paramAnonymous2Throwable);
                }
              }
            }, MoreExecutors.sameThreadExecutor());
          }
          catch (Exception localException)
          {
            Futures.FallbackFuture.this.setException(localException);
          }
          catch (Error localError)
          {
            Futures.FallbackFuture.this.setException(localError);
          }
        }
      }, paramExecutor);
    }
    
    public boolean cancel(boolean paramBoolean)
    {
      if (super.cancel(paramBoolean))
      {
        this.running.cancel(paramBoolean);
        return true;
      }
      return false;
    }
  }
  
  private static class ImmediateFailedCheckedFuture
    extends Futures.ImmediateFuture
    implements CheckedFuture
  {
    private final Exception thrown;
    
    ImmediateFailedCheckedFuture(Exception paramException)
    {
      super();
      this.thrown = paramException;
    }
    
    public Object get()
      throws ExecutionException
    {
      throw new ExecutionException(this.thrown);
    }
    
    public Object checkedGet()
      throws Exception
    {
      throw this.thrown;
    }
    
    public Object checkedGet(long paramLong, TimeUnit paramTimeUnit)
      throws Exception
    {
      Preconditions.checkNotNull(paramTimeUnit);
      throw this.thrown;
    }
  }
  
  private static class ImmediateCancelledFuture
    extends Futures.ImmediateFuture
  {
    private final CancellationException thrown = new CancellationException("Immediate cancelled future.");
    
    ImmediateCancelledFuture()
    {
      super();
    }
    
    public boolean isCancelled()
    {
      return true;
    }
    
    public Object get()
    {
      throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.thrown);
    }
  }
  
  private static class ImmediateFailedFuture
    extends Futures.ImmediateFuture
  {
    private final Throwable thrown;
    
    ImmediateFailedFuture(Throwable paramThrowable)
    {
      super();
      this.thrown = paramThrowable;
    }
    
    public Object get()
      throws ExecutionException
    {
      throw new ExecutionException(this.thrown);
    }
  }
  
  private static class ImmediateSuccessfulCheckedFuture
    extends Futures.ImmediateFuture
    implements CheckedFuture
  {
    private final Object value;
    
    ImmediateSuccessfulCheckedFuture(Object paramObject)
    {
      super();
      this.value = paramObject;
    }
    
    public Object get()
    {
      return this.value;
    }
    
    public Object checkedGet()
    {
      return this.value;
    }
    
    public Object checkedGet(long paramLong, TimeUnit paramTimeUnit)
    {
      Preconditions.checkNotNull(paramTimeUnit);
      return this.value;
    }
  }
  
  private static class ImmediateSuccessfulFuture
    extends Futures.ImmediateFuture
  {
    private final Object value;
    
    ImmediateSuccessfulFuture(Object paramObject)
    {
      super();
      this.value = paramObject;
    }
    
    public Object get()
    {
      return this.value;
    }
  }
  
  private static abstract class ImmediateFuture
    implements ListenableFuture
  {
    private static final Logger log = Logger.getLogger(ImmediateFuture.class.getName());
    
    public void addListener(Runnable paramRunnable, Executor paramExecutor)
    {
      Preconditions.checkNotNull(paramRunnable, "Runnable was null.");
      Preconditions.checkNotNull(paramExecutor, "Executor was null.");
      try
      {
        paramExecutor.execute(paramRunnable);
      }
      catch (RuntimeException localRuntimeException)
      {
        log.log(Level.SEVERE, "RuntimeException while executing runnable " + paramRunnable + " with executor " + paramExecutor, localRuntimeException);
      }
    }
    
    public boolean cancel(boolean paramBoolean)
    {
      return false;
    }
    
    public abstract Object get()
      throws ExecutionException;
    
    public Object get(long paramLong, TimeUnit paramTimeUnit)
      throws ExecutionException
    {
      Preconditions.checkNotNull(paramTimeUnit);
      return get();
    }
    
    public boolean isCancelled()
    {
      return false;
    }
    
    public boolean isDone()
    {
      return true;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\Futures.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */