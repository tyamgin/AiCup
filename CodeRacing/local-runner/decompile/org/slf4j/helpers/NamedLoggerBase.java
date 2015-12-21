package org.slf4j.helpers;

import java.io.Serializable;
import org.slf4j.Logger;

abstract class NamedLoggerBase
  implements Serializable, Logger
{
  protected String name;
  
  public String getName()
  {
    return this.name;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\slf4j\helpers\NamedLoggerBase.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */