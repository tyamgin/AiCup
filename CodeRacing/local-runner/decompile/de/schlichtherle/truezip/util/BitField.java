package de.schlichtherle.truezip.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public final class BitField
  implements Serializable, Iterable
{
  private final EnumSet bits;
  
  public static BitField noneOf(Class paramClass)
  {
    return new BitField(paramClass, false);
  }
  
  public static BitField of(Enum paramEnum)
  {
    return new BitField(paramEnum);
  }
  
  public static BitField of(Enum paramEnum, Enum... paramVarArgs)
  {
    return new BitField(paramEnum, paramVarArgs);
  }
  
  public static BitField copyOf(Collection paramCollection)
  {
    return new BitField(EnumSet.copyOf(paramCollection));
  }
  
  private BitField(Class paramClass, boolean paramBoolean)
  {
    this.bits = (paramBoolean ? EnumSet.allOf(paramClass) : EnumSet.noneOf(paramClass));
  }
  
  private BitField(Enum paramEnum)
  {
    this.bits = EnumSet.of(paramEnum);
  }
  
  private BitField(Enum paramEnum, Enum... paramVarArgs)
  {
    this.bits = EnumSet.of(paramEnum, paramVarArgs);
  }
  
  private BitField(EnumSet paramEnumSet)
  {
    assert (null != paramEnumSet);
    this.bits = paramEnumSet;
  }
  
  public boolean get(Enum paramEnum)
  {
    return this.bits.contains(paramEnum);
  }
  
  public BitField set(Enum paramEnum, boolean paramBoolean)
  {
    EnumSet localEnumSet;
    if (paramBoolean)
    {
      if (this.bits.contains(paramEnum)) {
        return this;
      }
      localEnumSet = this.bits.clone();
      localEnumSet.add(paramEnum);
    }
    else
    {
      if (!this.bits.contains(paramEnum)) {
        return this;
      }
      localEnumSet = this.bits.clone();
      localEnumSet.remove(paramEnum);
    }
    return new BitField(localEnumSet);
  }
  
  public BitField set(Enum paramEnum)
  {
    return set(paramEnum, true);
  }
  
  public BitField clear(Enum paramEnum)
  {
    return set(paramEnum, false);
  }
  
  public BitField not()
  {
    return new BitField(EnumSet.complementOf(this.bits));
  }
  
  public Iterator iterator()
  {
    return Collections.unmodifiableSet(this.bits).iterator();
  }
  
  public String toString()
  {
    int i = this.bits.size() * 11;
    if (0 >= i) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(i);
    Iterator localIterator = this.bits.iterator();
    while (localIterator.hasNext())
    {
      Enum localEnum = (Enum)localIterator.next();
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append('|');
      }
      localStringBuilder.append(localEnum);
    }
    return localStringBuilder.toString();
  }
  
  public boolean equals(Object paramObject)
  {
    return (this == paramObject) || (((paramObject instanceof BitField)) && (this.bits.equals(((BitField)paramObject).bits)));
  }
  
  public int hashCode()
  {
    return this.bits.hashCode();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\util\BitField.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */