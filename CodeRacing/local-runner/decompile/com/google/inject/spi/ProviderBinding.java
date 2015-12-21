package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.Key;

public abstract interface ProviderBinding
  extends Binding
{
  public abstract Key getProvidedKey();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ProviderBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */