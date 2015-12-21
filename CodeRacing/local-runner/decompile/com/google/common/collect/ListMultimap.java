package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.List;
import java.util.Map;

@GwtCompatible
public abstract interface ListMultimap
  extends Multimap
{
  public abstract List get(Object paramObject);
  
  public abstract List removeAll(Object paramObject);
  
  public abstract List replaceValues(Object paramObject, Iterable paramIterable);
  
  public abstract Map asMap();
  
  public abstract boolean equals(Object paramObject);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ListMultimap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */