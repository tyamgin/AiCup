package com.google.inject.internal;

import com.google.inject.spi.Dependency;

final class SingleParameterInjector
{
  private static final Object[] NO_ARGUMENTS = new Object[0];
  private final Dependency dependency;
  private final BindingImpl binding;
  
  SingleParameterInjector(Dependency paramDependency, BindingImpl paramBindingImpl)
  {
    this.dependency = paramDependency;
    this.binding = paramBindingImpl;
  }
  
  private Object inject(Errors paramErrors, InternalContext paramInternalContext)
    throws ErrorsException
  {
    Dependency localDependency = paramInternalContext.pushDependency(this.dependency, this.binding.getSource());
    try
    {
      Object localObject1 = this.binding.getInternalFactory().get(paramErrors.withSource(this.dependency), paramInternalContext, this.dependency, false);
      return localObject1;
    }
    finally
    {
      paramInternalContext.popStateAndSetDependency(localDependency);
    }
  }
  
  static Object[] getAll(Errors paramErrors, InternalContext paramInternalContext, SingleParameterInjector[] paramArrayOfSingleParameterInjector)
    throws ErrorsException
  {
    if (paramArrayOfSingleParameterInjector == null) {
      return NO_ARGUMENTS;
    }
    int i = paramErrors.size();
    int j = paramArrayOfSingleParameterInjector.length;
    Object[] arrayOfObject = new Object[j];
    for (int k = 0; k < j; k++)
    {
      SingleParameterInjector localSingleParameterInjector = paramArrayOfSingleParameterInjector[k];
      try
      {
        arrayOfObject[k] = localSingleParameterInjector.inject(paramErrors, paramInternalContext);
      }
      catch (ErrorsException localErrorsException)
      {
        paramErrors.merge(localErrorsException.getErrors());
      }
    }
    paramErrors.throwIfNewErrors(i);
    return arrayOfObject;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\SingleParameterInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */