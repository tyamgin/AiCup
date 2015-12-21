package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.internal.cglib.core..MethodWrapper;
import com.google.inject.internal.cglib.proxy..Callback;
import com.google.inject.internal.cglib.proxy..CallbackFilter;
import com.google.inject.internal.cglib.proxy..Enhancer;
import com.google.inject.internal.cglib.proxy..MethodInterceptor;
import com.google.inject.internal.cglib.proxy..NoOp;
import com.google.inject.internal.cglib.reflect..FastClass;
import com.google.inject.internal.cglib.reflect..FastConstructor;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

final class ProxyFactory
  implements ConstructionProxyFactory
{
  private static final Logger logger = Logger.getLogger(ProxyFactory.class.getName());
  private final InjectionPoint injectionPoint;
  private final ImmutableMap interceptors;
  private final Class declaringClass;
  private final List methods;
  private final .Callback[] callbacks;
  private BytecodeGen.Visibility visibility = BytecodeGen.Visibility.PUBLIC;
  
  ProxyFactory(InjectionPoint paramInjectionPoint, Iterable paramIterable)
  {
    this.injectionPoint = paramInjectionPoint;
    Constructor localConstructor = (Constructor)paramInjectionPoint.getMember();
    this.declaringClass = localConstructor.getDeclaringClass();
    ArrayList localArrayList = Lists.newArrayList();
    Object localObject1 = paramIterable.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (MethodAspect)((Iterator)localObject1).next();
      if (((MethodAspect)localObject2).matches(this.declaringClass)) {
        localArrayList.add(localObject2);
      }
    }
    if (localArrayList.isEmpty())
    {
      this.interceptors = ImmutableMap.of();
      this.methods = ImmutableList.of();
      this.callbacks = null;
      return;
    }
    this.methods = Lists.newArrayList();
    .Enhancer.getMethods(this.declaringClass, null, this.methods);
    localObject1 = Lists.newArrayList();
    Object localObject2 = this.methods.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (Method)((Iterator)localObject2).next();
      ((List)localObject1).add(new MethodInterceptorsPair((Method)localObject3));
    }
    int i = 0;
    Object localObject3 = localArrayList.iterator();
    Object localObject4;
    Object localObject5;
    while (((Iterator)localObject3).hasNext())
    {
      MethodAspect localMethodAspect = (MethodAspect)((Iterator)localObject3).next();
      localObject4 = ((List)localObject1).iterator();
      while (((Iterator)localObject4).hasNext())
      {
        localObject5 = (MethodInterceptorsPair)((Iterator)localObject4).next();
        if (localMethodAspect.matches(((MethodInterceptorsPair)localObject5).method))
        {
          if (((MethodInterceptorsPair)localObject5).method.isSynthetic()) {
            logger.log(Level.WARNING, "Method [{0}] is synthetic and is being intercepted by {1}. This could indicate a bug.  The method may be intercepted twice, or may not be intercepted at all.", new Object[] { ((MethodInterceptorsPair)localObject5).method, localMethodAspect.interceptors() });
          }
          this.visibility = this.visibility.and(BytecodeGen.Visibility.forMember(((MethodInterceptorsPair)localObject5).method));
          ((MethodInterceptorsPair)localObject5).addAll(localMethodAspect.interceptors());
          i = 1;
        }
      }
    }
    if (i == 0)
    {
      this.interceptors = ImmutableMap.of();
      this.callbacks = null;
      return;
    }
    localObject3 = null;
    this.callbacks = new .Callback[this.methods.size()];
    for (int j = 0; j < this.methods.size(); j++)
    {
      localObject4 = (MethodInterceptorsPair)((List)localObject1).get(j);
      if (!((MethodInterceptorsPair)localObject4).hasInterceptors())
      {
        this.callbacks[j] = .NoOp.INSTANCE;
      }
      else
      {
        if (localObject3 == null) {
          localObject3 = ImmutableMap.builder();
        }
        localObject5 = ImmutableSet.copyOf(((MethodInterceptorsPair)localObject4).interceptors).asList();
        ((ImmutableMap.Builder)localObject3).put(((MethodInterceptorsPair)localObject4).method, localObject5);
        this.callbacks[j] = new InterceptorStackCallback(((MethodInterceptorsPair)localObject4).method, (List)localObject5);
      }
    }
    this.interceptors = (localObject3 != null ? ((ImmutableMap.Builder)localObject3).build() : ImmutableMap.of());
  }
  
  public ImmutableMap getInterceptors()
  {
    return this.interceptors;
  }
  
  public ConstructionProxy create()
    throws ErrorsException
  {
    if (this.interceptors.isEmpty()) {
      return new DefaultConstructionProxyFactory(this.injectionPoint).create();
    }
    Class[] arrayOfClass = new Class[this.callbacks.length];
    for (int i = 0; i < this.callbacks.length; i++) {
      if (this.callbacks[i] == .NoOp.INSTANCE) {
        arrayOfClass[i] = .NoOp.class;
      } else {
        arrayOfClass[i] = .MethodInterceptor.class;
      }
    }
    try
    {
      .Enhancer localEnhancer = BytecodeGen.newEnhancer(this.declaringClass, this.visibility);
      localEnhancer.setCallbackFilter(new IndicesCallbackFilter(this.methods));
      localEnhancer.setCallbackTypes(arrayOfClass);
      return new ProxyConstructor(localEnhancer, this.injectionPoint, this.callbacks, this.interceptors);
    }
    catch (Throwable localThrowable)
    {
      throw new Errors().errorEnhancingClass(this.declaringClass, localThrowable).toException();
    }
  }
  
  private static class ProxyConstructor
    implements ConstructionProxy
  {
    final Class enhanced;
    final InjectionPoint injectionPoint;
    final Constructor constructor;
    final .Callback[] callbacks;
    final .FastConstructor fastConstructor;
    final ImmutableMap methodInterceptors;
    
    ProxyConstructor(.Enhancer paramEnhancer, InjectionPoint paramInjectionPoint, .Callback[] paramArrayOfCallback, ImmutableMap paramImmutableMap)
    {
      this.enhanced = paramEnhancer.createClass();
      this.injectionPoint = paramInjectionPoint;
      this.constructor = ((Constructor)paramInjectionPoint.getMember());
      this.callbacks = paramArrayOfCallback;
      this.methodInterceptors = paramImmutableMap;
      .FastClass localFastClass = BytecodeGen.newFastClass(this.enhanced, BytecodeGen.Visibility.forMember(this.constructor));
      this.fastConstructor = localFastClass.getConstructor(this.constructor.getParameterTypes());
    }
    
    public Object newInstance(Object... paramVarArgs)
      throws InvocationTargetException
    {
      .Enhancer.registerCallbacks(this.enhanced, this.callbacks);
      try
      {
        Object localObject1 = this.fastConstructor.newInstance(paramVarArgs);
        return localObject1;
      }
      finally
      {
        .Enhancer.registerCallbacks(this.enhanced, null);
      }
    }
    
    public InjectionPoint getInjectionPoint()
    {
      return this.injectionPoint;
    }
    
    public Constructor getConstructor()
    {
      return this.constructor;
    }
    
    public ImmutableMap getMethodInterceptors()
    {
      return this.methodInterceptors;
    }
  }
  
  private static class IndicesCallbackFilter
    implements .CallbackFilter
  {
    final Map indices;
    final int hashCode;
    
    IndicesCallbackFilter(List paramList)
    {
      HashMap localHashMap = Maps.newHashMap();
      for (int i = 0; i < paramList.size(); i++) {
        localHashMap.put(.MethodWrapper.create((Method)paramList.get(i)), Integer.valueOf(i));
      }
      this.indices = localHashMap;
      this.hashCode = localHashMap.hashCode();
    }
    
    public int accept(Method paramMethod)
    {
      return ((Integer)this.indices.get(.MethodWrapper.create(paramMethod))).intValue();
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof IndicesCallbackFilter)) && (((IndicesCallbackFilter)paramObject).indices.equals(this.indices));
    }
    
    public int hashCode()
    {
      return this.hashCode;
    }
  }
  
  private static class MethodInterceptorsPair
  {
    final Method method;
    List interceptors;
    
    MethodInterceptorsPair(Method paramMethod)
    {
      this.method = paramMethod;
    }
    
    void addAll(List paramList)
    {
      if (this.interceptors == null) {
        this.interceptors = Lists.newArrayList();
      }
      this.interceptors.addAll(paramList);
    }
    
    boolean hasInterceptors()
    {
      return this.interceptors != null;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ProxyFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */