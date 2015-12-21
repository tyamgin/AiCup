package com.google.inject.spi;

import com.google.inject.TypeLiteral;

public abstract interface TypeListener
{
  public abstract void hear(TypeLiteral paramTypeLiteral, TypeEncounter paramTypeEncounter);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\TypeListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */