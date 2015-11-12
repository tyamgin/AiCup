package com.google.inject.internal;

import com.google.common.collect.ImmutableMap;
import com.google.inject.internal.cglib.core..CodeGenerationException;
import com.google.inject.internal.cglib.reflect..FastClass;
import com.google.inject.internal.cglib.reflect..FastConstructor;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

final class DefaultConstructionProxyFactory
  implements ConstructionProxyFactory
{
  private final InjectionPoint injectionPoint;
  
  DefaultConstructionProxyFactory(InjectionPoint paramInjectionPoint)
  {
    this.injectionPoint = paramInjectionPoint;
  }
  
  public ConstructionProxy create()
  {
    final Constructor localConstructor = (Constructor)this.injectionPoint.getMember();
    if (Modifier.isPublic(localConstructor.getModifiers()))
    {
      Class localClass = localConstructor.getDeclaringClass();
      try
      {
        final .FastConstructor localFastConstructor = BytecodeGen.newFastClass(localClass, BytecodeGen.Visibility.forMember(localConstructor)).getConstructor(localConstructor);
        new ConstructionProxy()
        {
          public Object newInstance(Object... paramAnonymousVarArgs)
            throws InvocationTargetException
          {
            return localFastConstructor.newInstance(paramAnonymousVarArgs);
          }
          
          public InjectionPoint getInjectionPoint()
          {
            return DefaultConstructionProxyFactory.this.injectionPoint;
          }
          
          public Constructor getConstructor()
          {
            return localConstructor;
          }
          
          public ImmutableMap getMethodInterceptors()
          {
            return ImmutableMap.of();
          }
        };
      }
      catch (.CodeGenerationException localCodeGenerationException)
      {
        if (!Modifier.isPublic(localClass.getModifiers())) {
          localConstructor.setAccessible(true);
        }
      }
    }
    else
    {
      localConstructor.setAccessible(true);
    }
    new ConstructionProxy()
    {
      public Object newInstance(Object... paramAnonymousVarArgs)
        throws InvocationTargetException
      {
        try
        {
          return localConstructor.newInstance(paramAnonymousVarArgs);
        }
        catch (InstantiationException localInstantiationException)
        {
          throw new AssertionError(localInstantiationException);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new AssertionError(localIllegalAccessException);
        }
      }
      
      public InjectionPoint getInjectionPoint()
      {
        return DefaultConstructionProxyFactory.this.injectionPoint;
      }
      
      public Constructor getConstructor()
      {
        return localConstructor;
      }
      
      public ImmutableMap getMethodInterceptors()
      {
        return ImmutableMap.of();
      }
    };
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\DefaultConstructionProxyFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */