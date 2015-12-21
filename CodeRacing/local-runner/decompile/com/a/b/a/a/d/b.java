package com.a.b.a.a.d;

import com.a.b.a.a.c.h;
import com.codeforces.commons.process.ThreadUtil;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;

class b
  implements Runnable
{
  b(a parama, com.a.b.a.a.a.b paramb) {}
  
  public void run()
  {
    long l1 = System.currentTimeMillis();
    long l2 = l1;
    try
    {
      h localh = null;
      int i = 0;
      a.f localf;
      while (((localf = a()) != null) && (localf.a() != null))
      {
        l1 = a(l1, a.a(this.b).get());
        long l3 = System.currentTimeMillis();
        if (l3 - l2 > 1000L)
        {
          l2 = System.currentTimeMillis();
          a.b(this.b).set(i);
          i = 0;
        }
        a.a(this.b, localf);
        i++;
        localh = localf.a();
      }
      if (localh != null) {
        a.a(this.b, localh);
      }
      Thread.sleep(TimeUnit.SECONDS.toMillis(30L));
    }
    catch (InterruptedException localInterruptedException) {}catch (RuntimeException localRuntimeException)
    {
      a.b().error("Got unexpected runtime exception in rendering thread.", localRuntimeException);
      throw localRuntimeException;
    }
    System.exit(0);
  }
  
  private a.f a()
    throws InterruptedException
  {
    return this.a.n() ? (a.f)a.c(this.b).poll(20L, TimeUnit.MINUTES) : (a.f)a.c(this.b).poll(30L, TimeUnit.SECONDS);
  }
  
  private long a(long paramLong1, long paramLong2)
  {
    long l = System.currentTimeMillis();
    if (l - paramLong1 < paramLong2) {
      ThreadUtil.sleep(paramLong2 - l + paramLong1);
    }
    return System.currentTimeMillis();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\d\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */