package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.ExceptionContext;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class MathArithmeticException
  extends ArithmeticException
{
  private final ExceptionContext context = new ExceptionContext(this);
  
  public MathArithmeticException()
  {
    this.context.addMessage(LocalizedFormats.ARITHMETIC_EXCEPTION, new Object[0]);
  }
  
  public MathArithmeticException(Localizable paramLocalizable, Object... paramVarArgs)
  {
    this.context.addMessage(paramLocalizable, paramVarArgs);
  }
  
  public String getMessage()
  {
    return this.context.getMessage();
  }
  
  public String getLocalizedMessage()
  {
    return this.context.getLocalizedMessage();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\math3\exception\MathArithmeticException.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */