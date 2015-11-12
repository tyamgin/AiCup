package de.schlichtherle.truezip.util;

import java.net.URISyntaxException;

public class QuotedUriSyntaxException
  extends URISyntaxException
{
  public QuotedUriSyntaxException(Object paramObject, String paramString)
  {
    this(paramObject, paramString, -1);
  }
  
  public QuotedUriSyntaxException(Object paramObject, String paramString, int paramInt)
  {
    super("\"" + paramObject + "\"", paramString, paramInt);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\QuotedUriSyntaxException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */