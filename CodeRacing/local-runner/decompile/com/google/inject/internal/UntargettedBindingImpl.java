package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.UntargettedBinding;

final class UntargettedBindingImpl
  extends BindingImpl
  implements UntargettedBinding
{
  UntargettedBindingImpl(InjectorImpl paramInjectorImpl, Key paramKey, Object paramObject)
  {
    super(paramInjectorImpl, paramKey, paramObject, new InternalFactory()
    {
      public Object get(Errors paramAnonymousErrors, InternalContext paramAnonymousInternalContext, Dependency paramAnonymousDependency, boolean paramAnonymousBoolean)
      {
        throw new AssertionError();
      }
    }, Scoping.UNSCOPED);
  }
  
  public UntargettedBindingImpl(Object paramObject, Key paramKey, Scoping paramScoping)
  {
    super(paramObject, paramKey, paramScoping);
  }
  
  public Object acceptTargetVisitor(BindingTargetVisitor paramBindingTargetVisitor)
  {
    return paramBindingTargetVisitor.visit(this);
  }
  
  public BindingImpl withScoping(Scoping paramScoping)
  {
    return new UntargettedBindingImpl(getSource(), getKey(), paramScoping);
  }
  
  public BindingImpl withKey(Key paramKey)
  {
    return new UntargettedBindingImpl(getSource(), paramKey, getScoping());
  }
  
  public void applyTo(Binder paramBinder)
  {
    getScoping().applyTo(paramBinder.withSource(getSource()).bind(getKey()));
  }
  
  public String toString()
  {
    return Objects.toStringHelper(UntargettedBinding.class).add("key", getKey()).add("source", getSource()).toString();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof UntargettedBindingImpl))
    {
      UntargettedBindingImpl localUntargettedBindingImpl = (UntargettedBindingImpl)paramObject;
      return (getKey().equals(localUntargettedBindingImpl.getKey())) && (getScoping().equals(localUntargettedBindingImpl.getScoping()));
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { getKey(), getScoping() });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\UntargettedBindingImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */