package com.google.inject.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.internal.cglib.core..DefaultNamingPolicy;
import com.google.inject.internal.cglib.core..NamingPolicy;
import com.google.inject.internal.cglib.core..Predicate;
import com.google.inject.internal.cglib.proxy..Enhancer;
import com.google.inject.internal.cglib.reflect..FastClass;
import com.google.inject.internal.cglib.reflect..FastClass.Generator;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

public final class BytecodeGen
{
  static final Logger logger = Logger.getLogger(BytecodeGen.class.getName());
  static final ClassLoader GUICE_CLASS_LOADER = canonicalize(BytecodeGen.class.getClassLoader());
  static final String GUICE_INTERNAL_PACKAGE = BytecodeGen.class.getName().replaceFirst("\\.internal\\..*$", ".internal");
  static final String CGLIB_PACKAGE = .Enhancer.class.getName().replaceFirst("\\.cglib\\..*$", ".cglib");
  static final .NamingPolicy FASTCLASS_NAMING_POLICY = new .DefaultNamingPolicy()
  {
    protected String getTag()
    {
      return "ByGuice";
    }
    
    public String getClassName(String paramAnonymousString1, String paramAnonymousString2, Object paramAnonymousObject, .Predicate paramAnonymousPredicate)
    {
      return super.getClassName(paramAnonymousString1, "FastClass", paramAnonymousObject, paramAnonymousPredicate);
    }
  };
  static final .NamingPolicy ENHANCER_NAMING_POLICY = new .DefaultNamingPolicy()
  {
    protected String getTag()
    {
      return "ByGuice";
    }
    
    public String getClassName(String paramAnonymousString1, String paramAnonymousString2, Object paramAnonymousObject, .Predicate paramAnonymousPredicate)
    {
      return super.getClassName(paramAnonymousString1, "Enhancer", paramAnonymousObject, paramAnonymousPredicate);
    }
  };
  private static final LoadingCache CLASS_LOADER_CACHE;
  
  private static ClassLoader canonicalize(ClassLoader paramClassLoader)
  {
    return paramClassLoader != null ? paramClassLoader : SystemBridgeHolder.SYSTEM_BRIDGE.getParent();
  }
  
  public static ClassLoader getClassLoader(Class paramClass)
  {
    return getClassLoader(paramClass, paramClass.getClassLoader());
  }
  
  private static ClassLoader getClassLoader(Class paramClass, ClassLoader paramClassLoader)
  {
    if (InternalFlags.getCustomClassLoadingOption() == InternalFlags.CustomClassLoadingOption.OFF) {
      return paramClassLoader;
    }
    if (paramClass.getName().startsWith("java.")) {
      return GUICE_CLASS_LOADER;
    }
    paramClassLoader = canonicalize(paramClassLoader);
    if ((paramClassLoader == GUICE_CLASS_LOADER) || ((paramClassLoader instanceof BridgeClassLoader))) {
      return paramClassLoader;
    }
    if (Visibility.forType(paramClass) == Visibility.PUBLIC)
    {
      if (paramClassLoader != SystemBridgeHolder.SYSTEM_BRIDGE.getParent()) {
        return (ClassLoader)CLASS_LOADER_CACHE.getUnchecked(paramClassLoader);
      }
      return SystemBridgeHolder.SYSTEM_BRIDGE;
    }
    return paramClassLoader;
  }
  
  public static .FastClass newFastClass(Class paramClass, Visibility paramVisibility)
  {
    .FastClass.Generator localGenerator = new .FastClass.Generator();
    localGenerator.setType(paramClass);
    if (paramVisibility == Visibility.PUBLIC) {
      localGenerator.setClassLoader(getClassLoader(paramClass));
    }
    localGenerator.setNamingPolicy(FASTCLASS_NAMING_POLICY);
    String str1 = String.valueOf(String.valueOf(paramClass));
    String str2 = String.valueOf(String.valueOf(localGenerator.getClassLoader()));
    logger.fine(24 + str1.length() + str2.length() + "Loading " + str1 + " FastClass with " + str2);
    return localGenerator.create();
  }
  
  public static .Enhancer newEnhancer(Class paramClass, Visibility paramVisibility)
  {
    .Enhancer localEnhancer = new .Enhancer();
    localEnhancer.setSuperclass(paramClass);
    localEnhancer.setUseFactory(false);
    if (paramVisibility == Visibility.PUBLIC) {
      localEnhancer.setClassLoader(getClassLoader(paramClass));
    }
    localEnhancer.setNamingPolicy(ENHANCER_NAMING_POLICY);
    String str1 = String.valueOf(String.valueOf(paramClass));
    String str2 = String.valueOf(String.valueOf(localEnhancer.getClassLoader()));
    logger.fine(23 + str1.length() + str2.length() + "Loading " + str1 + " Enhancer with " + str2);
    return localEnhancer;
  }
  
  static
  {
    CacheBuilder localCacheBuilder = CacheBuilder.newBuilder().weakKeys().weakValues();
    if (InternalFlags.getCustomClassLoadingOption() == InternalFlags.CustomClassLoadingOption.OFF) {
      localCacheBuilder.maximumSize(0L);
    }
    CLASS_LOADER_CACHE = localCacheBuilder.build(new CacheLoader()
    {
      public ClassLoader load(final ClassLoader paramAnonymousClassLoader)
      {
        String str = String.valueOf(String.valueOf(paramAnonymousClassLoader));
        BytecodeGen.logger.fine(34 + str.length() + "Creating a bridge ClassLoader for " + str);
        (ClassLoader)AccessController.doPrivileged(new PrivilegedAction()
        {
          public ClassLoader run()
          {
            return new BytecodeGen.BridgeClassLoader(paramAnonymousClassLoader);
          }
        });
      }
    });
  }
  
  private static class BridgeClassLoader
    extends ClassLoader
  {
    BridgeClassLoader() {}
    
    BridgeClassLoader(ClassLoader paramClassLoader)
    {
      super();
    }
    
    protected Class loadClass(String paramString, boolean paramBoolean)
      throws ClassNotFoundException
    {
      if (paramString.startsWith("sun.reflect")) {
        return BytecodeGen.SystemBridgeHolder.SYSTEM_BRIDGE.classicLoadClass(paramString, paramBoolean);
      }
      if ((paramString.startsWith(BytecodeGen.GUICE_INTERNAL_PACKAGE)) || (paramString.startsWith(BytecodeGen.CGLIB_PACKAGE)))
      {
        if (null == BytecodeGen.GUICE_CLASS_LOADER) {
          return BytecodeGen.SystemBridgeHolder.SYSTEM_BRIDGE.classicLoadClass(paramString, paramBoolean);
        }
        try
        {
          Class localClass = BytecodeGen.GUICE_CLASS_LOADER.loadClass(paramString);
          if (paramBoolean) {
            resolveClass(localClass);
          }
          return localClass;
        }
        catch (Throwable localThrowable) {}
      }
      return classicLoadClass(paramString, paramBoolean);
    }
    
    Class classicLoadClass(String paramString, boolean paramBoolean)
      throws ClassNotFoundException
    {
      return super.loadClass(paramString, paramBoolean);
    }
  }
  
  public static abstract enum Visibility
  {
    PUBLIC,  SAME_PACKAGE;
    
    public static Visibility forMember(Member paramMember)
    {
      if ((paramMember.getModifiers() & 0x5) == 0) {
        return SAME_PACKAGE;
      }
      Class[] arrayOfClass;
      Object localObject;
      if ((paramMember instanceof Constructor))
      {
        arrayOfClass = ((Constructor)paramMember).getParameterTypes();
      }
      else
      {
        localObject = (Method)paramMember;
        if (forType(((Method)localObject).getReturnType()) == SAME_PACKAGE) {
          return SAME_PACKAGE;
        }
        arrayOfClass = ((Method)localObject).getParameterTypes();
      }
      for (Class localClass : arrayOfClass) {
        if (forType(localClass) == SAME_PACKAGE) {
          return SAME_PACKAGE;
        }
      }
      return PUBLIC;
    }
    
    public static Visibility forType(Class paramClass)
    {
      return (paramClass.getModifiers() & 0x5) != 0 ? PUBLIC : SAME_PACKAGE;
    }
    
    public abstract Visibility and(Visibility paramVisibility);
  }
  
  private static class SystemBridgeHolder
  {
    static final BytecodeGen.BridgeClassLoader SYSTEM_BRIDGE = new BytecodeGen.BridgeClassLoader();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\BytecodeGen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */