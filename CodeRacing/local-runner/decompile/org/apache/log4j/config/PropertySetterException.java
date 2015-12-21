package org.apache.log4j.config;

public class PropertySetterException
  extends Exception
{
  protected Throwable rootCause;
  
  public PropertySetterException(String paramString)
  {
    super(paramString);
  }
  
  public PropertySetterException(Throwable paramThrowable)
  {
    this.rootCause = paramThrowable;
  }
  
  public String getMessage()
  {
    String str = super.getMessage();
    if ((str == null) && (this.rootCause != null)) {
      str = this.rootCause.getMessage();
    }
    return str;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\log4j\config\PropertySetterException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       0.7.1
 */