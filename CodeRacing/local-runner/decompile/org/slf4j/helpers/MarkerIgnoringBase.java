package org.slf4j.helpers;

import org.slf4j.Logger;

public abstract class MarkerIgnoringBase
  extends NamedLoggerBase
  implements Logger
{
  public String toString()
  {
    return getClass().getName() + "(" + getName() + ")";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\slf4j\helpers\MarkerIgnoringBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */