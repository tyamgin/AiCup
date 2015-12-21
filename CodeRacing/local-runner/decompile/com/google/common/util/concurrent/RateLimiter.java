package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;

@Beta
public abstract class RateLimiter
{
  private final SleepingTicker ticker;
  private final long offsetNanos;
  double storedPermits;
  double maxPermits;
  volatile double stableIntervalMicros;
  private final Object mutex = new Object();
  private long nextFreeTicketMicros = 0L;
  
  public static RateLimiter create(double paramDouble)
  {
    return create(SleepingTicker.SYSTEM_TICKER, paramDouble);
  }
  
  @VisibleForTesting
  static RateLimiter create(SleepingTicker paramSleepingTicker, double paramDouble)
  {
    Bursty localBursty = new Bursty(paramSleepingTicker);
    localBursty.setRate(paramDouble);
    return localBursty;
  }
  
  public static RateLimiter create(double paramDouble, long paramLong, TimeUnit paramTimeUnit)
  {
    return create(SleepingTicker.SYSTEM_TICKER, paramDouble, paramLong, paramTimeUnit);
  }
  
  @VisibleForTesting
  static RateLimiter create(SleepingTicker paramSleepingTicker, double paramDouble, long paramLong, TimeUnit paramTimeUnit)
  {
    WarmingUp localWarmingUp = new WarmingUp(paramSleepingTicker, paramLong, paramTimeUnit);
    localWarmingUp.setRate(paramDouble);
    return localWarmingUp;
  }
  
  @VisibleForTesting
  static RateLimiter createBursty(SleepingTicker paramSleepingTicker, double paramDouble, int paramInt)
  {
    Bursty localBursty = new Bursty(paramSleepingTicker);
    localBursty.setRate(paramDouble);
    localBursty.maxPermits = paramInt;
    return localBursty;
  }
  
  private RateLimiter(SleepingTicker paramSleepingTicker)
  {
    this.ticker = paramSleepingTicker;
    this.offsetNanos = paramSleepingTicker.read();
  }
  
  public final void setRate(double paramDouble)
  {
    Preconditions.checkArgument((paramDouble > 0.0D) && (!Double.isNaN(paramDouble)), "rate must be positive");
    synchronized (this.mutex)
    {
      resync(readSafeMicros());
      double d = TimeUnit.SECONDS.toMicros(1L) / paramDouble;
      this.stableIntervalMicros = d;
      doSetRate(paramDouble, d);
    }
  }
  
  abstract void doSetRate(double paramDouble1, double paramDouble2);
  
  public final double getRate()
  {
    return TimeUnit.SECONDS.toMicros(1L) / this.stableIntervalMicros;
  }
  
  public void acquire()
  {
    acquire(1);
  }
  
  public void acquire(int paramInt)
  {
    checkPermits(paramInt);
    long l;
    synchronized (this.mutex)
    {
      l = reserveNextTicket(paramInt, readSafeMicros());
    }
    this.ticker.sleepMicrosUninterruptibly(l);
  }
  
  public boolean tryAcquire(long paramLong, TimeUnit paramTimeUnit)
  {
    return tryAcquire(1, paramLong, paramTimeUnit);
  }
  
  public boolean tryAcquire(int paramInt)
  {
    return tryAcquire(paramInt, 0L, TimeUnit.MICROSECONDS);
  }
  
  public boolean tryAcquire()
  {
    return tryAcquire(1, 0L, TimeUnit.MICROSECONDS);
  }
  
  public boolean tryAcquire(int paramInt, long paramLong, TimeUnit paramTimeUnit)
  {
    long l1 = paramTimeUnit.toMicros(paramLong);
    checkPermits(paramInt);
    long l2;
    synchronized (this.mutex)
    {
      long l3 = readSafeMicros();
      if (this.nextFreeTicketMicros > l3 + l1) {
        return false;
      }
      l2 = reserveNextTicket(paramInt, l3);
    }
    this.ticker.sleepMicrosUninterruptibly(l2);
    return true;
  }
  
  private static void checkPermits(int paramInt)
  {
    Preconditions.checkArgument(paramInt > 0, "Requested permits must be positive");
  }
  
  private long reserveNextTicket(double paramDouble, long paramLong)
  {
    resync(paramLong);
    long l1 = this.nextFreeTicketMicros - paramLong;
    double d1 = Math.min(paramDouble, this.storedPermits);
    double d2 = paramDouble - d1;
    long l2 = storedPermitsToWaitTime(this.storedPermits, d1) + (d2 * this.stableIntervalMicros);
    this.nextFreeTicketMicros += l2;
    this.storedPermits -= d1;
    return l1;
  }
  
  abstract long storedPermitsToWaitTime(double paramDouble1, double paramDouble2);
  
  private void resync(long paramLong)
  {
    if (paramLong > this.nextFreeTicketMicros)
    {
      this.storedPermits = Math.min(this.maxPermits, this.storedPermits + (paramLong - this.nextFreeTicketMicros) / this.stableIntervalMicros);
      this.nextFreeTicketMicros = paramLong;
    }
  }
  
  private long readSafeMicros()
  {
    return TimeUnit.NANOSECONDS.toMicros(this.ticker.read() - this.offsetNanos);
  }
  
  public String toString()
  {
    return String.format("RateLimiter[stableRate=%3.1fqps]", new Object[] { Double.valueOf(1000000.0D / this.stableIntervalMicros) });
  }
  
  @VisibleForTesting
  static abstract class SleepingTicker
    extends Ticker
  {
    static final SleepingTicker SYSTEM_TICKER = new SleepingTicker()
    {
      public long read()
      {
        return systemTicker().read();
      }
      
      public void sleepMicrosUninterruptibly(long paramAnonymousLong)
      {
        if (paramAnonymousLong > 0L) {
          Uninterruptibles.sleepUninterruptibly(paramAnonymousLong, TimeUnit.MICROSECONDS);
        }
      }
    };
    
    abstract void sleepMicrosUninterruptibly(long paramLong);
  }
  
  private static class Bursty
    extends RateLimiter
  {
    Bursty(RateLimiter.SleepingTicker paramSleepingTicker)
    {
      super(null);
    }
    
    void doSetRate(double paramDouble1, double paramDouble2)
    {
      double d = this.maxPermits;
      this.maxPermits = paramDouble1;
      this.storedPermits = (d == 0.0D ? 0.0D : this.storedPermits * this.maxPermits / d);
    }
    
    long storedPermitsToWaitTime(double paramDouble1, double paramDouble2)
    {
      return 0L;
    }
  }
  
  private static class WarmingUp
    extends RateLimiter
  {
    final long warmupPeriodMicros;
    private double slope;
    private double halfPermits;
    
    WarmingUp(RateLimiter.SleepingTicker paramSleepingTicker, long paramLong, TimeUnit paramTimeUnit)
    {
      super(null);
      this.warmupPeriodMicros = paramTimeUnit.toMicros(paramLong);
    }
    
    void doSetRate(double paramDouble1, double paramDouble2)
    {
      double d1 = this.maxPermits;
      this.maxPermits = (this.warmupPeriodMicros / paramDouble2);
      this.halfPermits = (this.maxPermits / 2.0D);
      double d2 = paramDouble2 * 3.0D;
      this.slope = ((d2 - paramDouble2) / this.halfPermits);
      if (d1 == Double.POSITIVE_INFINITY) {
        this.storedPermits = 0.0D;
      } else {
        this.storedPermits = (d1 == 0.0D ? this.maxPermits : this.storedPermits * this.maxPermits / d1);
      }
    }
    
    long storedPermitsToWaitTime(double paramDouble1, double paramDouble2)
    {
      double d1 = paramDouble1 - this.halfPermits;
      long l = 0L;
      if (d1 > 0.0D)
      {
        double d2 = Math.min(d1, paramDouble2);
        l = (d2 * (permitsToTime(d1) + permitsToTime(d1 - d2)) / 2.0D);
        paramDouble2 -= d2;
      }
      l = (l + this.stableIntervalMicros * paramDouble2);
      return l;
    }
    
    private double permitsToTime(double paramDouble)
    {
      return this.stableIntervalMicros + paramDouble * this.slope;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\RateLimiter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */