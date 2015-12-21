package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;

public class LookupTranslator
  extends CharSequenceTranslator
{
  private final HashMap lookupMap = new HashMap();
  private final HashSet prefixSet = new HashSet();
  private final int shortest;
  private final int longest;
  
  public LookupTranslator(CharSequence[]... paramVarArgs)
  {
    int i = Integer.MAX_VALUE;
    int j = 0;
    if (paramVarArgs != null) {
      for (CharSequence[] arrayOfCharSequence1 : paramVarArgs)
      {
        this.lookupMap.put(arrayOfCharSequence1[0].toString(), arrayOfCharSequence1[1].toString());
        this.prefixSet.add(Character.valueOf(arrayOfCharSequence1[0].charAt(0)));
        int n = arrayOfCharSequence1[0].length();
        if (n < i) {
          i = n;
        }
        if (n > j) {
          j = n;
        }
      }
    }
    this.shortest = i;
    this.longest = j;
  }
  
  public int translate(CharSequence paramCharSequence, int paramInt, Writer paramWriter)
    throws IOException
  {
    if (this.prefixSet.contains(Character.valueOf(paramCharSequence.charAt(paramInt))))
    {
      int i = this.longest;
      if (paramInt + this.longest > paramCharSequence.length()) {
        i = paramCharSequence.length() - paramInt;
      }
      for (int j = i; j >= this.shortest; j--)
      {
        CharSequence localCharSequence = paramCharSequence.subSequence(paramInt, paramInt + j);
        String str = (String)this.lookupMap.get(localCharSequence.toString());
        if (str != null)
        {
          paramWriter.write(str);
          return j;
        }
      }
    }
    return 0;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\LookupTranslator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */