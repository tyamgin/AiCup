package com.google.common.base;

import com.google.common.annotations.VisibleForTesting;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinalizableReferenceQueue
  implements Closeable
{
  private static final Logger logger = Logger.getLogger(FinalizableReferenceQueue.class.getName());
  private static final String FINALIZER_CLASS_NAME = "com.google.common.base.internal.Finalizer";
  private static final Method startFinalizer;
  final ReferenceQueue queue = new ReferenceQueue();
  final PhantomReference frqRef = new PhantomReference(this, this.queue);
  final boolean threadStarted;
  
  public FinalizableReferenceQueue()
  {
    boolean bool = false;
    try
    {
      startFinalizer.invoke(null, new Object[] { FinalizableReference.class, this.queue, this.frqRef });
      bool = true;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new AssertionError(localIllegalAccessException);
    }
    catch (Throwable localThrowable)
    {
      logger.log(Level.INFO, "Failed to start reference finalizer thread. Reference cleanup will only occur when new references are created.", localThrowable);
    }
    this.threadStarted = bool;
  }
  
  public void close()
  {
    this.frqRef.enqueue();
    cleanUp();
  }
  
  void cleanUp()
  {
    if (this.threadStarted) {
      return;
    }
    Reference localReference;
    while ((localReference = this.queue.poll()) != null)
    {
      localReference.clear();
      try
      {
        ((FinalizableReference)localReference).finalizeReferent();
      }
      catch (Throwable localThrowable)
      {
        logger.log(Level.SEVERE, "Error cleaning up after reference.", localThrowable);
      }
    }
  }
  
  private static Class loadFinalizer(FinalizerLoader... paramVarArgs)
  {
    for (FinalizerLoader localFinalizerLoader : paramVarArgs)
    {
      Class localClass = localFinalizerLoader.loadFinalizer();
      if (localClass != null) {
        return localClass;
      }
    }
    throw new AssertionError();
  }
  
  static Method getStartFinalizer(Class paramClass)
  {
    try
    {
      return paramClass.getMethod("startFinalizer", new Class[] { Class.class, ReferenceQueue.class, PhantomReference.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new AssertionError(localNoSuchMethodException);
    }
  }
  
  static
  {
    Class localClass = loadFinalizer(new FinalizerLoader[] { new SystemLoader(), new DecoupledLoader(), new DirectLoader() });
    startFinalizer = getStartFinalizer(localClass);
  }
  
  static class DirectLoader
    implements FinalizableReferenceQueue.FinalizerLoader
  {
    public Class loadFinalizer()
    {
      try
      {
        return Class.forName("com.google.common.base.internal.Finalizer");
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        throw new AssertionError(localClassNotFoundException);
      }
    }
  }
  
  static class DecoupledLoader
    implements FinalizableReferenceQueue.FinalizerLoader
  {
    private static final String LOADING_ERROR = "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.";
    
    public Class loadFinalizer()
    {
      try
      {
        URLClassLoader localURLClassLoader = newLoader(getBaseUrl());
        return localURLClassLoader.loadClass("com.google.common.base.internal.Finalizer");
      }
      catch (Exception localException)
      {
        FinalizableReferenceQueue.logger.log(Level.WARNING, "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.", localException);
      }
      return null;
    }
    
    URL getBaseUrl()
      throws IOException
    {
      String str1 = "com.google.common.base.internal.Finalizer".replace('.', '/') + ".class";
      URL localURL = getClass().getClassLoader().getResource(str1);
      if (localURL == null) {
        throw new FileNotFoundException(str1);
      }
      String str2 = localURL.toString();
      if (!str2.endsWith(str1)) {
        throw new IOException("Unsupported path style: " + str2);
      }
      str2 = str2.substring(0, str2.length() - str1.length());
      return new URL(localURL, str2);
    }
    
    URLClassLoader newLoader(URL paramURL)
    {
      return new URLClassLoader(new URL[] { paramURL }, null);
    }
  }
  
  static class SystemLoader
    implements FinalizableReferenceQueue.FinalizerLoader
  {
    @VisibleForTesting
    static boolean disabled;
    
    public Class loadFinalizer()
    {
      if (disabled) {
        return null;
      }
      ClassLoader localClassLoader;
      try
      {
        localClassLoader = ClassLoader.getSystemClassLoader();
      }
      catch (SecurityException localSecurityException)
      {
        FinalizableReferenceQueue.logger.info("Not allowed to access system class loader.");
        return null;
      }
      if (localClassLoader != null) {
        try
        {
          return localClassLoader.loadClass("com.google.common.base.internal.Finalizer");
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          return null;
        }
      }
      return null;
    }
  }
  
  static abstract interface FinalizerLoader
  {
    public abstract Class loadFinalizer();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\FinalizableReferenceQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */