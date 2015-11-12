package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.Executor;

@Beta
public abstract interface Service
{
  public abstract ListenableFuture start();
  
  public abstract State startAndWait();
  
  public abstract boolean isRunning();
  
  public abstract State state();
  
  public abstract ListenableFuture stop();
  
  public abstract State stopAndWait();
  
  public abstract Throwable failureCause();
  
  public abstract void addListener(Listener paramListener, Executor paramExecutor);
  
  @Beta
  public static abstract interface Listener
  {
    public abstract void starting();
    
    public abstract void running();
    
    public abstract void stopping(Service.State paramState);
    
    public abstract void terminated(Service.State paramState);
    
    public abstract void failed(Service.State paramState, Throwable paramThrowable);
  }
  
  @Beta
  public static enum State
  {
    NEW,  STARTING,  RUNNING,  STOPPING,  TERMINATED,  FAILED;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\Service.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */