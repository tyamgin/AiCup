package org.apache.commons.lang3;

public class NotImplementedException
  extends UnsupportedOperationException
{
  private final String code;
  
  public NotImplementedException(String paramString)
  {
    this(paramString, (String)null);
  }
  
  public NotImplementedException(String paramString1, String paramString2)
  {
    super(paramString1);
    this.code = paramString2;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\NotImplementedException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */