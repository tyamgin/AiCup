package org.apache.commons.lang3.mutable;

import java.io.Serializable;
import org.apache.commons.lang3.BooleanUtils;

public class MutableBoolean
  implements Serializable, Comparable
{
  private boolean value;
  
  public MutableBoolean() {}
  
  public MutableBoolean(boolean paramBoolean)
  {
    this.value = paramBoolean;
  }
  
  public void setValue(boolean paramBoolean)
  {
    this.value = paramBoolean;
  }
  
  public boolean booleanValue()
  {
    return this.value;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof MutableBoolean)) {
      return this.value == ((MutableBoolean)paramObject).booleanValue();
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
  }
  
  public int compareTo(MutableBoolean paramMutableBoolean)
  {
    return BooleanUtils.compare(this.value, paramMutableBoolean.value);
  }
  
  public String toString()
  {
    return String.valueOf(this.value);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\org\apache\commons\lang3\mutable\MutableBoolean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */