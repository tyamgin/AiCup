package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Binder;
import com.google.inject.ConfigurationException;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.internal.util.Classes;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

final class ConstructorBindingImpl
  extends BindingImpl
  implements DelayedInitialize, ConstructorBinding
{
  private final Factory factory;
  private final InjectionPoint constructorInjectionPoint;
  
  private ConstructorBindingImpl(InjectorImpl paramInjectorImpl, Key paramKey, Object paramObject, InternalFactory paramInternalFactory, Scoping paramScoping, Factory paramFactory, InjectionPoint paramInjectionPoint)
  {
    super(paramInjectorImpl, paramKey, paramObject, paramInternalFactory, paramScoping);
    this.factory = paramFactory;
    this.constructorInjectionPoint = paramInjectionPoint;
  }
  
  public ConstructorBindingImpl(Key paramKey, Object paramObject, Scoping paramScoping, InjectionPoint paramInjectionPoint, Set paramSet)
  {
    super(paramObject, paramKey, paramScoping);
    this.factory = new Factory(false, paramKey);
    ConstructionProxy localConstructionProxy = new DefaultConstructionProxyFactory(paramInjectionPoint).create();
    this.constructorInjectionPoint = paramInjectionPoint;
    this.factory.constructorInjector = new ConstructorInjector(paramSet, localConstructionProxy, null, null);
  }
  
  static ConstructorBindingImpl create(InjectorImpl paramInjectorImpl, Key paramKey, InjectionPoint paramInjectionPoint, Object paramObject, Scoping paramScoping, Errors paramErrors, boolean paramBoolean1, boolean paramBoolean2)
    throws ErrorsException
  {
    int i = paramErrors.size();
    Class localClass = paramInjectionPoint == null ? paramKey.getTypeLiteral().getRawType() : paramInjectionPoint.getDeclaringType().getRawType();
    if (Modifier.isAbstract(localClass.getModifiers())) {
      paramErrors.missingImplementation(paramKey);
    }
    if (Classes.isInnerClass(localClass)) {
      paramErrors.cannotInjectInnerClass(localClass);
    }
    paramErrors.throwIfNewErrors(i);
    if (paramInjectionPoint == null) {
      try
      {
        paramInjectionPoint = InjectionPoint.forConstructorOf(paramKey.getTypeLiteral());
        if ((paramBoolean2) && (!hasAtInject((Constructor)paramInjectionPoint.getMember()))) {
          paramErrors.atInjectRequired(localClass);
        }
      }
      catch (ConfigurationException localConfigurationException)
      {
        throw paramErrors.merge(localConfigurationException.getErrorMessages()).toException();
      }
    }
    if (!paramScoping.isExplicitlyScoped())
    {
      localObject1 = paramInjectionPoint.getMember().getDeclaringClass();
      localObject2 = Annotations.findScopeAnnotation(paramErrors, (Class)localObject1);
      if (localObject2 != null) {
        paramScoping = Scoping.makeInjectable(Scoping.forAnnotation((Class)localObject2), paramInjectorImpl, paramErrors.withSource(localClass));
      }
    }
    paramErrors.throwIfNewErrors(i);
    Object localObject1 = new Factory(paramBoolean1, paramKey);
    Object localObject2 = Scoping.scope(paramKey, paramInjectorImpl, (InternalFactory)localObject1, paramObject, paramScoping);
    return new ConstructorBindingImpl(paramInjectorImpl, paramKey, paramObject, (InternalFactory)localObject2, paramScoping, (Factory)localObject1, paramInjectionPoint);
  }
  
  private static boolean hasAtInject(Constructor paramConstructor)
  {
    return (paramConstructor.isAnnotationPresent(com.google.inject.Inject.class)) || (paramConstructor.isAnnotationPresent(javax.inject.Inject.class));
  }
  
  public void initialize(InjectorImpl paramInjectorImpl, Errors paramErrors)
    throws ErrorsException
  {
    this.factory.constructorInjector = paramInjectorImpl.constructors.get(this.constructorInjectionPoint, paramErrors);
    this.factory.provisionCallback = paramInjectorImpl.provisionListenerStore.get(this);
  }
  
  boolean isInitialized()
  {
    return this.factory.constructorInjector != null;
  }
  
  InjectionPoint getInternalConstructor()
  {
    if (this.factory.constructorInjector != null) {
      return this.factory.constructorInjector.getConstructionProxy().getInjectionPoint();
    }
    return this.constructorInjectionPoint;
  }
  
  Set getInternalDependencies()
  {
    ImmutableSet.Builder localBuilder = ImmutableSet.builder();
    if (this.factory.constructorInjector == null)
    {
      localBuilder.add(this.constructorInjectionPoint);
      try
      {
        localBuilder.addAll(InjectionPoint.forInstanceMethodsAndFields(this.constructorInjectionPoint.getDeclaringType()));
      }
      catch (ConfigurationException localConfigurationException) {}
    }
    else
    {
      localBuilder.add(getConstructor()).addAll(getInjectableMembers());
    }
    return Dependency.forInjectionPoints(localBuilder.build());
  }
  
  public Object acceptTargetVisitor(BindingTargetVisitor paramBindingTargetVisitor)
  {
    Preconditions.checkState(this.factory.constructorInjector != null, "not initialized");
    return paramBindingTargetVisitor.visit(this);
  }
  
  public InjectionPoint getConstructor()
  {
    Preconditions.checkState(this.factory.constructorInjector != null, "Binding is not ready");
    return this.factory.constructorInjector.getConstructionProxy().getInjectionPoint();
  }
  
  public Set getInjectableMembers()
  {
    Preconditions.checkState(this.factory.constructorInjector != null, "Binding is not ready");
    return this.factory.constructorInjector.getInjectableMembers();
  }
  
  public Map getMethodInterceptors()
  {
    Preconditions.checkState(this.factory.constructorInjector != null, "Binding is not ready");
    return this.factory.constructorInjector.getConstructionProxy().getMethodInterceptors();
  }
  
  public Set getDependencies()
  {
    return Dependency.forInjectionPoints(new ImmutableSet.Builder().add(getConstructor()).addAll(getInjectableMembers()).build());
  }
  
  protected BindingImpl withScoping(Scoping paramScoping)
  {
    return new ConstructorBindingImpl(null, getKey(), getSource(), this.factory, paramScoping, this.factory, this.constructorInjectionPoint);
  }
  
  protected BindingImpl withKey(Key paramKey)
  {
    return new ConstructorBindingImpl(null, paramKey, getSource(), this.factory, getScoping(), this.factory, this.constructorInjectionPoint);
  }
  
  public void applyTo(Binder paramBinder)
  {
    InjectionPoint localInjectionPoint = getConstructor();
    getScoping().applyTo(paramBinder.withSource(getSource()).bind(getKey()).toConstructor((Constructor)getConstructor().getMember(), localInjectionPoint.getDeclaringType()));
  }
  
  public String toString()
  {
    return Objects.toStringHelper(ConstructorBinding.class).add("key", getKey()).add("source", getSource()).add("scope", getScoping()).toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ConstructorBindingImpl))
    {
      ConstructorBindingImpl localConstructorBindingImpl = (ConstructorBindingImpl)paramObject;
      return (getKey().equals(localConstructorBindingImpl.getKey())) && (getScoping().equals(localConstructorBindingImpl.getScoping())) && (Objects.equal(this.constructorInjectionPoint, localConstructorBindingImpl.constructorInjectionPoint));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { getKey(), getScoping(), this.constructorInjectionPoint });
  }
  
  private static class Factory
    implements InternalFactory
  {
    private final boolean failIfNotLinked;
    private final Key key;
    private ConstructorInjector constructorInjector;
    private ProvisionListenerStackCallback provisionCallback;
    
    Factory(boolean paramBoolean, Key paramKey)
    {
      this.failIfNotLinked = paramBoolean;
      this.key = paramKey;
    }
    
    public Object get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
      throws ErrorsException
    {
      Preconditions.checkState(this.constructorInjector != null, "Constructor not ready");
      if ((this.failIfNotLinked) && (!paramBoolean)) {
        throw paramErrors.jitDisabled(this.key).toException();
      }
      return this.constructorInjector.construct(paramErrors, paramInternalContext, paramDependency.getKey().getTypeLiteral().getRawType(), this.provisionCallback);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ConstructorBindingImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */