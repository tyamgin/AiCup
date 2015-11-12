package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.PrivateElements;
import java.util.Set;

public final class ExposedBindingImpl
  extends BindingImpl
  implements ExposedBinding
{
  private final PrivateElements privateElements;
  
  public ExposedBindingImpl(InjectorImpl paramInjectorImpl, Object paramObject, Key paramKey, InternalFactory paramInternalFactory, PrivateElements paramPrivateElements)
  {
    super(paramInjectorImpl, paramKey, paramObject, paramInternalFactory, Scoping.UNSCOPED);
    this.privateElements = paramPrivateElements;
  }
  
  public Object acceptTargetVisitor(BindingTargetVisitor paramBindingTargetVisitor)
  {
    return paramBindingTargetVisitor.visit(this);
  }
  
  public Set getDependencies()
  {
    return ImmutableSet.of(Dependency.get(Key.get(Injector.class)));
  }
  
  public PrivateElements getPrivateElements()
  {
    return this.privateElements;
  }
  
  public String toString()
  {
    return Objects.toStringHelper(ExposedBinding.class).add("key", getKey()).add("source", getSource()).add("privateElements", this.privateElements).toString();
  }
  
  public void applyTo(Binder paramBinder)
  {
    throw new UnsupportedOperationException("This element represents a synthetic binding.");
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ExposedBindingImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */