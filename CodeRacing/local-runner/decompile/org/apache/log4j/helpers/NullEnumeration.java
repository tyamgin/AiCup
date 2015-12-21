package org.apache.log4j.helpers;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class NullEnumeration
  implements Enumeration
{
  private static final NullEnumeration instance = new NullEnumeration();
  
  public static NullEnumeration getInstance()
  {
    return instance;
  }
  
  public boolean hasMoreElements()
  {
    return false;
  }
  
  public Object nextElement()
  {
    throw new NoSuchElementException();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\helpers\NullEnumeration.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */