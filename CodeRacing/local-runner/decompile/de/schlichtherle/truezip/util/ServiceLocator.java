package de.schlichtherle.truezip.util;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public final class ServiceLocator
{
  private final ClassLoader l1;
  
  public ServiceLocator()
  {
    this(null);
  }
  
  public ServiceLocator(ClassLoader paramClassLoader)
  {
    this.l1 = (null != paramClassLoader ? paramClassLoader : ClassLoader.getSystemClassLoader());
  }
  
  public Iterator getServices(Class paramClass)
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    return this.l1 == localClassLoader ? ServiceLoader.load(paramClass, this.l1).iterator() : new JointIterator(ServiceLoader.load(paramClass, this.l1).iterator(), ServiceLoader.load(paramClass, localClassLoader).iterator());
  }
  
  public Object getService(Class paramClass1, Class paramClass2)
  {
    String str = System.getProperty(paramClass1.getName(), null == paramClass2 ? null : paramClass2.getName());
    if (null == str) {
      return null;
    }
    try
    {
      return paramClass2.cast(getClass(str).newInstance());
    }
    catch (ClassCastException localClassCastException)
    {
      throw new ServiceConfigurationError(localClassCastException.toString(), localClassCastException);
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new ServiceConfigurationError(localInstantiationException.toString(), localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ServiceConfigurationError(localIllegalAccessException.toString(), localIllegalAccessException);
    }
  }
  
  public Class getClass(String paramString)
  {
    try
    {
      return this.l1.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException1)
    {
      ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
      if (this.l1 == localClassLoader) {
        throw localClassNotFoundException1;
      }
      return localClassLoader.loadClass(paramString);
    }
    catch (ClassNotFoundException localClassNotFoundException2)
    {
      throw new ServiceConfigurationError(localClassNotFoundException2.toString(), localClassNotFoundException2);
    }
  }
  
  static
  {
    Logger.getLogger(ServiceLocator.class.getName(), ServiceLocator.class.getName()).config("banner");
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\ServiceLocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */