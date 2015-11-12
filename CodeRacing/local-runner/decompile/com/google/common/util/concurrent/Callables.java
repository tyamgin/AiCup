package com.google.common.util.concurrent;

import java.util.concurrent.Callable;

public final class Callables
{
  public static Callable returning(Object paramObject)
  {
    new Callable()
    {
      public Object call()
      {
        return this.val$value;
      }
    };
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\Callables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */