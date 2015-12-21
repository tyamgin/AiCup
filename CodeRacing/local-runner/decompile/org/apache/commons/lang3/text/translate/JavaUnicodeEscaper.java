package org.apache.commons.lang3.text.translate;

public class JavaUnicodeEscaper
  extends UnicodeEscaper
{
  public static JavaUnicodeEscaper outsideOf(int paramInt1, int paramInt2)
  {
    return new JavaUnicodeEscaper(paramInt1, paramInt2, false);
  }
  
  public JavaUnicodeEscaper(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramInt1, paramInt2, paramBoolean);
  }
  
  protected String toUtf16Escape(int paramInt)
  {
    char[] arrayOfChar = Character.toChars(paramInt);
    return "\\u" + hex(arrayOfChar[0]) + "\\u" + hex(arrayOfChar[1]);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\text\translate\JavaUnicodeEscaper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */