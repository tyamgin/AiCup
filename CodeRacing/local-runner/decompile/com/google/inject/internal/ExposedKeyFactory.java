package com.google.inject.internal;

import com.google.inject.Key;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.PrivateElements;

final class ExposedKeyFactory
  implements CreationListener, InternalFactory
{
  private final Key key;
  private final PrivateElements privateElements;
  private BindingImpl delegate;
  
  ExposedKeyFactory(Key paramKey, PrivateElements paramPrivateElements)
  {
    this.key = paramKey;
    this.privateElements = paramPrivateElements;
  }
  
  public void notify(Errors paramErrors)
  {
    InjectorImpl localInjectorImpl = (InjectorImpl)this.privateElements.getInjector();
    BindingImpl localBindingImpl = localInjectorImpl.state.getExplicitBinding(this.key);
    if (localBindingImpl.getInternalFactory() == this)
    {
      paramErrors.withSource(localBindingImpl.getSource()).exposedButNotBound(this.key);
      return;
    }
    this.delegate = localBindingImpl;
  }
  
  public Object get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
    throws ErrorsException
  {
    return this.delegate.getInternalFactory().get(paramErrors, paramInternalContext, paramDependency, paramBoolean);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ExposedKeyFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */