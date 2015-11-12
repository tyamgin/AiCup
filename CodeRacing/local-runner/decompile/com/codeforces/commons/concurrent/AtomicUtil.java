package com.codeforces.commons.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicUtil
{
  public static void invert(AtomicBoolean paramAtomicBoolean)
  {
    boolean bool;
    do
    {
      bool = paramAtomicBoolean.get();
    } while (!paramAtomicBoolean.compareAndSet(bool, !bool));
  }
  
  public static void decrement(AtomicInteger paramAtomicInteger, int paramInt)
  {
    int i;
    int j;
    do
    {
      i = paramAtomicInteger.get();
      j = normalizeValue(i - 1, paramInt);
    } while (!paramAtomicInteger.compareAndSet(i, j));
  }
  
  private static int normalizeValue(int paramInt1, int paramInt2)
  {
    while (paramInt1 > paramInt2) {
      paramInt1 -= paramInt2 + 1;
    }
    while (paramInt1 < 0) {
      paramInt1 += paramInt2 + 1;
    }
    return paramInt1;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\concurrent\AtomicUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */