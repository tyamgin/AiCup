package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

public class UnicodeUnescaper
  extends CharSequenceTranslator
{
  public int translate(CharSequence paramCharSequence, int paramInt, Writer paramWriter)
    throws IOException
  {
    if ((paramCharSequence.charAt(paramInt) == '\\') && (paramInt + 1 < paramCharSequence.length()) && (paramCharSequence.charAt(paramInt + 1) == 'u'))
    {
      for (int i = 2; (paramInt + i < paramCharSequence.length()) && (paramCharSequence.charAt(paramInt + i) == 'u'); i++) {}
      if ((paramInt + i < paramCharSequence.length()) && (paramCharSequence.charAt(paramInt + i) == '+')) {
        i++;
      }
      if (paramInt + i + 4 <= paramCharSequence.length())
      {
        CharSequence localCharSequence = paramCharSequence.subSequence(paramInt + i, paramInt + i + 4);
        try
        {
          int j = Integer.parseInt(localCharSequence.toString(), 16);
          paramWriter.write((char)j);
        }
        catch (NumberFormatException localNumberFormatException)
        {
          throw new IllegalArgumentException("Unable to parse unicode value: " + localCharSequence, localNumberFormatException);
        }
        return i + 4;
      }
      throw new IllegalArgumentException("Less than 4 hex digits in unicode value: '" + paramCharSequence.subSequence(paramInt, paramCharSequence.length()) + "' due to end of CharSequence");
    }
    return 0;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\UnicodeUnescaper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */