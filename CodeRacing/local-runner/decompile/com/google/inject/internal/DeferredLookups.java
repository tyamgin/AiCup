package com.google.inject.internal;

import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.ProviderLookup;
import java.util.List;

final class DeferredLookups
  implements Lookups
{
  private final InjectorImpl injector;
  private final List lookups = Lists.newArrayList();
  
  DeferredLookups(InjectorImpl paramInjectorImpl)
  {
    this.injector = paramInjectorImpl;
  }
  
  void initialize(Errors paramErrors)
  {
    this.injector.lookups = this.injector;
    new LookupProcessor(paramErrors).process(this.injector, this.lookups);
  }
  
  public Provider getProvider(Key paramKey)
  {
    ProviderLookup localProviderLookup = new ProviderLookup(paramKey, paramKey);
    this.lookups.add(localProviderLookup);
    return localProviderLookup.getProvider();
  }
  
  public MembersInjector getMembersInjector(TypeLiteral paramTypeLiteral)
  {
    MembersInjectorLookup localMembersInjectorLookup = new MembersInjectorLookup(paramTypeLiteral, paramTypeLiteral);
    this.lookups.add(localMembersInjectorLookup);
    return localMembersInjectorLookup.getMembersInjector();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\DeferredLookups.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */