package com.google.gson;

public enum LongSerializationPolicy
{
  DEFAULT,  STRING;
  
  public abstract JsonElement serialize(Long paramLong);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\LongSerializationPolicy.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */