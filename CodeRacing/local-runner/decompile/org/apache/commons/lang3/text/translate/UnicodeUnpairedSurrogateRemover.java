package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

public class UnicodeUnpairedSurrogateRemover
  extends CodePointTranslator
{
  public boolean translate(int paramInt, Writer paramWriter)
    throws IOException
  {
    return (paramInt >= 55296) && (paramInt <= 57343);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\UnicodeUnpairedSurrogateRemover.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */