package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.NoSuchElementException;

@GwtCompatible
public abstract class AbstractSequentialIterator
  extends UnmodifiableIterator
{
  private Object nextOrNull;
  
  protected AbstractSequentialIterator(Object paramObject)
  {
    this.nextOrNull = paramObject;
  }
  
  protected abstract Object computeNext(Object paramObject);
  
  public final boolean hasNext()
  {
    return this.nextOrNull != null;
  }
  
  public final Object next()
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    try
    {
      Object localObject1 = this.nextOrNull;
      return localObject1;
    }
    finally
    {
      this.nextOrNull = computeNext(this.nextOrNull);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractSequentialIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */