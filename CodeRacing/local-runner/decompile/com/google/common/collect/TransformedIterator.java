package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Iterator;

@GwtCompatible
abstract class TransformedIterator
  implements Iterator
{
  final Iterator backingIterator;
  
  TransformedIterator(Iterator paramIterator)
  {
    this.backingIterator = ((Iterator)Preconditions.checkNotNull(paramIterator));
  }
  
  abstract Object transform(Object paramObject);
  
  public final boolean hasNext()
  {
    return this.backingIterator.hasNext();
  }
  
  public final Object next()
  {
    return transform(this.backingIterator.next());
  }
  
  public final void remove()
  {
    this.backingIterator.remove();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\TransformedIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */