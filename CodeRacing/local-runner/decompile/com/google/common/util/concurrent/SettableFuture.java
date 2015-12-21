package com.google.common.util.concurrent;

public final class SettableFuture
  extends AbstractFuture
{
  public static SettableFuture create()
  {
    return new SettableFuture();
  }
  
  public boolean set(Object paramObject)
  {
    return super.set(paramObject);
  }
  
  public boolean setException(Throwable paramThrowable)
  {
    return super.setException(paramThrowable);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\SettableFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */