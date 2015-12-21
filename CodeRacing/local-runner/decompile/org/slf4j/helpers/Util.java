package org.slf4j.helpers;

import java.io.PrintStream;

public final class Util
{
  private static final ClassContextSecurityManager SECURITY_MANAGER = new ClassContextSecurityManager(null);
  
  public static Class getCallingClass()
  {
    Class[] arrayOfClass = SECURITY_MANAGER.getClassContext();
    String str = Util.class.getName();
    for (int i = 0; (i < arrayOfClass.length) && (!str.equals(arrayOfClass[i].getName())); i++) {}
    if ((i >= arrayOfClass.length) || (i + 2 >= arrayOfClass.length)) {
      throw new IllegalStateException("Failed to find org.slf4j.helpers.Util or its caller in the stack; this should not happen");
    }
    return arrayOfClass[(i + 2)];
  }
  
  public static final void report(String paramString, Throwable paramThrowable)
  {
    System.err.println(paramString);
    System.err.println("Reported exception:");
    paramThrowable.printStackTrace();
  }
  
  public static final void report(String paramString)
  {
    System.err.println("SLF4J: " + paramString);
  }
  
  private static final class ClassContextSecurityManager
    extends SecurityManager
  {
    protected Class[] getClassContext()
    {
      return super.getClassContext();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\slf4j\helpers\Util.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */