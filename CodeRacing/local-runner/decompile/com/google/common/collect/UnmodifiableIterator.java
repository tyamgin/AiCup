package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public abstract class UnmodifiableIterator
  implements Iterator
{
  @Deprecated
  public final void remove()
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\UnmodifiableIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */