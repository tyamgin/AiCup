package com.google.common.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

public class ListenableFutureTask
  extends FutureTask
  implements ListenableFuture
{
  private final ExecutionList executionList = new ExecutionList();
  
  public static ListenableFutureTask create(Callable paramCallable)
  {
    return new ListenableFutureTask(paramCallable);
  }
  
  public static ListenableFutureTask create(Runnable paramRunnable, Object paramObject)
  {
    return new ListenableFutureTask(paramRunnable, paramObject);
  }
  
  ListenableFutureTask(Callable paramCallable)
  {
    super(paramCallable);
  }
  
  ListenableFutureTask(Runnable paramRunnable, Object paramObject)
  {
    super(paramRunnable, paramObject);
  }
  
  public void addListener(Runnable paramRunnable, Executor paramExecutor)
  {
    this.executionList.add(paramRunnable, paramExecutor);
  }
  
  protected void done()
  {
    this.executionList.execute();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\ListenableFutureTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */