package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.EnumSet;

public class NumericEntityUnescaper
  extends CharSequenceTranslator
{
  private final EnumSet options;
  
  public NumericEntityUnescaper(OPTION... paramVarArgs)
  {
    if (paramVarArgs.length > 0) {
      this.options = EnumSet.copyOf(Arrays.asList(paramVarArgs));
    } else {
      this.options = EnumSet.copyOf(Arrays.asList(new OPTION[] { OPTION.semiColonRequired }));
    }
  }
  
  public boolean isSet(OPTION paramOPTION)
  {
    return this.options == null ? false : this.options.contains(paramOPTION);
  }
  
  public int translate(CharSequence paramCharSequence, int paramInt, Writer paramWriter)
    throws IOException
  {
    int i = paramCharSequence.length();
    if ((paramCharSequence.charAt(paramInt) == '&') && (paramInt < i - 2) && (paramCharSequence.charAt(paramInt + 1) == '#'))
    {
      int j = paramInt + 2;
      int k = 0;
      int m = paramCharSequence.charAt(j);
      if ((m == 120) || (m == 88))
      {
        j++;
        k = 1;
        if (j == i) {
          return 0;
        }
      }
      for (int n = j; (n < i) && (((paramCharSequence.charAt(n) >= '0') && (paramCharSequence.charAt(n) <= '9')) || ((paramCharSequence.charAt(n) >= 'a') && (paramCharSequence.charAt(n) <= 'f')) || ((paramCharSequence.charAt(n) >= 'A') && (paramCharSequence.charAt(n) <= 'F'))); n++) {}
      int i1 = (n != i) && (paramCharSequence.charAt(n) == ';') ? 1 : 0;
      if (i1 == 0)
      {
        if (isSet(OPTION.semiColonRequired)) {
          return 0;
        }
        if (isSet(OPTION.errorIfNoSemiColon)) {
          throw new IllegalArgumentException("Semi-colon required at end of numeric entity");
        }
      }
      int i2;
      try
      {
        if (k != 0) {
          i2 = Integer.parseInt(paramCharSequence.subSequence(j, n).toString(), 16);
        } else {
          i2 = Integer.parseInt(paramCharSequence.subSequence(j, n).toString(), 10);
        }
      }
      catch (NumberFormatException localNumberFormatException)
      {
        return 0;
      }
      if (i2 > 65535)
      {
        char[] arrayOfChar = Character.toChars(i2);
        paramWriter.write(arrayOfChar[0]);
        paramWriter.write(arrayOfChar[1]);
      }
      else
      {
        paramWriter.write(i2);
      }
      return 2 + n - j + (k != 0 ? 1 : 0) + (i1 != 0 ? 1 : 0);
    }
    return 0;
  }
  
  public static enum OPTION
  {
    semiColonRequired,  semiColonOptional,  errorIfNoSemiColon;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\NumericEntityUnescaper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */