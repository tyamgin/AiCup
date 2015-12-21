package org.apache.commons.lang3;

import java.io.Serializable;

public class ObjectUtils
{
  public static final Null NULL = new Null();
  
  @Deprecated
  public static String toString(Object paramObject)
  {
    return paramObject == null ? "" : paramObject.toString();
  }
  
  public static class Null
    implements Serializable
  {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\ObjectUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */