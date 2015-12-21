package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
final class Hashing
{
  private static final int C1 = -862048943;
  private static final int C2 = 461845907;
  static int MAX_TABLE_SIZE = 1073741824;
  
  static int smear(int paramInt)
  {
    return 461845907 * Integer.rotateLeft(paramInt * -862048943, 15);
  }
  
  static int closedTableSize(int paramInt, double paramDouble)
  {
    paramInt = Math.max(paramInt, 2);
    int i = Integer.highestOneBit(paramInt);
    if (paramInt / i > paramDouble)
    {
      i <<= 1;
      return i > 0 ? i : MAX_TABLE_SIZE;
    }
    return i;
  }
  
  static boolean needsResizing(int paramInt1, int paramInt2, double paramDouble)
  {
    return (paramInt1 > paramDouble * paramInt2) && (paramInt2 < MAX_TABLE_SIZE);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Hashing.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */