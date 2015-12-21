package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Exposed;
import com.google.inject.Key;
import com.google.inject.PrivateBinder;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.internal.asm..Type;
import com.google.inject.internal.cglib.core..CodeGenerationException;
import com.google.inject.internal.cglib.core..Signature;
import com.google.inject.internal.cglib.reflect..FastClass;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderWithExtensionVisitor;
import com.google.inject.spi.ProvidesMethodBinding;
import com.google.inject.spi.ProvidesMethodTargetVisitor;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

public abstract class ProviderMethod
  implements HasDependencies, ProviderWithExtensionVisitor, ProvidesMethodBinding
{
  protected final Object instance;
  protected final Method method;
  private final Key key;
  private final Class scopeAnnotation;
  private final ImmutableSet dependencies;
  private final List parameterProviders;
  private final boolean exposed;
  private final Annotation annotation;
  
  static ProviderMethod create(Key paramKey, Method paramMethod, Object paramObject, ImmutableSet paramImmutableSet, List paramList, Class paramClass, boolean paramBoolean, Annotation paramAnnotation)
  {
    int i = paramMethod.getModifiers();
    if ((!paramBoolean) && (!Modifier.isPrivate(i)) && (!Modifier.isProtected(i))) {
      try
      {
        return new FastClassProviderMethod(paramKey, paramMethod, paramObject, paramImmutableSet, paramList, paramClass, paramAnnotation);
      }
      catch (.CodeGenerationException localCodeGenerationException) {}
    }
    if ((!Modifier.isPublic(i)) || (!Modifier.isPublic(paramMethod.getDeclaringClass().getModifiers()))) {
      paramMethod.setAccessible(true);
    }
    return new ReflectionProviderMethod(paramKey, paramMethod, paramObject, paramImmutableSet, paramList, paramClass, paramAnnotation);
  }
  
  private ProviderMethod(Key paramKey, Method paramMethod, Object paramObject, ImmutableSet paramImmutableSet, List paramList, Class paramClass, Annotation paramAnnotation)
  {
    this.key = paramKey;
    this.scopeAnnotation = paramClass;
    this.instance = paramObject;
    this.dependencies = paramImmutableSet;
    this.method = paramMethod;
    this.parameterProviders = paramList;
    this.exposed = paramMethod.isAnnotationPresent(Exposed.class);
    this.annotation = paramAnnotation;
  }
  
  public Key getKey()
  {
    return this.key;
  }
  
  public Method getMethod()
  {
    return this.method;
  }
  
  public Object getInstance()
  {
    return this.instance;
  }
  
  public Object getEnclosingInstance()
  {
    return this.instance;
  }
  
  public Annotation getAnnotation()
  {
    return this.annotation;
  }
  
  public void configure(Binder paramBinder)
  {
    paramBinder = paramBinder.withSource(this.method);
    if (this.scopeAnnotation != null) {
      paramBinder.bind(this.key).toProvider(this).in(this.scopeAnnotation);
    } else {
      paramBinder.bind(this.key).toProvider(this);
    }
    if (this.exposed) {
      ((PrivateBinder)paramBinder).expose(this.key);
    }
  }
  
  public Object get()
  {
    Object[] arrayOfObject = new Object[this.parameterProviders.size()];
    for (int i = 0; i < arrayOfObject.length; i++) {
      arrayOfObject[i] = ((Provider)this.parameterProviders.get(i)).get();
    }
    try
    {
      Object localObject = doProvision(arrayOfObject);
      return localObject;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new AssertionError(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw Exceptions.rethrowCause(localInvocationTargetException);
    }
  }
  
  abstract Object doProvision(Object[] paramArrayOfObject)
    throws IllegalAccessException, InvocationTargetException;
  
  public Set getDependencies()
  {
    return this.dependencies;
  }
  
  public Object acceptExtensionVisitor(BindingTargetVisitor paramBindingTargetVisitor, ProviderInstanceBinding paramProviderInstanceBinding)
  {
    if ((paramBindingTargetVisitor instanceof ProvidesMethodTargetVisitor)) {
      return ((ProvidesMethodTargetVisitor)paramBindingTargetVisitor).visit(this);
    }
    return paramBindingTargetVisitor.visit(paramProviderInstanceBinding);
  }
  
  public String toString()
  {
    String str1 = this.annotation.toString();
    if (this.annotation.annotationType() == Provides.class) {
      str1 = "@Provides";
    } else if (str1.endsWith("()")) {
      str1 = str1.substring(0, str1.length() - 2);
    }
    String str2 = String.valueOf(String.valueOf(str1));
    String str3 = String.valueOf(String.valueOf(StackTraceElements.forMember(this.method)));
    return 1 + str2.length() + str3.length() + str2 + " " + str3;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ProviderMethod))
    {
      ProviderMethod localProviderMethod = (ProviderMethod)paramObject;
      return (this.method.equals(localProviderMethod.method)) && (this.instance.equals(localProviderMethod.instance)) && (this.annotation.equals(localProviderMethod.annotation));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { this.method, this.annotation });
  }
  
  private static final class ReflectionProviderMethod
    extends ProviderMethod
  {
    ReflectionProviderMethod(Key paramKey, Method paramMethod, Object paramObject, ImmutableSet paramImmutableSet, List paramList, Class paramClass, Annotation paramAnnotation)
    {
      super(paramMethod, paramObject, paramImmutableSet, paramList, paramClass, paramAnnotation, null);
    }
    
    Object doProvision(Object[] paramArrayOfObject)
      throws IllegalAccessException, InvocationTargetException
    {
      return this.method.invoke(this.instance, paramArrayOfObject);
    }
  }
  
  private static final class FastClassProviderMethod
    extends ProviderMethod
  {
    final .FastClass fastClass;
    final int methodIndex;
    
    FastClassProviderMethod(Key paramKey, Method paramMethod, Object paramObject, ImmutableSet paramImmutableSet, List paramList, Class paramClass, Annotation paramAnnotation)
    {
      super(paramMethod, paramObject, paramImmutableSet, paramList, paramClass, paramAnnotation, null);
      this.fastClass = BytecodeGen.newFastClass(paramMethod.getDeclaringClass(), BytecodeGen.Visibility.forMember(paramMethod));
      this.methodIndex = this.fastClass.getIndex(new .Signature(paramMethod.getName(), .Type.getMethodDescriptor(paramMethod)));
      Preconditions.checkArgument(this.methodIndex >= 0, "Could not find method %s in fast class for class %s", new Object[] { paramMethod, paramMethod.getDeclaringClass() });
    }
    
    public Object doProvision(Object[] paramArrayOfObject)
      throws IllegalAccessException, InvocationTargetException
    {
      return this.fastClass.invoke(this.methodIndex, this.instance, paramArrayOfObject);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ProviderMethod.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */