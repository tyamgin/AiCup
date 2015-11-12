package com.google.common.primitives;

import com.google.common.base.Preconditions;

final class AndroidInteger
{
  static Integer tryParse(String paramString)
  {
    return tryParse(paramString, 10);
  }
  
  static Integer tryParse(String paramString, int paramInt)
  {
    Preconditions.checkNotNull(paramString);
    Preconditions.checkArgument(paramInt >= 2, "Invalid radix %s, min radix is %s", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(2) });
    Preconditions.checkArgument(paramInt <= 36, "Invalid radix %s, max radix is %s", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(36) });
    int i = paramString.length();
    int j = 0;
    if (i == 0) {
      return null;
    }
    boolean bool = paramString.charAt(j) == '-';
    if (bool)
    {
      j++;
      if (j == i) {
        return null;
      }
    }
    return tryParse(paramString, j, paramInt, bool);
  }
  
  private static Integer tryParse(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = Integer.MIN_VALUE / paramInt2;
    int j = 0;
    int k = paramString.length();
    while (paramInt1 < k)
    {
      int m = Character.digit(paramString.charAt(paramInt1++), paramInt2);
      if (m == -1) {
        return null;
      }
      if (i > j) {
        return null;
      }
      int n = j * paramInt2 - m;
      if (n > j) {
        return null;
      }
      j = n;
    }
    if (!paramBoolean)
    {
      j = -j;
      if (j < 0) {
        return null;
      }
    }
    if ((j > Integer.MAX_VALUE) || (j < Integer.MIN_VALUE)) {
      return null;
    }
    return Integer.valueOf(j);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\AndroidInteger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */