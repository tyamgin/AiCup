package com.google.inject.binder;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import java.lang.reflect.Constructor;

public abstract interface LinkedBindingBuilder
  extends ScopedBindingBuilder
{
  public abstract ScopedBindingBuilder to(Class paramClass);
  
  public abstract ScopedBindingBuilder to(TypeLiteral paramTypeLiteral);
  
  public abstract ScopedBindingBuilder to(Key paramKey);
  
  public abstract void toInstance(Object paramObject);
  
  public abstract ScopedBindingBuilder toProvider(com.google.inject.Provider paramProvider);
  
  public abstract ScopedBindingBuilder toProvider(javax.inject.Provider paramProvider);
  
  public abstract ScopedBindingBuilder toProvider(Class paramClass);
  
  public abstract ScopedBindingBuilder toProvider(TypeLiteral paramTypeLiteral);
  
  public abstract ScopedBindingBuilder toProvider(Key paramKey);
  
  public abstract ScopedBindingBuilder toConstructor(Constructor paramConstructor);
  
  public abstract ScopedBindingBuilder toConstructor(Constructor paramConstructor, TypeLiteral paramTypeLiteral);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\binder\LinkedBindingBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */