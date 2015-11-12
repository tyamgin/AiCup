package com.google.inject.internal;

import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;

abstract interface Lookups
{
  public abstract Provider getProvider(Key paramKey);
  
  public abstract MembersInjector getMembersInjector(TypeLiteral paramTypeLiteral);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\Lookups.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */