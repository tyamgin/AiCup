package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.inject.Scope;
import com.google.inject.spi.ScopeBinding;

final class ScopeBindingProcessor
  extends AbstractProcessor
{
  ScopeBindingProcessor(Errors paramErrors)
  {
    super(paramErrors);
  }
  
  public Boolean visit(ScopeBinding paramScopeBinding)
  {
    Scope localScope = (Scope)Preconditions.checkNotNull(paramScopeBinding.getScope(), "scope");
    Class localClass = (Class)Preconditions.checkNotNull(paramScopeBinding.getAnnotationType(), "annotation type");
    if (!Annotations.isScopeAnnotation(localClass)) {
      this.errors.missingScopeAnnotation(localClass);
    }
    if (!Annotations.isRetainedAtRuntime(localClass)) {
      this.errors.missingRuntimeRetention(localClass);
    }
    ScopeBinding localScopeBinding = this.injector.state.getScopeBinding(localClass);
    if (localScopeBinding != null)
    {
      if (!localScope.equals(localScopeBinding.getScope())) {
        this.errors.duplicateScopes(localScopeBinding, localClass, localScope);
      }
    }
    else {
      this.injector.state.putScopeBinding(localClass, paramScopeBinding);
    }
    return Boolean.valueOf(true);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ScopeBindingProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */