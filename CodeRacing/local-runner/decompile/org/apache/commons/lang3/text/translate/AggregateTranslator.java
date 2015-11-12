package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.ArrayUtils;

public class AggregateTranslator
  extends CharSequenceTranslator
{
  private final CharSequenceTranslator[] translators;
  
  public AggregateTranslator(CharSequenceTranslator... paramVarArgs)
  {
    this.translators = ((CharSequenceTranslator[])ArrayUtils.clone(paramVarArgs));
  }
  
  public int translate(CharSequence paramCharSequence, int paramInt, Writer paramWriter)
    throws IOException
  {
    for (CharSequenceTranslator localCharSequenceTranslator : this.translators)
    {
      int k = localCharSequenceTranslator.translate(paramCharSequence, paramInt, paramWriter);
      if (k != 0) {
        return k;
      }
    }
    return 0;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\AggregateTranslator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */