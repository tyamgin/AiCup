package com.google.inject.internal.cglib.core;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class $DuplicatesPredicate
  implements .Predicate
{
  private Set unique = new HashSet();
  
  public boolean evaluate(Object paramObject)
  {
    return this.unique.add(.MethodWrapper.create((Method)paramObject));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$DuplicatesPredicate.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */