package de.schlichtherle.truezip.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class JointIterator
  implements Iterator
{
  private Iterator i1;
  private Iterator i2;
  
  public JointIterator(Iterator paramIterator1, Iterator paramIterator2)
  {
    if ((paramIterator1 == null) || (paramIterator2 == null)) {
      throw new NullPointerException();
    }
    this.i1 = paramIterator1;
    this.i2 = paramIterator2;
  }
  
  public boolean hasNext()
  {
    return (this.i1.hasNext()) || ((this.i1 != this.i2) && ((this.i1 = this.i2).hasNext()));
  }
  
  public Object next()
  {
    try
    {
      return this.i1.next();
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
      if (this.i1 == this.i2) {
        throw localNoSuchElementException;
      }
    }
    return (this.i1 = this.i2).next();
  }
  
  public void remove()
  {
    this.i1.remove();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\JointIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */