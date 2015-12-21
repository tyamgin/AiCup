package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

@Beta
public class AsyncEventBus
  extends EventBus
{
  private final Executor executor;
  private final ConcurrentLinkedQueue eventsToDispatch = new ConcurrentLinkedQueue();
  
  public AsyncEventBus(String paramString, Executor paramExecutor)
  {
    super(paramString);
    this.executor = ((Executor)Preconditions.checkNotNull(paramExecutor));
  }
  
  public AsyncEventBus(Executor paramExecutor)
  {
    this.executor = ((Executor)Preconditions.checkNotNull(paramExecutor));
  }
  
  void enqueueEvent(Object paramObject, EventHandler paramEventHandler)
  {
    this.eventsToDispatch.offer(new EventBus.EventWithHandler(paramObject, paramEventHandler));
  }
  
  protected void dispatchQueuedEvents()
  {
    for (;;)
    {
      EventBus.EventWithHandler localEventWithHandler = (EventBus.EventWithHandler)this.eventsToDispatch.poll();
      if (localEventWithHandler == null) {
        break;
      }
      dispatch(localEventWithHandler.event, localEventWithHandler.handler);
    }
  }
  
  void dispatch(final Object paramObject, final EventHandler paramEventHandler)
  {
    Preconditions.checkNotNull(paramObject);
    Preconditions.checkNotNull(paramEventHandler);
    this.executor.execute(new Runnable()
    {
      public void run()
      {
        AsyncEventBus.this.dispatch(paramObject, paramEventHandler);
      }
    });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\eventbus\AsyncEventBus.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */