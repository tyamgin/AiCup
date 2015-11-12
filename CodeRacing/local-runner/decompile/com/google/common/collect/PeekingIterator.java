package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public abstract interface PeekingIterator
  extends Iterator
{
  public abstract Object peek();
  
  public abstract Object next();
  
  public abstract void remove();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\PeekingIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */