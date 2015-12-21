package de.schlichtherle.truezip.util;

public abstract class ControlFlowException
  extends Error
{
  private static final String TRACEABLE_PROPERTY_KEY = ControlFlowException.class.getName() + ".traceable";
  
  public ControlFlowException()
  {
    this(null);
  }
  
  public ControlFlowException(Throwable paramThrowable)
  {
    super(paramThrowable);
  }
  
  public static boolean isTraceable()
  {
    return Boolean.getBoolean(TRACEABLE_PROPERTY_KEY);
  }
  
  public Throwable fillInStackTrace()
  {
    return isTraceable() ? super.fillInStackTrace() : this;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\ControlFlowException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */