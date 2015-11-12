package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.collect.ForwardingObject;
import java.util.concurrent.Executor;

@Beta
@Deprecated
public abstract class ForwardingService
  extends ForwardingObject
  implements Service
{
  protected abstract Service delegate();
  
  public ListenableFuture start()
  {
    return delegate().start();
  }
  
  public Service.State state()
  {
    return delegate().state();
  }
  
  public ListenableFuture stop()
  {
    return delegate().stop();
  }
  
  public Service.State startAndWait()
  {
    return delegate().startAndWait();
  }
  
  public Service.State stopAndWait()
  {
    return delegate().stopAndWait();
  }
  
  public boolean isRunning()
  {
    return delegate().isRunning();
  }
  
  public void addListener(Service.Listener paramListener, Executor paramExecutor)
  {
    delegate().addListener(paramListener, paramExecutor);
  }
  
  public Throwable failureCause()
  {
    return delegate().failureCause();
  }
  
  protected Service.State standardStartAndWait()
  {
    return (Service.State)Futures.getUnchecked(start());
  }
  
  protected Service.State standardStopAndWait()
  {
    return (Service.State)Futures.getUnchecked(stop());
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\ForwardingService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */