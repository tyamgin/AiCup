package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;
import java.util.Set;

@GwtCompatible
public abstract interface SetMultimap
  extends Multimap
{
  public abstract Set get(Object paramObject);
  
  public abstract Set removeAll(Object paramObject);
  
  public abstract Set replaceValues(Object paramObject, Iterable paramIterable);
  
  public abstract Set entries();
  
  public abstract Map asMap();
  
  public abstract boolean equals(Object paramObject);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SetMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */