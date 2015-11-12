package com.google.inject.internal;

import com.google.inject.Provider;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.ProviderLookup;

final class LookupProcessor
  extends AbstractProcessor
{
  LookupProcessor(Errors paramErrors)
  {
    super(paramErrors);
  }
  
  public Boolean visit(MembersInjectorLookup paramMembersInjectorLookup)
  {
    try
    {
      MembersInjectorImpl localMembersInjectorImpl = this.injector.membersInjectorStore.get(paramMembersInjectorLookup.getType(), this.errors);
      paramMembersInjectorLookup.initializeDelegate(localMembersInjectorImpl);
    }
    catch (ErrorsException localErrorsException)
    {
      this.errors.merge(localErrorsException.getErrors());
    }
    return Boolean.valueOf(true);
  }
  
  public Boolean visit(ProviderLookup paramProviderLookup)
  {
    try
    {
      Provider localProvider = this.injector.getProviderOrThrow(paramProviderLookup.getDependency(), this.errors);
      paramProviderLookup.initializeDelegate(localProvider);
    }
    catch (ErrorsException localErrorsException)
    {
      this.errors.merge(localErrorsException.getErrors());
    }
    return Boolean.valueOf(true);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\LookupProcessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */