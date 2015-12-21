package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Map;

@GwtCompatible
public abstract interface MapDifference
{
  public abstract boolean areEqual();
  
  public abstract Map entriesOnlyOnLeft();
  
  public abstract Map entriesOnlyOnRight();
  
  public abstract Map entriesInCommon();
  
  public abstract Map entriesDiffering();
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public static abstract interface ValueDifference
  {
    public abstract Object leftValue();
    
    public abstract Object rightValue();
    
    public abstract boolean equals(Object paramObject);
    
    public abstract int hashCode();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\MapDifference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */