package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

@Beta
public abstract class Invokable
  extends Element
  implements GenericDeclaration
{
  Invokable(AccessibleObject paramAccessibleObject)
  {
    super(paramAccessibleObject);
  }
  
  public static Invokable from(Method paramMethod)
  {
    return new MethodInvokable(paramMethod);
  }
  
  public static Invokable from(Constructor paramConstructor)
  {
    return new ConstructorInvokable(paramConstructor);
  }
  
  public abstract boolean isOverridable();
  
  public abstract boolean isVarArgs();
  
  public final Object invoke(Object paramObject, Object... paramVarArgs)
    throws InvocationTargetException, IllegalAccessException
  {
    return invokeInternal(paramObject, (Object[])Preconditions.checkNotNull(paramVarArgs));
  }
  
  public final TypeToken getReturnType()
  {
    return TypeToken.of(getGenericReturnType());
  }
  
  public final ImmutableList getParameters()
  {
    Type[] arrayOfType = getGenericParameterTypes();
    Annotation[][] arrayOfAnnotation = getParameterAnnotations();
    ImmutableList.Builder localBuilder = ImmutableList.builder();
    for (int i = 0; i < arrayOfType.length; i++) {
      localBuilder.add(new Parameter(this, i, TypeToken.of(arrayOfType[i]), arrayOfAnnotation[i]));
    }
    return localBuilder.build();
  }
  
  public final ImmutableList getExceptionTypes()
  {
    ImmutableList.Builder localBuilder = ImmutableList.builder();
    for (Type localType : getGenericExceptionTypes())
    {
      TypeToken localTypeToken = TypeToken.of(localType);
      localBuilder.add(localTypeToken);
    }
    return localBuilder.build();
  }
  
  public final Invokable returning(Class paramClass)
  {
    return returning(TypeToken.of(paramClass));
  }
  
  public final Invokable returning(TypeToken paramTypeToken)
  {
    if (!paramTypeToken.isAssignableFrom(getReturnType())) {
      throw new IllegalArgumentException("Invokable is known to return " + getReturnType() + ", not " + paramTypeToken);
    }
    Invokable localInvokable = this;
    return localInvokable;
  }
  
  public final Class getDeclaringClass()
  {
    return super.getDeclaringClass();
  }
  
  public TypeToken getOwnerType()
  {
    return TypeToken.of(getDeclaringClass());
  }
  
  abstract Object invokeInternal(Object paramObject, Object[] paramArrayOfObject)
    throws InvocationTargetException, IllegalAccessException;
  
  abstract Type[] getGenericParameterTypes();
  
  abstract Type[] getGenericExceptionTypes();
  
  abstract Annotation[][] getParameterAnnotations();
  
  abstract Type getGenericReturnType();
  
  static class ConstructorInvokable
    extends Invokable
  {
    private final Constructor constructor;
    
    ConstructorInvokable(Constructor paramConstructor)
    {
      super();
      this.constructor = paramConstructor;
    }
    
    final Object invokeInternal(Object paramObject, Object[] paramArrayOfObject)
      throws InvocationTargetException, IllegalAccessException
    {
      try
      {
        return this.constructor.newInstance(paramArrayOfObject);
      }
      catch (InstantiationException localInstantiationException)
      {
        throw new RuntimeException(this.constructor + " failed.", localInstantiationException);
      }
    }
    
    Type getGenericReturnType()
    {
      return this.constructor.getDeclaringClass();
    }
    
    Type[] getGenericParameterTypes()
    {
      Type[] arrayOfType = this.constructor.getGenericParameterTypes();
      Class localClass = this.constructor.getDeclaringClass();
      if ((!Modifier.isStatic(localClass.getModifiers())) && (localClass.getEnclosingClass() != null) && (arrayOfType.length == this.constructor.getParameterTypes().length)) {
        return (Type[])Arrays.copyOfRange(arrayOfType, 1, arrayOfType.length);
      }
      return arrayOfType;
    }
    
    Type[] getGenericExceptionTypes()
    {
      return this.constructor.getGenericExceptionTypes();
    }
    
    final Annotation[][] getParameterAnnotations()
    {
      return this.constructor.getParameterAnnotations();
    }
    
    public final TypeVariable[] getTypeParameters()
    {
      return this.constructor.getTypeParameters();
    }
    
    public final boolean isOverridable()
    {
      return false;
    }
    
    public final boolean isVarArgs()
    {
      return this.constructor.isVarArgs();
    }
  }
  
  static class MethodInvokable
    extends Invokable
  {
    private final Method method;
    
    MethodInvokable(Method paramMethod)
    {
      super();
      this.method = paramMethod;
    }
    
    final Object invokeInternal(Object paramObject, Object[] paramArrayOfObject)
      throws InvocationTargetException, IllegalAccessException
    {
      return this.method.invoke(paramObject, paramArrayOfObject);
    }
    
    Type getGenericReturnType()
    {
      return this.method.getGenericReturnType();
    }
    
    Type[] getGenericParameterTypes()
    {
      return this.method.getGenericParameterTypes();
    }
    
    Type[] getGenericExceptionTypes()
    {
      return this.method.getGenericExceptionTypes();
    }
    
    final Annotation[][] getParameterAnnotations()
    {
      return this.method.getParameterAnnotations();
    }
    
    public final TypeVariable[] getTypeParameters()
    {
      return this.method.getTypeParameters();
    }
    
    public final boolean isOverridable()
    {
      return (!isFinal()) && (!isPrivate()) && (!isStatic()) && (!Modifier.isFinal(getDeclaringClass().getModifiers()));
    }
    
    public final boolean isVarArgs()
    {
      return this.method.isVarArgs();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\Invokable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */