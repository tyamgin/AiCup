package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.SortedMap;

@GwtCompatible
public abstract interface SortedMapDifference
  extends MapDifference
{
  public abstract SortedMap entriesOnlyOnLeft();
  
  public abstract SortedMap entriesOnlyOnRight();
  
  public abstract SortedMap entriesInCommon();
  
  public abstract SortedMap entriesDiffering();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\SortedMapDifference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */