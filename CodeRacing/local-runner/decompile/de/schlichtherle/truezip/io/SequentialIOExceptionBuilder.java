package de.schlichtherle.truezip.io;

import de.schlichtherle.truezip.util.AbstractExceptionBuilder;
import java.lang.reflect.Constructor;

public class SequentialIOExceptionBuilder
  extends AbstractExceptionBuilder
{
  private final Class assemblyClass;
  private volatile Constructor assemblyConstructor;
  
  public SequentialIOExceptionBuilder(Class paramClass1, Class paramClass2)
  {
    this.assemblyClass = paramClass2;
    try
    {
      if (!paramClass2.isAssignableFrom(paramClass1)) {
        wrap(null);
      }
    }
    catch (IllegalStateException localIllegalStateException)
    {
      throw new IllegalArgumentException(localIllegalStateException.getCause());
    }
  }
  
  private Class assemblyClass()
  {
    return this.assemblyClass;
  }
  
  private Constructor assemblyConstructor()
  {
    Constructor localConstructor = this.assemblyConstructor;
    return null != localConstructor ? localConstructor : (this.assemblyConstructor = newAssemblyConstructor());
  }
  
  private Constructor newAssemblyConstructor()
  {
    try
    {
      return assemblyClass().getConstructor(new Class[] { String.class });
    }
    catch (Exception localException)
    {
      throw new IllegalStateException(localException);
    }
  }
  
  protected final SequentialIOException update(Exception paramException, SequentialIOException paramSequentialIOException)
  {
    SequentialIOException localSequentialIOException = null;
    if (assemblyClass().isInstance(paramException))
    {
      localSequentialIOException = (SequentialIOException)paramException;
      if (localSequentialIOException.isInitPredecessor())
      {
        if (null == paramSequentialIOException) {
          return localSequentialIOException;
        }
        localSequentialIOException = null;
      }
    }
    if (null == localSequentialIOException) {
      localSequentialIOException = wrap(paramException);
    }
    localSequentialIOException.initPredecessor(paramSequentialIOException);
    return localSequentialIOException;
  }
  
  private SequentialIOException wrap(Exception paramException)
  {
    SequentialIOException localSequentialIOException = newAssembly(toString(paramException));
    localSequentialIOException.initCause(paramException);
    return localSequentialIOException;
  }
  
  private static String toString(Object paramObject)
  {
    return null == paramObject ? "" : paramObject.toString();
  }
  
  private SequentialIOException newAssembly(String paramString)
  {
    Constructor localConstructor = assemblyConstructor();
    try
    {
      return (SequentialIOException)localConstructor.newInstance(new Object[] { paramString });
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      localConstructor.setAccessible(true);
      return (SequentialIOException)localConstructor.newInstance(new Object[] { paramString });
    }
    catch (Exception localException)
    {
      throw new IllegalStateException(localException);
    }
  }
  
  protected final SequentialIOException post(SequentialIOException paramSequentialIOException)
  {
    return paramSequentialIOException.sortPriority();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\io\SequentialIOExceptionBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */