package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

public abstract class CharSequenceTranslator
{
  static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  public abstract int translate(CharSequence paramCharSequence, int paramInt, Writer paramWriter)
    throws IOException;
  
  public final String translate(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null) {
      return null;
    }
    try
    {
      StringWriter localStringWriter = new StringWriter(paramCharSequence.length() * 2);
      translate(paramCharSequence, localStringWriter);
      return localStringWriter.toString();
    }
    catch (IOException localIOException)
    {
      throw new RuntimeException(localIOException);
    }
  }
  
  public final void translate(CharSequence paramCharSequence, Writer paramWriter)
    throws IOException
  {
    if (paramWriter == null) {
      throw new IllegalArgumentException("The Writer must not be null");
    }
    if (paramCharSequence == null) {
      return;
    }
    int i = 0;
    int j = paramCharSequence.length();
    while (i < j)
    {
      int k = translate(paramCharSequence, i, paramWriter);
      int m;
      if (k == 0)
      {
        m = paramCharSequence.charAt(i);
        paramWriter.write(m);
        i++;
        if ((Character.isHighSurrogate(m)) && (i < j))
        {
          char c = paramCharSequence.charAt(i);
          if (Character.isLowSurrogate(c))
          {
            paramWriter.write(c);
            i++;
          }
        }
      }
      else
      {
        for (m = 0; m < k; m++) {
          i += Character.charCount(Character.codePointAt(paramCharSequence, i));
        }
      }
    }
  }
  
  public final CharSequenceTranslator with(CharSequenceTranslator... paramVarArgs)
  {
    CharSequenceTranslator[] arrayOfCharSequenceTranslator = new CharSequenceTranslator[paramVarArgs.length + 1];
    arrayOfCharSequenceTranslator[0] = this;
    System.arraycopy(paramVarArgs, 0, arrayOfCharSequenceTranslator, 1, paramVarArgs.length);
    return new AggregateTranslator(arrayOfCharSequenceTranslator);
  }
  
  public static String hex(int paramInt)
  {
    return Integer.toHexString(paramInt).toUpperCase(Locale.ENGLISH);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\CharSequenceTranslator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */