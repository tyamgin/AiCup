package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public abstract interface CheckedFuture
  extends ListenableFuture
{
  public abstract Object checkedGet()
    throws Exception;
  
  public abstract Object checkedGet(long paramLong, TimeUnit paramTimeUnit)
    throws TimeoutException, Exception;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\CheckedFuture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */