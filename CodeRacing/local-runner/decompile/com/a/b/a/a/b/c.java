package com.a.b.a.a.b;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class c
  implements ThreadFactory
{
  private final AtomicInteger b = new AtomicInteger();
  
  c(a parama) {}
  
  public Thread newThread(Runnable paramRunnable)
  {
    Thread localThread = new Thread(paramRunnable);
    localThread.setDaemon(true);
    localThread.setName(String.format("%s#StrategyThread-%d", new Object[] { a.class.getSimpleName(), Integer.valueOf(this.b.incrementAndGet()) }));
    return localThread;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */