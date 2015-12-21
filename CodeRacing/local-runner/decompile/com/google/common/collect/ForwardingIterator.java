package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public abstract class ForwardingIterator
  extends ForwardingObject
  implements Iterator
{
  protected abstract Iterator delegate();
  
  public boolean hasNext()
  {
    return delegate().hasNext();
  }
  
  public Object next()
  {
    return delegate().next();
  }
  
  public void remove()
  {
    delegate().remove();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ForwardingIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */