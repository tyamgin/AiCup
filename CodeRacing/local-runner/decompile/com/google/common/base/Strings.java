package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;

@GwtCompatible
public final class Strings
{
  public static String nullToEmpty(String paramString)
  {
    return paramString == null ? "" : paramString;
  }
  
  public static String emptyToNull(String paramString)
  {
    return isNullOrEmpty(paramString) ? null : paramString;
  }
  
  public static boolean isNullOrEmpty(String paramString)
  {
    return (paramString == null) || (paramString.length() == 0);
  }
  
  public static String padStart(String paramString, int paramInt, char paramChar)
  {
    Preconditions.checkNotNull(paramString);
    if (paramString.length() >= paramInt) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder(paramInt);
    for (int i = paramString.length(); i < paramInt; i++) {
      localStringBuilder.append(paramChar);
    }
    localStringBuilder.append(paramString);
    return localStringBuilder.toString();
  }
  
  public static String padEnd(String paramString, int paramInt, char paramChar)
  {
    Preconditions.checkNotNull(paramString);
    if (paramString.length() >= paramInt) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder(paramInt);
    localStringBuilder.append(paramString);
    for (int i = paramString.length(); i < paramInt; i++) {
      localStringBuilder.append(paramChar);
    }
    return localStringBuilder.toString();
  }
  
  public static String repeat(String paramString, int paramInt)
  {
    Preconditions.checkNotNull(paramString);
    if (paramInt <= 1)
    {
      Preconditions.checkArgument(paramInt >= 0, "invalid count: %s", new Object[] { Integer.valueOf(paramInt) });
      return paramInt == 0 ? "" : paramString;
    }
    int i = paramString.length();
    long l = i * paramInt;
    int j = (int)l;
    if (j != l) {
      throw new ArrayIndexOutOfBoundsException("Required array size too large: " + String.valueOf(l));
    }
    char[] arrayOfChar = new char[j];
    paramString.getChars(0, i, arrayOfChar, 0);
    int k = i;
    while (k < j - k)
    {
      System.arraycopy(arrayOfChar, 0, arrayOfChar, k, k);
      k <<= 1;
    }
    System.arraycopy(arrayOfChar, 0, arrayOfChar, k, j - k);
    return new String(arrayOfChar);
  }
  
  public static String commonPrefix(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    Preconditions.checkNotNull(paramCharSequence1);
    Preconditions.checkNotNull(paramCharSequence2);
    int i = Math.min(paramCharSequence1.length(), paramCharSequence2.length());
    for (int j = 0; (j < i) && (paramCharSequence1.charAt(j) == paramCharSequence2.charAt(j)); j++) {}
    if ((validSurrogatePairAt(paramCharSequence1, j - 1)) || (validSurrogatePairAt(paramCharSequence2, j - 1))) {
      j--;
    }
    return paramCharSequence1.subSequence(0, j).toString();
  }
  
  public static String commonSuffix(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    Preconditions.checkNotNull(paramCharSequence1);
    Preconditions.checkNotNull(paramCharSequence2);
    int i = Math.min(paramCharSequence1.length(), paramCharSequence2.length());
    for (int j = 0; (j < i) && (paramCharSequence1.charAt(paramCharSequence1.length() - j - 1) == paramCharSequence2.charAt(paramCharSequence2.length() - j - 1)); j++) {}
    if ((validSurrogatePairAt(paramCharSequence1, paramCharSequence1.length() - j - 1)) || (validSurrogatePairAt(paramCharSequence2, paramCharSequence2.length() - j - 1))) {
      j--;
    }
    return paramCharSequence1.subSequence(paramCharSequence1.length() - j, paramCharSequence1.length()).toString();
  }
  
  @VisibleForTesting
  static boolean validSurrogatePairAt(CharSequence paramCharSequence, int paramInt)
  {
    return (paramInt >= 0) && (paramInt <= paramCharSequence.length() - 2) && (Character.isHighSurrogate(paramCharSequence.charAt(paramInt))) && (Character.isLowSurrogate(paramCharSequence.charAt(paramInt + 1)));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Strings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */