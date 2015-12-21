package de.schlichtherle.truezip.util;

import java.lang.reflect.Constructor;

public class Throwables
{
  public static Throwable wrap(Throwable paramThrowable)
  {
    try
    {
      return ((Throwable)paramThrowable.getClass().getConstructor(new Class[] { String.class }).newInstance(new Object[] { paramThrowable.toString() })).initCause(paramThrowable);
    }
    catch (Exception localException)
    {
      if (JSE7.AVAILABLE) {
        paramThrowable.addSuppressed(localException);
      }
    }
    return paramThrowable;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\Throwables.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */