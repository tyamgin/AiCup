package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.NoSuchElementException;

@GwtCompatible
public abstract class AbstractIterator
  extends UnmodifiableIterator
{
  private State state = State.NOT_READY;
  private Object next;
  
  protected abstract Object computeNext();
  
  protected final Object endOfData()
  {
    this.state = State.DONE;
    return null;
  }
  
  public final boolean hasNext()
  {
    Preconditions.checkState(this.state != State.FAILED);
    switch (this.state)
    {
    case DONE: 
      return false;
    case READY: 
      return true;
    }
    return tryToComputeNext();
  }
  
  private boolean tryToComputeNext()
  {
    this.state = State.FAILED;
    this.next = computeNext();
    if (this.state != State.DONE)
    {
      this.state = State.READY;
      return true;
    }
    return false;
  }
  
  public final Object next()
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    this.state = State.NOT_READY;
    return this.next;
  }
  
  public final Object peek()
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    return this.next;
  }
  
  private static enum State
  {
    READY,  NOT_READY,  DONE,  FAILED;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\AbstractIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */