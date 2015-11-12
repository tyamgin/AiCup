package com.google.common.base;

import java.lang.ref.WeakReference;

public abstract class FinalizableWeakReference
  extends WeakReference
  implements FinalizableReference
{
  protected FinalizableWeakReference(Object paramObject, FinalizableReferenceQueue paramFinalizableReferenceQueue)
  {
    super(paramObject, paramFinalizableReferenceQueue.queue);
    paramFinalizableReferenceQueue.cleanUp();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\FinalizableWeakReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */