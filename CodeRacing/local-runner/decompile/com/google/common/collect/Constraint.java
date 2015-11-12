package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;

@Beta
@GwtCompatible
public abstract interface Constraint
{
  public abstract Object checkElement(Object paramObject);
  
  public abstract String toString();
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Constraint.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */