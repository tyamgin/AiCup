package com.codeforces.commons.holder;

public class SimpleMutable
  extends Mutable
{
  private Object value;
  
  public Object get()
  {
    return this.value;
  }
  
  public Object set(Object paramObject)
  {
    return this.value = paramObject;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\holder\SimpleMutable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */