package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.Key;
import java.util.Set;

public abstract interface ConvertedConstantBinding
  extends Binding, HasDependencies
{
  public abstract Object getValue();
  
  public abstract TypeConverterBinding getTypeConverterBinding();
  
  public abstract Key getSourceKey();
  
  public abstract Set getDependencies();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\spi\ConvertedConstantBinding.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */