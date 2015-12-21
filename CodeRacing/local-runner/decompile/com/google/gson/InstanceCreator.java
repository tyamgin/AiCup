package com.google.gson;

import java.lang.reflect.Type;

public abstract interface InstanceCreator
{
  public abstract Object createInstance(Type paramType);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\gson\InstanceCreator.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */