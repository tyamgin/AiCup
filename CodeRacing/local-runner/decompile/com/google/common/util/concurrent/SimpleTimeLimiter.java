package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public final class SimpleTimeLimiter
  implements TimeLimiter
{
  private final ExecutorService executor;
  
  public SimpleTimeLimiter(ExecutorService paramExecutorService)
  {
    this.executor = ((ExecutorService)Preconditions.checkNotNull(paramExecutorService));
  }
  
  public SimpleTimeLimiter()
  {
    this(Executors.newCachedThreadPool());
  }
  
  public Object newProxy(final Object paramObject, Class paramClass, final long paramLong, TimeUnit paramTimeUnit)
  {
    Preconditions.checkNotNull(paramObject);
    Preconditions.checkNotNull(paramClass);
    Preconditions.checkNotNull(paramTimeUnit);
    Preconditions.checkArgument(paramLong > 0L, "bad timeout: " + paramLong);
    Preconditions.checkArgument(paramClass.isInterface(), "interfaceType must be an interface type");
    final Set localSet = findInterruptibleMethods(paramClass);
    InvocationHandler local1 = new InvocationHandler()
    {
      public Object invoke(Object paramAnonymousObject, final Method paramAnonymousMethod, final Object[] paramAnonymousArrayOfObject)
        throws Throwable
      {
        Callable local1 = new Callable()
        {
          public Object call()
            throws Exception
          {
            try
            {
              return paramAnonymousMethod.invoke(SimpleTimeLimiter.1.this.val$target, paramAnonymousArrayOfObject);
            }
            catch (InvocationTargetException localInvocationTargetException)
            {
              SimpleTimeLimiter.throwCause(localInvocationTargetException, false);
              throw new AssertionError("can't get here");
            }
          }
        };
        return SimpleTimeLimiter.this.callWithTimeout(local1, paramLong, localSet, this.val$interruptibleMethods.contains(paramAnonymousMethod));
      }
    };
    return newProxy(paramClass, local1);
  }
  
  public Object callWithTimeout(Callable paramCallable, long paramLong, TimeUnit paramTimeUnit, boolean paramBoolean)
    throws Exception
  {
    Preconditions.checkNotNull(paramCallable);
    Preconditions.checkNotNull(paramTimeUnit);
    Preconditions.checkArgument(paramLong > 0L, "timeout must be positive: %s", new Object[] { Long.valueOf(paramLong) });
    Future localFuture = this.executor.submit(paramCallable);
    try
    {
      if (paramBoolean) {
        try
        {
          return localFuture.get(paramLong, paramTimeUnit);
        }
        catch (InterruptedException localInterruptedException)
        {
          localFuture.cancel(true);
          throw localInterruptedException;
        }
      }
      return Uninterruptibles.getUninterruptibly(localFuture, paramLong, paramTimeUnit);
    }
    catch (ExecutionException localExecutionException)
    {
      throw throwCause(localExecutionException, true);
    }
    catch (TimeoutException localTimeoutException)
    {
      localFuture.cancel(true);
      throw new UncheckedTimeoutException(localTimeoutException);
    }
  }
  
  private static Exception throwCause(Exception paramException, boolean paramBoolean)
    throws Exception
  {
    Throwable localThrowable = paramException.getCause();
    if (localThrowable == null) {
      throw paramException;
    }
    if (paramBoolean)
    {
      StackTraceElement[] arrayOfStackTraceElement = (StackTraceElement[])ObjectArrays.concat(localThrowable.getStackTrace(), paramException.getStackTrace(), StackTraceElement.class);
      localThrowable.setStackTrace(arrayOfStackTraceElement);
    }
    if ((localThrowable instanceof Exception)) {
      throw ((Exception)localThrowable);
    }
    if ((localThrowable instanceof Error)) {
      throw ((Error)localThrowable);
    }
    throw paramException;
  }
  
  private static Set findInterruptibleMethods(Class paramClass)
  {
    HashSet localHashSet = Sets.newHashSet();
    for (Method localMethod : paramClass.getMethods()) {
      if (declaresInterruptedEx(localMethod)) {
        localHashSet.add(localMethod);
      }
    }
    return localHashSet;
  }
  
  private static boolean declaresInterruptedEx(Method paramMethod)
  {
    for (Class localClass : paramMethod.getExceptionTypes()) {
      if (localClass == InterruptedException.class) {
        return true;
      }
    }
    return false;
  }
  
  private static Object newProxy(Class paramClass, InvocationHandler paramInvocationHandler)
  {
    Object localObject = Proxy.newProxyInstance(paramClass.getClassLoader(), new Class[] { paramClass }, paramInvocationHandler);
    return paramClass.cast(localObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\SimpleTimeLimiter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */