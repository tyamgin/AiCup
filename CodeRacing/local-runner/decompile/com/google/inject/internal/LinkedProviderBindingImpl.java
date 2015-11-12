package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.ProviderKeyBinding;
import java.util.Set;

final class LinkedProviderBindingImpl
  extends BindingImpl
  implements DelayedInitialize, HasDependencies, ProviderKeyBinding
{
  final Key providerKey;
  final DelayedInitialize delayedInitializer;
  
  private LinkedProviderBindingImpl(InjectorImpl paramInjectorImpl, Key paramKey1, Object paramObject, InternalFactory paramInternalFactory, Scoping paramScoping, Key paramKey2, DelayedInitialize paramDelayedInitialize)
  {
    super(paramInjectorImpl, paramKey1, paramObject, paramInternalFactory, paramScoping);
    this.providerKey = paramKey2;
    this.delayedInitializer = paramDelayedInitialize;
  }
  
  public LinkedProviderBindingImpl(InjectorImpl paramInjectorImpl, Key paramKey1, Object paramObject, InternalFactory paramInternalFactory, Scoping paramScoping, Key paramKey2)
  {
    this(paramInjectorImpl, paramKey1, paramObject, paramInternalFactory, paramScoping, paramKey2, null);
  }
  
  LinkedProviderBindingImpl(Object paramObject, Key paramKey1, Scoping paramScoping, Key paramKey2)
  {
    super(paramObject, paramKey1, paramScoping);
    this.providerKey = paramKey2;
    this.delayedInitializer = null;
  }
  
  static LinkedProviderBindingImpl createWithInitializer(InjectorImpl paramInjectorImpl, Key paramKey1, Object paramObject, InternalFactory paramInternalFactory, Scoping paramScoping, Key paramKey2, DelayedInitialize paramDelayedInitialize)
  {
    return new LinkedProviderBindingImpl(paramInjectorImpl, paramKey1, paramObject, paramInternalFactory, paramScoping, paramKey2, paramDelayedInitialize);
  }
  
  public Object acceptTargetVisitor(BindingTargetVisitor paramBindingTargetVisitor)
  {
    return paramBindingTargetVisitor.visit(this);
  }
  
  public Key getProviderKey()
  {
    return this.providerKey;
  }
  
  public void initialize(InjectorImpl paramInjectorImpl, Errors paramErrors)
    throws ErrorsException
  {
    if (this.delayedInitializer != null) {
      this.delayedInitializer.initialize(paramInjectorImpl, paramErrors);
    }
  }
  
  public Set getDependencies()
  {
    return ImmutableSet.of(Dependency.get(this.providerKey));
  }
  
  public BindingImpl withScoping(Scoping paramScoping)
  {
    return new LinkedProviderBindingImpl(getSource(), getKey(), paramScoping, this.providerKey);
  }
  
  public BindingImpl withKey(Key paramKey)
  {
    return new LinkedProviderBindingImpl(getSource(), paramKey, getScoping(), this.providerKey);
  }
  
  public void applyTo(Binder paramBinder)
  {
    getScoping().applyTo(paramBinder.withSource(getSource()).bind(getKey()).toProvider(getProviderKey()));
  }
  
  public String toString()
  {
    return Objects.toStringHelper(ProviderKeyBinding.class).add("key", getKey()).add("source", getSource()).add("scope", getScoping()).add("provider", this.providerKey).toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof LinkedProviderBindingImpl))
    {
      LinkedProviderBindingImpl localLinkedProviderBindingImpl = (LinkedProviderBindingImpl)paramObject;
      return (getKey().equals(localLinkedProviderBindingImpl.getKey())) && (getScoping().equals(localLinkedProviderBindingImpl.getScoping())) && (Objects.equal(this.providerKey, localLinkedProviderBindingImpl.providerKey));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { getKey(), getScoping(), this.providerKey });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\LinkedProviderBindingImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */