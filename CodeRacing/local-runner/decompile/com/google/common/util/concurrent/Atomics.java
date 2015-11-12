package com.google.common.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class Atomics
{
  public static AtomicReference newReference()
  {
    return new AtomicReference();
  }
  
  public static AtomicReference newReference(Object paramObject)
  {
    return new AtomicReference(paramObject);
  }
  
  public static AtomicReferenceArray newReferenceArray(int paramInt)
  {
    return new AtomicReferenceArray(paramInt);
  }
  
  public static AtomicReferenceArray newReferenceArray(Object[] paramArrayOfObject)
  {
    return new AtomicReferenceArray(paramArrayOfObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\util\concurrent\Atomics.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */