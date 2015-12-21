package com.google.inject.internal;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.InstanceBinding;

public abstract class BindingImpl
  implements Binding
{
  private final InjectorImpl injector;
  private final Key key;
  private final Object source;
  private final Scoping scoping;
  private final InternalFactory internalFactory;
  private volatile Provider provider;
  
  public BindingImpl(InjectorImpl paramInjectorImpl, Key paramKey, Object paramObject, InternalFactory paramInternalFactory, Scoping paramScoping)
  {
    this.injector = paramInjectorImpl;
    this.key = paramKey;
    this.source = paramObject;
    this.internalFactory = paramInternalFactory;
    this.scoping = paramScoping;
  }
  
  protected BindingImpl(Object paramObject, Key paramKey, Scoping paramScoping)
  {
    this.internalFactory = null;
    this.injector = null;
    this.source = paramObject;
    this.key = paramKey;
    this.scoping = paramScoping;
  }
  
  public Key getKey()
  {
    return this.key;
  }
  
  public Object getSource()
  {
    return this.source;
  }
  
  public Provider getProvider()
  {
    if (this.provider == null)
    {
      if (this.injector == null) {
        throw new UnsupportedOperationException("getProvider() not supported for module bindings");
      }
      this.provider = this.injector.getProvider(this.key);
    }
    return this.provider;
  }
  
  public InternalFactory getInternalFactory()
  {
    return this.internalFactory;
  }
  
  public Scoping getScoping()
  {
    return this.scoping;
  }
  
  public boolean isConstant()
  {
    return this instanceof InstanceBinding;
  }
  
  public Object acceptVisitor(ElementVisitor paramElementVisitor)
  {
    return paramElementVisitor.visit(this);
  }
  
  public Object acceptScopingVisitor(BindingScopingVisitor paramBindingScopingVisitor)
  {
    return this.scoping.acceptVisitor(paramBindingScopingVisitor);
  }
  
  protected BindingImpl withScoping(Scoping paramScoping)
  {
    throw new AssertionError();
  }
  
  protected BindingImpl withKey(Key paramKey)
  {
    throw new AssertionError();
  }
  
  public String toString()
  {
    return Objects.toStringHelper(Binding.class).add("key", this.key).add("scope", this.scoping).add("source", this.source).toString();
  }
  
  public InjectorImpl getInjector()
  {
    return this.injector;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\BindingImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */