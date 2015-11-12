package com.google.common.primitives;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

@GwtCompatible
public final class Booleans
{
  public static int hashCode(boolean paramBoolean)
  {
    return paramBoolean ? 1231 : 1237;
  }
  
  public static int compare(boolean paramBoolean1, boolean paramBoolean2)
  {
    return paramBoolean1 ? 1 : paramBoolean1 == paramBoolean2 ? 0 : -1;
  }
  
  public static boolean contains(boolean[] paramArrayOfBoolean, boolean paramBoolean)
  {
    for (boolean bool : paramArrayOfBoolean) {
      if (bool == paramBoolean) {
        return true;
      }
    }
    return false;
  }
  
  public static int indexOf(boolean[] paramArrayOfBoolean, boolean paramBoolean)
  {
    return indexOf(paramArrayOfBoolean, paramBoolean, 0, paramArrayOfBoolean.length);
  }
  
  private static int indexOf(boolean[] paramArrayOfBoolean, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramArrayOfBoolean[i] == paramBoolean) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
  {
    Preconditions.checkNotNull(paramArrayOfBoolean1, "array");
    Preconditions.checkNotNull(paramArrayOfBoolean2, "target");
    if (paramArrayOfBoolean2.length == 0) {
      return 0;
    }
    label64:
    for (int i = 0; i < paramArrayOfBoolean1.length - paramArrayOfBoolean2.length + 1; i++)
    {
      for (int j = 0; j < paramArrayOfBoolean2.length; j++) {
        if (paramArrayOfBoolean1[(i + j)] != paramArrayOfBoolean2[j]) {
          break label64;
        }
      }
      return i;
    }
    return -1;
  }
  
  public static int lastIndexOf(boolean[] paramArrayOfBoolean, boolean paramBoolean)
  {
    return lastIndexOf(paramArrayOfBoolean, paramBoolean, 0, paramArrayOfBoolean.length);
  }
  
  private static int lastIndexOf(boolean[] paramArrayOfBoolean, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    for (int i = paramInt2 - 1; i >= paramInt1; i--) {
      if (paramArrayOfBoolean[i] == paramBoolean) {
        return i;
      }
    }
    return -1;
  }
  
  public static boolean[] concat(boolean[]... paramVarArgs)
  {
    int i = 0;
    for (Object localObject2 : paramVarArgs) {
      i += localObject2.length;
    }
    ??? = new boolean[i];
    ??? = 0;
    for (boolean[] arrayOfBoolean1 : paramVarArgs)
    {
      System.arraycopy(arrayOfBoolean1, 0, ???, ???, arrayOfBoolean1.length);
      ??? += arrayOfBoolean1.length;
    }
    return (boolean[])???;
  }
  
  public static boolean[] ensureCapacity(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument(paramInt1 >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(paramInt1) });
    Preconditions.checkArgument(paramInt2 >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(paramInt2) });
    return paramArrayOfBoolean.length < paramInt1 ? copyOf(paramArrayOfBoolean, paramInt1 + paramInt2) : paramArrayOfBoolean;
  }
  
  private static boolean[] copyOf(boolean[] paramArrayOfBoolean, int paramInt)
  {
    boolean[] arrayOfBoolean = new boolean[paramInt];
    System.arraycopy(paramArrayOfBoolean, 0, arrayOfBoolean, 0, Math.min(paramArrayOfBoolean.length, paramInt));
    return arrayOfBoolean;
  }
  
  public static String join(String paramString, boolean... paramVarArgs)
  {
    Preconditions.checkNotNull(paramString);
    if (paramVarArgs.length == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(paramVarArgs.length * 7);
    localStringBuilder.append(paramVarArgs[0]);
    for (int i = 1; i < paramVarArgs.length; i++) {
      localStringBuilder.append(paramString).append(paramVarArgs[i]);
    }
    return localStringBuilder.toString();
  }
  
  public static Comparator lexicographicalComparator()
  {
    return LexicographicalComparator.INSTANCE;
  }
  
  public static boolean[] toArray(Collection paramCollection)
  {
    if ((paramCollection instanceof BooleanArrayAsList)) {
      return ((BooleanArrayAsList)paramCollection).toBooleanArray();
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    boolean[] arrayOfBoolean = new boolean[i];
    for (int j = 0; j < i; j++) {
      arrayOfBoolean[j] = ((Boolean)Preconditions.checkNotNull(arrayOfObject[j])).booleanValue();
    }
    return arrayOfBoolean;
  }
  
  public static List asList(boolean... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return Collections.emptyList();
    }
    return new BooleanArrayAsList(paramVarArgs);
  }
  
  @GwtCompatible
  private static class BooleanArrayAsList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final boolean[] array;
    final int start;
    final int end;
    private static final long serialVersionUID = 0L;
    
    BooleanArrayAsList(boolean[] paramArrayOfBoolean)
    {
      this(paramArrayOfBoolean, 0, paramArrayOfBoolean.length);
    }
    
    BooleanArrayAsList(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2)
    {
      this.array = paramArrayOfBoolean;
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
    
    public Boolean get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return Boolean.valueOf(this.array[(this.start + paramInt)]);
    }
    
    public boolean contains(Object paramObject)
    {
      return ((paramObject instanceof Boolean)) && (Booleans.indexOf(this.array, ((Boolean)paramObject).booleanValue(), this.start, this.end) != -1);
    }
    
    public int indexOf(Object paramObject)
    {
      if ((paramObject instanceof Boolean))
      {
        int i = Booleans.indexOf(this.array, ((Boolean)paramObject).booleanValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      if ((paramObject instanceof Boolean))
      {
        int i = Booleans.lastIndexOf(this.array, ((Boolean)paramObject).booleanValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public Boolean set(int paramInt, Boolean paramBoolean)
    {
      Preconditions.checkElementIndex(paramInt, size());
      int i = this.array[(this.start + paramInt)];
      this.array[(this.start + paramInt)] = ((Boolean)Preconditions.checkNotNull(paramBoolean)).booleanValue();
      return Boolean.valueOf(i);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      int i = size();
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, i);
      if (paramInt1 == paramInt2) {
        return Collections.emptyList();
      }
      return new BooleanArrayAsList(this.array, this.start + paramInt1, this.start + paramInt2);
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof BooleanArrayAsList))
      {
        BooleanArrayAsList localBooleanArrayAsList = (BooleanArrayAsList)paramObject;
        int i = size();
        if (localBooleanArrayAsList.size() != i) {
          return false;
        }
        for (int j = 0; j < i; j++) {
          if (this.array[(this.start + j)] != localBooleanArrayAsList.array[(localBooleanArrayAsList.start + j)]) {
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
        i = 31 * i + Booleans.hashCode(this.array[j]);
      }
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(size() * 7);
      localStringBuilder.append(this.array[this.start] != 0 ? "[true" : "[false");
      for (int i = this.start + 1; i < this.end; i++) {
        localStringBuilder.append(this.array[i] != 0 ? ", true" : ", false");
      }
      return ']';
    }
    
    boolean[] toBooleanArray()
    {
      int i = size();
      boolean[] arrayOfBoolean = new boolean[i];
      System.arraycopy(this.array, this.start, arrayOfBoolean, 0, i);
      return arrayOfBoolean;
    }
  }
  
  private static enum LexicographicalComparator
    implements Comparator
  {
    INSTANCE;
    
    public int compare(boolean[] paramArrayOfBoolean1, boolean[] paramArrayOfBoolean2)
    {
      int i = Math.min(paramArrayOfBoolean1.length, paramArrayOfBoolean2.length);
      for (int j = 0; j < i; j++)
      {
        int k = Booleans.compare(paramArrayOfBoolean1[j], paramArrayOfBoolean2[j]);
        if (k != 0) {
          return k;
        }
      }
      return paramArrayOfBoolean1.length - paramArrayOfBoolean2.length;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\Booleans.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */