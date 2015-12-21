package com.google.inject.internal;

import com.google.common.collect.Lists;
import com.google.inject.internal.cglib.proxy..MethodInterceptor;
import com.google.inject.internal.cglib.proxy..MethodProxy;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

final class InterceptorStackCallback
  implements .MethodInterceptor
{
  private static final Set AOP_INTERNAL_CLASSES = new HashSet(Arrays.asList(new String[] { InterceptorStackCallback.class.getName(), InterceptedMethodInvocation.class.getName(), .MethodProxy.class.getName() }));
  final MethodInterceptor[] interceptors;
  final Method method;
  
  public InterceptorStackCallback(Method paramMethod, List paramList)
  {
    this.method = paramMethod;
    this.interceptors = ((MethodInterceptor[])paramList.toArray(new MethodInterceptor[paramList.size()]));
  }
  
  public Object intercept(Object paramObject, Method paramMethod, Object[] paramArrayOfObject, .MethodProxy paramMethodProxy)
    throws Throwable
  {
    return new InterceptedMethodInvocation(paramObject, paramMethodProxy, paramArrayOfObject, 0).proceed();
  }
  
  private void pruneStacktrace(Throwable paramThrowable)
  {
    for (Throwable localThrowable = paramThrowable; localThrowable != null; localThrowable = localThrowable.getCause())
    {
      StackTraceElement[] arrayOfStackTraceElement1 = localThrowable.getStackTrace();
      ArrayList localArrayList = Lists.newArrayList();
      for (StackTraceElement localStackTraceElement : arrayOfStackTraceElement1)
      {
        String str = localStackTraceElement.getClassName();
        if ((!AOP_INTERNAL_CLASSES.contains(str)) && (!str.contains("$EnhancerByGuice$"))) {
          localArrayList.add(localStackTraceElement);
        }
      }
      localThrowable.setStackTrace((StackTraceElement[])localArrayList.toArray(new StackTraceElement[localArrayList.size()]));
    }
  }
  
  private class InterceptedMethodInvocation
    implements MethodInvocation
  {
    final Object proxy;
    final Object[] arguments;
    final .MethodProxy methodProxy;
    final int index;
    
    public InterceptedMethodInvocation(Object paramObject, .MethodProxy paramMethodProxy, Object[] paramArrayOfObject, int paramInt)
    {
      this.proxy = paramObject;
      this.methodProxy = paramMethodProxy;
      this.arguments = paramArrayOfObject;
      this.index = paramInt;
    }
    
    public Object proceed()
      throws Throwable
    {
      try
      {
        return this.index == InterceptorStackCallback.this.interceptors.length ? this.methodProxy.invokeSuper(this.proxy, this.arguments) : InterceptorStackCallback.this.interceptors[this.index].invoke(new InterceptedMethodInvocation(InterceptorStackCallback.this, this.proxy, this.methodProxy, this.arguments, this.index + 1));
      }
      catch (Throwable localThrowable)
      {
        InterceptorStackCallback.this.pruneStacktrace(localThrowable);
        throw localThrowable;
      }
    }
    
    public Method getMethod()
    {
      return InterceptorStackCallback.this.method;
    }
    
    public Object[] getArguments()
    {
      return this.arguments;
    }
    
    public Object getThis()
    {
      return this.proxy;
    }
    
    public AccessibleObject getStaticPart()
    {
      return getMethod();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InterceptorStackCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */