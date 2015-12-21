package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.util.concurrent.Executor;

@Beta
public final class RemovalListeners
{
  public static RemovalListener asynchronous(final RemovalListener paramRemovalListener, Executor paramExecutor)
  {
    Preconditions.checkNotNull(paramRemovalListener);
    Preconditions.checkNotNull(paramExecutor);
    new RemovalListener()
    {
      public void onRemoval(final RemovalNotification paramAnonymousRemovalNotification)
      {
        this.val$executor.execute(new Runnable()
        {
          public void run()
          {
            RemovalListeners.1.this.val$listener.onRemoval(paramAnonymousRemovalNotification);
          }
        });
      }
    };
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\cache\RemovalListeners.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */