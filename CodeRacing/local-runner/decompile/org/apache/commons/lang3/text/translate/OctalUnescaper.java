package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

public class OctalUnescaper
  extends CharSequenceTranslator
{
  public int translate(CharSequence paramCharSequence, int paramInt, Writer paramWriter)
    throws IOException
  {
    int i = paramCharSequence.length() - paramInt - 1;
    StringBuilder localStringBuilder = new StringBuilder();
    if ((paramCharSequence.charAt(paramInt) == '\\') && (i > 0) && (isOctalDigit(paramCharSequence.charAt(paramInt + 1))))
    {
      int j = paramInt + 1;
      int k = paramInt + 2;
      int m = paramInt + 3;
      localStringBuilder.append(paramCharSequence.charAt(j));
      if ((i > 1) && (isOctalDigit(paramCharSequence.charAt(k))))
      {
        localStringBuilder.append(paramCharSequence.charAt(k));
        if ((i > 2) && (isZeroToThree(paramCharSequence.charAt(j))) && (isOctalDigit(paramCharSequence.charAt(m)))) {
          localStringBuilder.append(paramCharSequence.charAt(m));
        }
      }
      paramWriter.write(Integer.parseInt(localStringBuilder.toString(), 8));
      return 1 + localStringBuilder.length();
    }
    return 0;
  }
  
  private boolean isOctalDigit(char paramChar)
  {
    return (paramChar >= '0') && (paramChar <= '7');
  }
  
  private boolean isZeroToThree(char paramChar)
  {
    return (paramChar >= '0') && (paramChar <= '3');
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\OctalUnescaper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */