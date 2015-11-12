package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

@Beta
public final class Closer
  implements Closeable
{
  private static final Suppressor SUPPRESSOR = SuppressingSuppressor.isAvailable() ? SuppressingSuppressor.INSTANCE : LoggingSuppressor.INSTANCE;
  @VisibleForTesting
  final Suppressor suppressor;
  private final Deque stack = new ArrayDeque(4);
  private Throwable thrown;
  
  public static Closer create()
  {
    return new Closer(SUPPRESSOR);
  }
  
  @VisibleForTesting
  Closer(Suppressor paramSuppressor)
  {
    this.suppressor = ((Suppressor)Preconditions.checkNotNull(paramSuppressor));
  }
  
  public Closeable register(Closeable paramCloseable)
  {
    this.stack.push(paramCloseable);
    return paramCloseable;
  }
  
  public RuntimeException rethrow(Throwable paramThrowable)
    throws IOException
  {
    this.thrown = paramThrowable;
    Throwables.propagateIfPossible(paramThrowable, IOException.class);
    throw Throwables.propagate(paramThrowable);
  }
  
  public RuntimeException rethrow(Throwable paramThrowable, Class paramClass)
    throws IOException, Exception
  {
    this.thrown = paramThrowable;
    Throwables.propagateIfPossible(paramThrowable, IOException.class);
    Throwables.propagateIfPossible(paramThrowable, paramClass);
    throw Throwables.propagate(paramThrowable);
  }
  
  public RuntimeException rethrow(Throwable paramThrowable, Class paramClass1, Class paramClass2)
    throws IOException, Exception, Exception
  {
    this.thrown = paramThrowable;
    Throwables.propagateIfPossible(paramThrowable, IOException.class);
    Throwables.propagateIfPossible(paramThrowable, paramClass1, paramClass2);
    throw Throwables.propagate(paramThrowable);
  }
  
  public void close()
    throws IOException
  {
    Object localObject = this.thrown;
    while (!this.stack.isEmpty())
    {
      Closeable localCloseable = (Closeable)this.stack.pop();
      try
      {
        localCloseable.close();
      }
      catch (Throwable localThrowable)
      {
        if (localObject == null) {
          localObject = localThrowable;
        } else {
          this.suppressor.suppress(localCloseable, (Throwable)localObject, localThrowable);
        }
      }
    }
    if ((this.thrown == null) && (localObject != null))
    {
      Throwables.propagateIfPossible((Throwable)localObject, IOException.class);
      throw new AssertionError(localObject);
    }
  }
  
  @VisibleForTesting
  static final class SuppressingSuppressor
    implements Closer.Suppressor
  {
    static final SuppressingSuppressor INSTANCE = new SuppressingSuppressor();
    static final Method addSuppressed = getAddSuppressed();
    
    static boolean isAvailable()
    {
      return addSuppressed != null;
    }
    
    private static Method getAddSuppressed()
    {
      try
      {
        return Throwable.class.getMethod("addSuppressed", new Class[] { Throwable.class });
      }
      catch (Throwable localThrowable) {}
      return null;
    }
    
    public void suppress(Closeable paramCloseable, Throwable paramThrowable1, Throwable paramThrowable2)
    {
      if (paramThrowable1 == paramThrowable2) {
        return;
      }
      try
      {
        addSuppressed.invoke(paramThrowable1, new Object[] { paramThrowable2 });
      }
      catch (Throwable localThrowable)
      {
        Closer.LoggingSuppressor.INSTANCE.suppress(paramCloseable, paramThrowable1, paramThrowable2);
      }
    }
  }
  
  @VisibleForTesting
  static final class LoggingSuppressor
    implements Closer.Suppressor
  {
    static final LoggingSuppressor INSTANCE = new LoggingSuppressor();
    
    public void suppress(Closeable paramCloseable, Throwable paramThrowable1, Throwable paramThrowable2)
    {
      Closeables.logger.log(Level.WARNING, "Suppressing exception thrown when closing " + paramCloseable, paramThrowable2);
    }
  }
  
  @VisibleForTesting
  static abstract interface Suppressor
  {
    public abstract void suppress(Closeable paramCloseable, Throwable paramThrowable1, Throwable paramThrowable2);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\Closer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */