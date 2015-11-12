package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.Key;

public abstract interface LinkedKeyBinding
  extends Binding
{
  public abstract Key getLinkedKey();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\LinkedKeyBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */