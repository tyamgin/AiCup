package com.google.inject.internal.cglib.proxy;

import com.google.inject.internal.cglib.core..AbstractClassGenerator;
import com.google.inject.internal.cglib.core..CodeGenerationException;
import com.google.inject.internal.cglib.core..GeneratorStrategy;
import com.google.inject.internal.cglib.core..NamingPolicy;
import com.google.inject.internal.cglib.core..Signature;
import com.google.inject.internal.cglib.reflect..FastClass;
import com.google.inject.internal.cglib.reflect..FastClass.Generator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class $MethodProxy
{
  private .Signature sig1;
  private .Signature sig2;
  private CreateInfo createInfo;
  private final Object initLock = new Object();
  private volatile FastClassInfo fastClassInfo;
  
  public static MethodProxy create(Class paramClass1, Class paramClass2, String paramString1, String paramString2, String paramString3)
  {
    MethodProxy localMethodProxy = new MethodProxy();
    localMethodProxy.sig1 = new .Signature(paramString2, paramString1);
    localMethodProxy.sig2 = new .Signature(paramString3, paramString1);
    localMethodProxy.createInfo = new CreateInfo(paramClass1, paramClass2);
    return localMethodProxy;
  }
  
  private void init()
  {
    if (this.fastClassInfo == null) {
      synchronized (this.initLock)
      {
        if (this.fastClassInfo == null)
        {
          CreateInfo localCreateInfo = this.createInfo;
          FastClassInfo localFastClassInfo = new FastClassInfo(null);
          localFastClassInfo.f1 = helper(localCreateInfo, localCreateInfo.c1);
          localFastClassInfo.f2 = helper(localCreateInfo, localCreateInfo.c2);
          localFastClassInfo.i1 = localFastClassInfo.f1.getIndex(this.sig1);
          localFastClassInfo.i2 = localFastClassInfo.f2.getIndex(this.sig2);
          this.fastClassInfo = localFastClassInfo;
          this.createInfo = null;
        }
      }
    }
  }
  
  private static .FastClass helper(CreateInfo paramCreateInfo, Class paramClass)
  {
    .FastClass.Generator localGenerator = new .FastClass.Generator();
    localGenerator.setType(paramClass);
    localGenerator.setClassLoader(paramCreateInfo.c2.getClassLoader());
    localGenerator.setNamingPolicy(paramCreateInfo.namingPolicy);
    localGenerator.setStrategy(paramCreateInfo.strategy);
    localGenerator.setAttemptLoad(paramCreateInfo.attemptLoad);
    return localGenerator.create();
  }
  
  public .Signature getSignature()
  {
    return this.sig1;
  }
  
  public String getSuperName()
  {
    return this.sig2.getName();
  }
  
  public int getSuperIndex()
  {
    init();
    return this.fastClassInfo.i2;
  }
  
  .FastClass getFastClass()
  {
    init();
    return this.fastClassInfo.f1;
  }
  
  .FastClass getSuperFastClass()
  {
    init();
    return this.fastClassInfo.f2;
  }
  
  public static MethodProxy find(Class paramClass, .Signature paramSignature)
  {
    try
    {
      Method localMethod = paramClass.getDeclaredMethod("CGLIB$findMethodProxy", .MethodInterceptorGenerator.FIND_PROXY_TYPES);
      return (MethodProxy)localMethod.invoke(null, new Object[] { paramSignature });
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new IllegalArgumentException("Class " + paramClass + " does not use a MethodInterceptor");
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new .CodeGenerationException(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new .CodeGenerationException(localInvocationTargetException);
    }
  }
  
  public Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws Throwable
  {
    try
    {
      init();
      FastClassInfo localFastClassInfo = this.fastClassInfo;
      return localFastClassInfo.f1.invoke(localFastClassInfo.i1, paramObject, paramArrayOfObject);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw localInvocationTargetException.getTargetException();
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      if (this.fastClassInfo.i1 < 0) {
        throw new IllegalArgumentException("Protected method: " + this.sig1);
      }
      throw localIllegalArgumentException;
    }
  }
  
  public Object invokeSuper(Object paramObject, Object[] paramArrayOfObject)
    throws Throwable
  {
    try
    {
      init();
      FastClassInfo localFastClassInfo = this.fastClassInfo;
      return localFastClassInfo.f2.invoke(localFastClassInfo.i2, paramObject, paramArrayOfObject);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw localInvocationTargetException.getTargetException();
    }
  }
  
  private static class CreateInfo
  {
    Class c1;
    Class c2;
    .NamingPolicy namingPolicy;
    .GeneratorStrategy strategy;
    boolean attemptLoad;
    
    public CreateInfo(Class paramClass1, Class paramClass2)
    {
      this.c1 = paramClass1;
      this.c2 = paramClass2;
      .AbstractClassGenerator localAbstractClassGenerator = .AbstractClassGenerator.getCurrent();
      if (localAbstractClassGenerator != null)
      {
        this.namingPolicy = localAbstractClassGenerator.getNamingPolicy();
        this.strategy = localAbstractClassGenerator.getStrategy();
        this.attemptLoad = localAbstractClassGenerator.getAttemptLoad();
      }
    }
  }
  
  private static class FastClassInfo
  {
    .FastClass f1;
    .FastClass f2;
    int i1;
    int i2;
    
    private FastClassInfo() {}
    
    FastClassInfo(.MethodProxy.1 param1)
    {
      this();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\proxy\$MethodProxy.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */