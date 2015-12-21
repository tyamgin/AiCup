package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableInt
  extends Number
  implements Comparable
{
  private int value;
  
  public MutableInt() {}
  
  public MutableInt(int paramInt)
  {
    this.value = paramInt;
  }
  
  public void increment()
  {
    this.value += 1;
  }
  
  public void add(int paramInt)
  {
    this.value += paramInt;
  }
  
  public int intValue()
  {
    return this.value;
  }
  
  public long longValue()
  {
    return this.value;
  }
  
  public float floatValue()
  {
    return this.value;
  }
  
  public double doubleValue()
  {
    return this.value;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof MutableInt)) {
      return this.value == ((MutableInt)paramObject).intValue();
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.value;
  }
  
  public int compareTo(MutableInt paramMutableInt)
  {
    return NumberUtils.compare(this.value, paramMutableInt.value);
  }
  
  public String toString()
  {
    return String.valueOf(this.value);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\mutable\MutableInt.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */