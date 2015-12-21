package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.ListIterator;

@GwtCompatible
abstract class TransformedListIterator
  extends TransformedIterator
  implements ListIterator
{
  TransformedListIterator(ListIterator paramListIterator)
  {
    super(paramListIterator);
  }
  
  private ListIterator backingIterator()
  {
    return Iterators.cast(this.backingIterator);
  }
  
  public final boolean hasPrevious()
  {
    return backingIterator().hasPrevious();
  }
  
  public final Object previous()
  {
    return transform(backingIterator().previous());
  }
  
  public final int nextIndex()
  {
    return backingIterator().nextIndex();
  }
  
  public final int previousIndex()
  {
    return backingIterator().previousIndex();
  }
  
  public void set(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
  
  public void add(Object paramObject)
  {
    throw new UnsupportedOperationException();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\TransformedListIterator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */