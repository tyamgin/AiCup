package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;

@GwtCompatible
final class Count
  implements Serializable
{
  private int value;
  
  Count(int paramInt)
  {
    this.value = paramInt;
  }
  
  public int get()
  {
    return this.value;
  }
  
  public int getAndAdd(int paramInt)
  {
    int i = this.value;
    this.value = (i + paramInt);
    return i;
  }
  
  public int addAndGet(int paramInt)
  {
    return this.value += paramInt;
  }
  
  public void set(int paramInt)
  {
    this.value = paramInt;
  }
  
  public int getAndSet(int paramInt)
  {
    int i = this.value;
    this.value = paramInt;
    return i;
  }
  
  public int hashCode()
  {
    return this.value;
  }
  
  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof Count)) && (((Count)paramObject).value == this.value);
  }
  
  public String toString()
  {
    return Integer.toString(this.value);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\Count.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */