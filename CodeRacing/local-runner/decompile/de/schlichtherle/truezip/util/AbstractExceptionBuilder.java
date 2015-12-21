package de.schlichtherle.truezip.util;

public abstract class AbstractExceptionBuilder
{
  private Exception assembly;
  
  protected abstract Exception update(Exception paramException1, Exception paramException2);
  
  protected Exception post(Exception paramException)
  {
    return paramException;
  }
  
  public final void warn(Exception paramException)
  {
    if (null == paramException) {
      throw new NullPointerException();
    }
    this.assembly = update(paramException, this.assembly);
  }
  
  public final void check()
    throws Exception
  {
    Exception localException = this.assembly;
    if (null != localException)
    {
      this.assembly = null;
      throw post(localException);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\AbstractExceptionBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */