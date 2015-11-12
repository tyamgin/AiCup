package de.schlichtherle.truezip.util;

public final class JSE7
{
  public static final boolean AVAILABLE;
  
  static
  {
    boolean bool = true;
    try
    {
      Class.forName("java.nio.file.Path");
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      bool = false;
    }
    AVAILABLE = bool;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\JSE7.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */