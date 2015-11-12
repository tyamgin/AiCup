package com.google.common.base.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Finalizer
  implements Runnable
{
  private static final Logger logger = Logger.getLogger(Finalizer.class.getName());
  private static final String FINALIZABLE_REFERENCE = "com.google.common.base.FinalizableReference";
  private final WeakReference finalizableReferenceClassReference;
  private final PhantomReference frqReference;
  private final ReferenceQueue queue;
  private static final Field inheritableThreadLocals = getInheritableThreadLocalsField();
  
  public static void startFinalizer(Class paramClass, ReferenceQueue paramReferenceQueue, PhantomReference paramPhantomReference)
  {
    if (!paramClass.getName().equals("com.google.common.base.FinalizableReference")) {
      throw new IllegalArgumentException("Expected com.google.common.base.FinalizableReference.");
    }
    Finalizer localFinalizer = new Finalizer(paramClass, paramReferenceQueue, paramPhantomReference);
    Thread localThread = new Thread(localFinalizer);
    localThread.setName(Finalizer.class.getName());
    localThread.setDaemon(true);
    try
    {
      if (inheritableThreadLocals != null) {
        inheritableThreadLocals.set(localThread, null);
      }
    }
    catch (Throwable localThrowable)
    {
      logger.log(Level.INFO, "Failed to clear thread local values inherited by reference finalizer thread.", localThrowable);
    }
    localThread.start();
  }
  
  private Finalizer(Class paramClass, ReferenceQueue paramReferenceQueue, PhantomReference paramPhantomReference)
  {
    this.queue = paramReferenceQueue;
    this.finalizableReferenceClassReference = new WeakReference(paramClass);
    this.frqReference = paramPhantomReference;
  }
  
  public void run()
  {
    try
    {
      try
      {
        for (;;)
        {
          cleanUp(this.queue.remove());
        }
      }
      catch (InterruptedException localInterruptedException) {}
    }
    catch (ShutDown localShutDown) {}
  }
  
  private void cleanUp(Reference paramReference)
    throws Finalizer.ShutDown
  {
    Method localMethod = getFinalizeReferentMethod();
    do
    {
      paramReference.clear();
      if (paramReference == this.frqReference) {
        throw new ShutDown(null);
      }
      try
      {
        localMethod.invoke(paramReference, new Object[0]);
      }
      catch (Throwable localThrowable)
      {
        logger.log(Level.SEVERE, "Error cleaning up after reference.", localThrowable);
      }
    } while ((paramReference = this.queue.poll()) != null);
  }
  
  private Method getFinalizeReferentMethod()
    throws Finalizer.ShutDown
  {
    Class localClass = (Class)this.finalizableReferenceClassReference.get();
    if (localClass == null) {
      throw new ShutDown(null);
    }
    try
    {
      return localClass.getMethod("finalizeReferent", new Class[0]);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new AssertionError(localNoSuchMethodException);
    }
  }
  
  public static Field getInheritableThreadLocalsField()
  {
    try
    {
      Field localField = Thread.class.getDeclaredField("inheritableThreadLocals");
      localField.setAccessible(true);
      return localField;
    }
    catch (Throwable localThrowable)
    {
      logger.log(Level.INFO, "Couldn't access Thread.inheritableThreadLocals. Reference finalizer threads will inherit thread local values.");
    }
    return null;
  }
  
  private static class ShutDown
    extends Exception
  {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\internal\Finalizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */