package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

public class NumericEntityEscaper
  extends CodePointTranslator
{
  private final int below;
  private final int above;
  private final boolean between;
  
  private NumericEntityEscaper(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.below = paramInt1;
    this.above = paramInt2;
    this.between = paramBoolean;
  }
  
  public NumericEntityEscaper()
  {
    this(0, Integer.MAX_VALUE, true);
  }
  
  public static NumericEntityEscaper between(int paramInt1, int paramInt2)
  {
    return new NumericEntityEscaper(paramInt1, paramInt2, true);
  }
  
  public boolean translate(int paramInt, Writer paramWriter)
    throws IOException
  {
    if (this.between)
    {
      if ((paramInt < this.below) || (paramInt > this.above)) {
        return false;
      }
    }
    else if ((paramInt >= this.below) && (paramInt <= this.above)) {
      return false;
    }
    paramWriter.write("&#");
    paramWriter.write(Integer.toString(paramInt, 10));
    paramWriter.write(59);
    return true;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\NumericEntityEscaper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */