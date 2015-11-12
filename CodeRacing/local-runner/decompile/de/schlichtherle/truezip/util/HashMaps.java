package de.schlichtherle.truezip.util;

public class HashMaps
{
  public static int initialCapacity(int paramInt)
  {
    if (paramInt < 47) {
      paramInt = 47;
    }
    long l = paramInt * 4L / 3L + 1L;
    return 2147483647L >= l ? powerOfTwo((int)l) : Integer.MAX_VALUE;
  }
  
  private static int powerOfTwo(int paramInt)
  {
    paramInt--;
    paramInt |= paramInt >>> 1;
    paramInt |= paramInt >>> 2;
    paramInt |= paramInt >>> 4;
    paramInt |= paramInt >>> 8;
    paramInt |= paramInt >>> 16;
    return paramInt + 1;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\HashMaps.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */