package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible(emulated=true)
public final class Chars
{
  public static final int BYTES = 2;
  
  public static int hashCode(char paramChar)
  {
    return paramChar;
  }
  
  public static char checkedCast(long paramLong)
  {
    char c = (char)(int)paramLong;
    Preconditions.checkArgument(c == paramLong, "Out of range: %s", new Object[] { Long.valueOf(paramLong) });
    return c;
  }
  
  public static char saturatedCast(long paramLong)
  {
    if (paramLong > 65535L) {
      return 65535;
    }
    if (paramLong < 0L) {
      return '\000';
    }
    return (char)(int)paramLong;
  }
  
  public static int compare(char paramChar1, char paramChar2)
  {
    return paramChar1 - paramChar2;
  }
  
  public static boolean contains(char[] paramArrayOfChar, char paramChar)
  {
    for (char c : paramArrayOfChar) {
      if (c == paramChar) {
        return true;
      }
    }
    return false;
  }
  
  public static int indexOf(char[] paramArrayOfChar, char paramChar)
  {
    return indexOf(paramArrayOfChar, paramChar, 0, paramArrayOfChar.length);
  }
  
  private static int indexOf(char[] paramArrayOfChar, char paramChar, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramArrayOfChar[i] == paramChar) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(char[] paramArrayOfChar1, char[] paramArrayOfChar2)
  {
    Preconditions.checkNotNull(paramArrayOfChar1, "array");
    Preconditions.checkNotNull(paramArrayOfChar2, "target");
    if (paramArrayOfChar2.length == 0) {
      return 0;
    }
    label64:
    for (int i = 0; i < paramArrayOfChar1.length - paramArrayOfChar2.length + 1; i++)
    {
      for (int j = 0; j < paramArrayOfChar2.length; j++) {
        if (paramArrayOfChar1[(i + j)] != paramArrayOfChar2[j]) {
          break label64;
        }
      }
      return i;
    }
    return -1;
  }
  
  public static int lastIndexOf(char[] paramArrayOfChar, char paramChar)
  {
    return lastIndexOf(paramArrayOfChar, paramChar, 0, paramArrayOfChar.length);
  }
  
  private static int lastIndexOf(char[] paramArrayOfChar, char paramChar, int paramInt1, int paramInt2)
  {
    for (int i = paramInt2 - 1; i >= paramInt1; i--) {
      if (paramArrayOfChar[i] == paramChar) {
        return i;
      }
    }
    return -1;
  }
  
  public static char min(char... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    char c = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      if (paramVarArgs[i] < c) {
        c = paramVarArgs[i];
      }
    }
    return c;
  }
  
  public static char max(char... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    char c = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      if (paramVarArgs[i] > c) {
        c = paramVarArgs[i];
      }
    }
    return c;
  }
  
  public static char[] concat(char[]... paramVarArgs)
  {
    int i = 0;
    for (Object localObject2 : paramVarArgs) {
      i += localObject2.length;
    }
    ??? = new char[i];
    ??? = 0;
    for (char[] arrayOfChar1 : paramVarArgs)
    {
      System.arraycopy(arrayOfChar1, 0, ???, ???, arrayOfChar1.length);
      ??? += arrayOfChar1.length;
    }
    return (char[])???;
  }
  
  @GwtIncompatible("doesn't work")
  public static byte[] toByteArray(char paramChar)
  {
    return new byte[] { (byte)(paramChar >> '\b'), (byte)paramChar };
  }
  
  @GwtIncompatible("doesn't work")
  public static char fromByteArray(byte[] paramArrayOfByte)
  {
    Preconditions.checkArgument(paramArrayOfByte.length >= 2, "array too small: %s < %s", new Object[] { Integer.valueOf(paramArrayOfByte.length), Integer.valueOf(2) });
    return fromBytes(paramArrayOfByte[0], paramArrayOfByte[1]);
  }
  
  @GwtIncompatible("doesn't work")
  public static char fromBytes(byte paramByte1, byte paramByte2)
  {
    return (char)(paramByte1 << 8 | paramByte2 & 0xFF);
  }
  
  public static char[] ensureCapacity(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument(paramInt1 >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(paramInt1) });
    Preconditions.checkArgument(paramInt2 >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(paramInt2) });
    return paramArrayOfChar.length < paramInt1 ? copyOf(paramArrayOfChar, paramInt1 + paramInt2) : paramArrayOfChar;
  }
  
  private static char[] copyOf(char[] paramArrayOfChar, int paramInt)
  {
    char[] arrayOfChar = new char[paramInt];
    System.arraycopy(paramArrayOfChar, 0, arrayOfChar, 0, Math.min(paramArrayOfChar.length, paramInt));
    return arrayOfChar;
  }
  
  public static String join(String paramString, char... paramVarArgs)
  {
    Preconditions.checkNotNull(paramString);
    int i = paramVarArgs.length;
    if (i == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(i + paramString.length() * (i - 1));
    localStringBuilder.append(paramVarArgs[0]);
    for (int j = 1; j < i; j++) {
      localStringBuilder.append(paramString).append(paramVarArgs[j]);
    }
    return localStringBuilder.toString();
  }
  
  public static Comparator lexicographicalComparator()
  {
    return LexicographicalComparator.INSTANCE;
  }
  
  public static char[] toArray(Collection paramCollection)
  {
    if ((paramCollection instanceof CharArrayAsList)) {
      return ((CharArrayAsList)paramCollection).toCharArray();
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    char[] arrayOfChar = new char[i];
    for (int j = 0; j < i; j++) {
      arrayOfChar[j] = ((Character)Preconditions.checkNotNull(arrayOfObject[j])).charValue();
    }
    return arrayOfChar;
  }
  
  public static List asList(char... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return Collections.emptyList();
    }
    return new CharArrayAsList(paramVarArgs);
  }
  
  @GwtCompatible
  private static class CharArrayAsList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final char[] array;
    final int start;
    final int end;
    private static final long serialVersionUID = 0L;
    
    CharArrayAsList(char[] paramArrayOfChar)
    {
      this(paramArrayOfChar, 0, paramArrayOfChar.length);
    }
    
    CharArrayAsList(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    {
      this.array = paramArrayOfChar;
      this.start = paramInt1;
      this.end = paramInt2;
    }
    
    public int size()
    {
      return this.end - this.start;
    }
    
    public boolean isEmpty()
    {
      return false;
    }
    
    public Character get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return Character.valueOf(this.array[(this.start + paramInt)]);
    }
    
    public boolean contains(Object paramObject)
    {
      return ((paramObject instanceof Character)) && (Chars.indexOf(this.array, ((Character)paramObject).charValue(), this.start, this.end) != -1);
    }
    
    public int indexOf(Object paramObject)
    {
      if ((paramObject instanceof Character))
      {
        int i = Chars.indexOf(this.array, ((Character)paramObject).charValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      if ((paramObject instanceof Character))
      {
        int i = Chars.lastIndexOf(this.array, ((Character)paramObject).charValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public Character set(int paramInt, Character paramCharacter)
    {
      Preconditions.checkElementIndex(paramInt, size());
      char c = this.array[(this.start + paramInt)];
      this.array[(this.start + paramInt)] = ((Character)Preconditions.checkNotNull(paramCharacter)).charValue();
      return Character.valueOf(c);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      int i = size();
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, i);
      if (paramInt1 == paramInt2) {
        return Collections.emptyList();
      }
      return new CharArrayAsList(this.array, this.start + paramInt1, this.start + paramInt2);
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof CharArrayAsList))
      {
        CharArrayAsList localCharArrayAsList = (CharArrayAsList)paramObject;
        int i = size();
        if (localCharArrayAsList.size() != i) {
          return false;
        }
        for (int j = 0; j < i; j++) {
          if (this.array[(this.start + j)] != localCharArrayAsList.array[(localCharArrayAsList.start + j)]) {
            return false;
          }
        }
        return true;
      }
      return super.equals(paramObject);
    }
    
    public int hashCode()
    {
      int i = 1;
      for (int j = this.start; j < this.end; j++) {
        i = 31 * i + Chars.hashCode(this.array[j]);
      }
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(size() * 3);
      localStringBuilder.append('[').append(this.array[this.start]);
      for (int i = this.start + 1; i < this.end; i++) {
        localStringBuilder.append(", ").append(this.array[i]);
      }
      return ']';
    }
    
    char[] toCharArray()
    {
      int i = size();
      char[] arrayOfChar = new char[i];
      System.arraycopy(this.array, this.start, arrayOfChar, 0, i);
      return arrayOfChar;
    }
  }
  
  private static enum LexicographicalComparator
    implements Comparator
  {
    INSTANCE;
    
    public int compare(char[] paramArrayOfChar1, char[] paramArrayOfChar2)
    {
      int i = Math.min(paramArrayOfChar1.length, paramArrayOfChar2.length);
      for (int j = 0; j < i; j++)
      {
        int k = Chars.compare(paramArrayOfChar1[j], paramArrayOfChar2[j]);
        if (k != 0) {
          return k;
        }
      }
      return paramArrayOfChar1.length - paramArrayOfChar2.length;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\Chars.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */