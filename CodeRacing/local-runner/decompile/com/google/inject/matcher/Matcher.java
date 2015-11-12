package com.google.inject.matcher;

public abstract interface Matcher
{
  public abstract boolean matches(Object paramObject);
  
  public abstract Matcher and(Matcher paramMatcher);
  
  public abstract Matcher or(Matcher paramMatcher);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\matcher\Matcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */