package com.google.common.hash;

import java.nio.charset.Charset;

abstract class AbstractHasher
  implements Hasher
{
  public final Hasher putBoolean(boolean paramBoolean)
  {
    return putByte((byte)(paramBoolean ? 1 : 0));
  }
  
  public final Hasher putDouble(double paramDouble)
  {
    return putLong(Double.doubleToRawLongBits(paramDouble));
  }
  
  public final Hasher putFloat(float paramFloat)
  {
    return putInt(Float.floatToRawIntBits(paramFloat));
  }
  
  public Hasher putString(CharSequence paramCharSequence)
  {
    int i = 0;
    int j = paramCharSequence.length();
    while (i < j)
    {
      putChar(paramCharSequence.charAt(i));
      i++;
    }
    return this;
  }
  
  public Hasher putString(CharSequence paramCharSequence, Charset paramCharset)
  {
    return putBytes(paramCharSequence.toString().getBytes(paramCharset));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\AbstractHasher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */