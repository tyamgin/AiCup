package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

public class UnicodeEscaper
  extends CodePointTranslator
{
  private final int below;
  private final int above;
  private final boolean between;
  
  protected UnicodeEscaper(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.below = paramInt1;
    this.above = paramInt2;
    this.between = paramBoolean;
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
    if (paramInt > 65535)
    {
      paramWriter.write(toUtf16Escape(paramInt));
    }
    else
    {
      paramWriter.write("\\u");
      paramWriter.write(HEX_DIGITS[(paramInt >> 12 & 0xF)]);
      paramWriter.write(HEX_DIGITS[(paramInt >> 8 & 0xF)]);
      paramWriter.write(HEX_DIGITS[(paramInt >> 4 & 0xF)]);
      paramWriter.write(HEX_DIGITS[(paramInt & 0xF)]);
    }
    return true;
  }
  
  protected String toUtf16Escape(int paramInt)
  {
    return "\\u" + hex(paramInt);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\UnicodeEscaper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */