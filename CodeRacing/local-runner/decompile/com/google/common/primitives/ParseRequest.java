package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class ParseRequest
{
  final String rawValue;
  final int radix;
  
  private ParseRequest(String paramString, int paramInt)
  {
    this.rawValue = paramString;
    this.radix = paramInt;
  }
  
  static ParseRequest fromString(String paramString)
  {
    if (paramString.length() == 0) {
      throw new NumberFormatException("empty string");
    }
    int j = paramString.charAt(0);
    String str;
    int i;
    if ((paramString.startsWith("0x")) || (paramString.startsWith("0X")))
    {
      str = paramString.substring(2);
      i = 16;
    }
    else if (j == 35)
    {
      str = paramString.substring(1);
      i = 16;
    }
    else if ((j == 48) && (paramString.length() > 1))
    {
      str = paramString.substring(1);
      i = 8;
    }
    else
    {
      str = paramString;
      i = 10;
    }
    return new ParseRequest(str, i);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\ParseRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */