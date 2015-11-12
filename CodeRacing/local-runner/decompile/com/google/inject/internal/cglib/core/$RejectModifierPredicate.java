package com.google.inject.internal.cglib.core;

import java.lang.reflect.Member;

public class $RejectModifierPredicate
  implements .Predicate
{
  private int rejectMask;
  
  public $RejectModifierPredicate(int paramInt)
  {
    this.rejectMask = paramInt;
  }
  
  public boolean evaluate(Object paramObject)
  {
    return (((Member)paramObject).getModifiers() & this.rejectMask) == 0;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$RejectModifierPredicate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */