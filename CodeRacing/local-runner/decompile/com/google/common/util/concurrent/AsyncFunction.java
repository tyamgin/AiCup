package com.google.common.util.concurrent;

public abstract interface AsyncFunction
{
  public abstract ListenableFuture apply(Object paramObject)
    throws Exception;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\AsyncFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */