package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..ClassReader;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public abstract class $AbstractClassGenerator
  implements .ClassGenerator
{
  private static final Object NAME_KEY = new Object();
  private static final ThreadLocal CURRENT = new ThreadLocal();
  private .GeneratorStrategy strategy = .DefaultGeneratorStrategy.INSTANCE;
  private .NamingPolicy namingPolicy = .DefaultNamingPolicy.INSTANCE;
  private Source source;
  private ClassLoader classLoader;
  private String namePrefix;
  private Object key;
  private boolean useCache = true;
  private String className;
  private boolean attemptLoad;
  
  protected $AbstractClassGenerator(Source paramSource)
  {
    this.source = paramSource;
  }
  
  protected void setNamePrefix(String paramString)
  {
    this.namePrefix = paramString;
  }
  
  protected final String getClassName()
  {
    if (this.className == null) {
      this.className = getClassName(getClassLoader());
    }
    return this.className;
  }
  
  private String getClassName(ClassLoader paramClassLoader)
  {
    Set localSet = getClassNameCache(paramClassLoader);
    this.namingPolicy.getClassName(this.namePrefix, this.source.name, this.key, new .Predicate()
    {
      private final Set val$nameCache;
      
      public boolean evaluate(Object paramAnonymousObject)
      {
        return this.val$nameCache.contains(paramAnonymousObject);
      }
    });
  }
  
  private Set getClassNameCache(ClassLoader paramClassLoader)
  {
    return (Set)((Map)this.source.cache.get(paramClassLoader)).get(NAME_KEY);
  }
  
  public void setClassLoader(ClassLoader paramClassLoader)
  {
    this.classLoader = paramClassLoader;
  }
  
  public void setNamingPolicy(.NamingPolicy paramNamingPolicy)
  {
    if (paramNamingPolicy == null) {
      paramNamingPolicy = .DefaultNamingPolicy.INSTANCE;
    }
    this.namingPolicy = paramNamingPolicy;
  }
  
  public .NamingPolicy getNamingPolicy()
  {
    return this.namingPolicy;
  }
  
  public void setUseCache(boolean paramBoolean)
  {
    this.useCache = paramBoolean;
  }
  
  public boolean getUseCache()
  {
    return this.useCache;
  }
  
  public void setAttemptLoad(boolean paramBoolean)
  {
    this.attemptLoad = paramBoolean;
  }
  
  public boolean getAttemptLoad()
  {
    return this.attemptLoad;
  }
  
  public void setStrategy(.GeneratorStrategy paramGeneratorStrategy)
  {
    if (paramGeneratorStrategy == null) {
      paramGeneratorStrategy = .DefaultGeneratorStrategy.INSTANCE;
    }
    this.strategy = paramGeneratorStrategy;
  }
  
  public .GeneratorStrategy getStrategy()
  {
    return this.strategy;
  }
  
  public static AbstractClassGenerator getCurrent()
  {
    return (AbstractClassGenerator)CURRENT.get();
  }
  
  public ClassLoader getClassLoader()
  {
    ClassLoader localClassLoader = this.classLoader;
    if (localClassLoader == null) {
      localClassLoader = getDefaultClassLoader();
    }
    if (localClassLoader == null) {
      localClassLoader = getClass().getClassLoader();
    }
    if (localClassLoader == null) {
      localClassLoader = Thread.currentThread().getContextClassLoader();
    }
    if (localClassLoader == null) {
      throw new IllegalStateException("Cannot determine classloader");
    }
    return localClassLoader;
  }
  
  protected abstract ClassLoader getDefaultClassLoader();
  
  protected Object create(Object paramObject)
  {
    try
    {
      Class localClass = null;
      synchronized (this.source)
      {
        ClassLoader localClassLoader = getClassLoader();
        Object localObject1 = null;
        localObject1 = (Map)this.source.cache.get(localClassLoader);
        Object localObject2;
        if (localObject1 == null)
        {
          localObject1 = new HashMap();
          ((Map)localObject1).put(NAME_KEY, new HashSet());
          this.source.cache.put(localClassLoader, localObject1);
        }
        else if (this.useCache)
        {
          localObject2 = (Reference)((Map)localObject1).get(paramObject);
          localClass = (Class)(localObject2 == null ? null : ((Reference)localObject2).get());
        }
        if (localClass == null)
        {
          localObject2 = CURRENT.get();
          CURRENT.set(this);
          try
          {
            this.key = paramObject;
            if (this.attemptLoad) {
              try
              {
                localClass = localClassLoader.loadClass(getClassName());
              }
              catch (ClassNotFoundException localClassNotFoundException) {}
            }
            if (localClass == null)
            {
              localObject3 = this.strategy.generate(this);
              String str = .ClassNameReader.getClassName(new .ClassReader((byte[])localObject3));
              getClassNameCache(localClassLoader).add(str);
              localClass = .ReflectUtils.defineClass(str, (byte[])localObject3, localClassLoader);
            }
            if (this.useCache) {
              ((Map)localObject1).put(paramObject, new WeakReference(localClass));
            }
            Object localObject3 = firstInstance(localClass);
            CURRENT.set(localObject2);
            return localObject3;
          }
          finally
          {
            CURRENT.set(localObject2);
          }
        }
      }
      return firstInstance(localClass);
    }
    catch (RuntimeException localRuntimeException)
    {
      throw localRuntimeException;
    }
    catch (Error localError)
    {
      throw localError;
    }
    catch (Exception localException)
    {
      throw new .CodeGenerationException(localException);
    }
  }
  
  protected abstract Object firstInstance(Class paramClass)
    throws Exception;
  
  protected abstract Object nextInstance(Object paramObject)
    throws Exception;
  
  protected static class Source
  {
    String name;
    Map cache = new WeakHashMap();
    
    public Source(String paramString)
    {
      this.name = paramString;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$AbstractClassGenerator.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */