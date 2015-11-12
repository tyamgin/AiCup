package com.google.inject.spi;

import com.google.inject.TypeLiteral;

public abstract interface TypeConverter
{
  public abstract Object convert(String paramString, TypeLiteral paramTypeLiteral);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\TypeConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */