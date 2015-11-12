package com.google.inject.internal;

import com.google.inject.spi.Dependency;

abstract interface InternalFactory
{
  public abstract Object get(Errors paramErrors, InternalContext paramInternalContext, Dependency paramDependency, boolean paramBoolean)
    throws ErrorsException;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\InternalFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */