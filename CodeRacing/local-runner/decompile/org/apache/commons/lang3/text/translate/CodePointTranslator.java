package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

public abstract class CodePointTranslator
  extends CharSequenceTranslator
{
  public final int translate(CharSequence paramCharSequence, int paramInt, Writer paramWriter)
    throws IOException
  {
    int i = Character.codePointAt(paramCharSequence, paramInt);
    boolean bool = translate(i, paramWriter);
    return bool ? 1 : 0;
  }
  
  public abstract boolean translate(int paramInt, Writer paramWriter)
    throws IOException;
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\CodePointTranslator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */