package de.schlichtherle.truezip.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class FilteringIterator
  implements Iterator
{
  private final Iterator it;
  private Boolean hasNext;
  private Object next;
  
  protected FilteringIterator(Iterator paramIterator)
  {
    if (null == (this.it = paramIterator)) {
      throw new NullPointerException();
    }
  }
  
  protected abstract boolean accept(Object paramObject);
  
  public boolean hasNext()
  {
    if (null != this.hasNext) {
      return this.hasNext.booleanValue();
    }
    while (this.it.hasNext()) {
      if (accept(this.next = this.it.next())) {
        return (this.hasNext = Boolean.valueOf(true)).booleanValue();
      }
    }
    return (this.hasNext = Boolean.valueOf(false)).booleanValue();
  }
  
  public Object next()
  {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    this.hasNext = null;
    return this.next;
  }
  
  public void remove()
  {
    this.it.remove();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\FilteringIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */