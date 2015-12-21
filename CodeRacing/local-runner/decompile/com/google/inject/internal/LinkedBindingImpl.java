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
import com.google.inject.spi.LinkedKeyBinding;
import java.util.Set;

public final class LinkedBindingImpl
  extends BindingImpl
  implements HasDependencies, LinkedKeyBinding
{
  final Key targetKey;
  
  public LinkedBindingImpl(InjectorImpl paramInjectorImpl, Key paramKey1, Object paramObject, InternalFactory paramInternalFactory, Scoping paramScoping, Key paramKey2)
  {
    super(paramInjectorImpl, paramKey1, paramObject, paramInternalFactory, paramScoping);
    this.targetKey = paramKey2;
  }
  
  public LinkedBindingImpl(Object paramObject, Key paramKey1, Scoping paramScoping, Key paramKey2)
  {
    super(paramObject, paramKey1, paramScoping);
    this.targetKey = paramKey2;
  }
  
  public Object acceptTargetVisitor(BindingTargetVisitor paramBindingTargetVisitor)
  {
    return paramBindingTargetVisitor.visit(this);
  }
  
  public Key getLinkedKey()
  {
    return this.targetKey;
  }
  
  public Set getDependencies()
  {
    return ImmutableSet.of(Dependency.get(this.targetKey));
  }
  
  public BindingImpl withScoping(Scoping paramScoping)
  {
    return new LinkedBindingImpl(getSource(), getKey(), paramScoping, this.targetKey);
  }
  
  public BindingImpl withKey(Key paramKey)
  {
    return new LinkedBindingImpl(getSource(), paramKey, getScoping(), this.targetKey);
  }
  
  public void applyTo(Binder paramBinder)
  {
    getScoping().applyTo(paramBinder.withSource(getSource()).bind(getKey()).to(getLinkedKey()));
  }
  
  public String toString()
  {
    return Objects.toStringHelper(LinkedKeyBinding.class).add("key", getKey()).add("source", getSource()).add("scope", getScoping()).add("target", this.targetKey).toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof LinkedBindingImpl))
    {
      LinkedBindingImpl localLinkedBindingImpl = (LinkedBindingImpl)paramObject;
      return (getKey().equals(localLinkedBindingImpl.getKey())) && (getScoping().equals(localLinkedBindingImpl.getScoping())) && (Objects.equal(this.targetKey, localLinkedBindingImpl.targetKey));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { getKey(), getScoping(), this.targetKey });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\LinkedBindingImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */