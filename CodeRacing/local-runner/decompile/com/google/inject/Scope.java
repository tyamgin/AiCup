package com.google.inject;

public abstract interface Scope
{
  public abstract Provider scope(Key paramKey, Provider paramProvider);
  
  public abstract String toString();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\Scope.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */