package com.google.common.base;

import java.lang.ref.PhantomReference;

public abstract class FinalizablePhantomReference
  extends PhantomReference
  implements FinalizableReference
{
  protected FinalizablePhantomReference(Object paramObject, FinalizableReferenceQueue paramFinalizableReferenceQueue)
  {
    super(paramObject, paramFinalizableReferenceQueue.queue);
    paramFinalizableReferenceQueue.cleanUp();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\FinalizablePhantomReference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */