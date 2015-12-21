package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import java.util.BitSet;

@GwtIncompatible("no precomputation is done in GWT")
final class SmallCharMatcher
  extends CharMatcher.FastMatcher
{
  static final int MAX_SIZE = 1023;
  private final char[] table;
  private final boolean containsZero;
  private final long filter;
  private static final int C1 = -862048943;
  private static final int C2 = 461845907;
  private static final double DESIRED_LOAD_FACTOR = 0.5D;
  
  private SmallCharMatcher(char[] paramArrayOfChar, long paramLong, boolean paramBoolean, String paramString)
  {
    super(paramString);
    this.table = paramArrayOfChar;
    this.filter = paramLong;
    this.containsZero = paramBoolean;
  }
  
  static int smear(int paramInt)
  {
    return 461845907 * Integer.rotateLeft(paramInt * -862048943, 15);
  }
  
  private boolean checkFilter(int paramInt)
  {
    return 1L == (1L & this.filter >> paramInt);
  }
  
  @VisibleForTesting
  static int chooseTableSize(int paramInt)
  {
    if (paramInt == 1) {
      return 2;
    }
    int i = Integer.highestOneBit(paramInt - 1) << 1;
    while (i * 0.5D < paramInt) {
      i <<= 1;
    }
    return i;
  }
  
  @GwtIncompatible("java.util.BitSet")
  static CharMatcher from(BitSet paramBitSet, String paramString)
  {
    long l = 0L;
    int i = paramBitSet.cardinality();
    boolean bool = paramBitSet.get(0);
    char[] arrayOfChar = new char[chooseTableSize(i)];
    int j = arrayOfChar.length - 1;
    for (int k = paramBitSet.nextSetBit(0); k != -1; k = paramBitSet.nextSetBit(k + 1))
    {
      l |= 1L << k;
      for (int m = smear(k) & j;; m = m + 1 & j) {
        if (arrayOfChar[m] == 0)
        {
          arrayOfChar[m] = ((char)k);
          break;
        }
      }
    }
    return new SmallCharMatcher(arrayOfChar, l, bool, paramString);
  }
  
  public boolean matches(char paramChar)
  {
    if (paramChar == 0) {
      return this.containsZero;
    }
    if (!checkFilter(paramChar)) {
      return false;
    }
    int i = this.table.length - 1;
    int j = smear(paramChar) & i;
    int k = j;
    do
    {
      if (this.table[k] == 0) {
        return false;
      }
      if (this.table[k] == paramChar) {
        return true;
      }
      k = k + 1 & i;
    } while (k != j);
    return false;
  }
  
  @GwtIncompatible("java.util.BitSet")
  void setBits(BitSet paramBitSet)
  {
    if (this.containsZero) {
      paramBitSet.set(0);
    }
    for (int k : this.table) {
      if (k != 0) {
        paramBitSet.set(k);
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\SmallCharMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */