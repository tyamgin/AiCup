package com.google.common.primitives;

import com.google.common.annotations.Beta;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@GwtCompatible(emulated=true)
public final class Floats
{
  public static final int BYTES = 4;
  
  public static int hashCode(float paramFloat)
  {
    return Float.valueOf(paramFloat).hashCode();
  }
  
  public static int compare(float paramFloat1, float paramFloat2)
  {
    return Float.compare(paramFloat1, paramFloat2);
  }
  
  public static boolean isFinite(float paramFloat)
  {
    return (Float.NEGATIVE_INFINITY < paramFloat ? 1 : 0) & (paramFloat < Float.POSITIVE_INFINITY ? 1 : 0);
  }
  
  public static boolean contains(float[] paramArrayOfFloat, float paramFloat)
  {
    for (float f : paramArrayOfFloat) {
      if (f == paramFloat) {
        return true;
      }
    }
    return false;
  }
  
  public static int indexOf(float[] paramArrayOfFloat, float paramFloat)
  {
    return indexOf(paramArrayOfFloat, paramFloat, 0, paramArrayOfFloat.length);
  }
  
  private static int indexOf(float[] paramArrayOfFloat, float paramFloat, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramArrayOfFloat[i] == paramFloat) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
  {
    Preconditions.checkNotNull(paramArrayOfFloat1, "array");
    Preconditions.checkNotNull(paramArrayOfFloat2, "target");
    if (paramArrayOfFloat2.length == 0) {
      return 0;
    }
    label65:
    for (int i = 0; i < paramArrayOfFloat1.length - paramArrayOfFloat2.length + 1; i++)
    {
      for (int j = 0; j < paramArrayOfFloat2.length; j++) {
        if (paramArrayOfFloat1[(i + j)] != paramArrayOfFloat2[j]) {
          break label65;
        }
      }
      return i;
    }
    return -1;
  }
  
  public static int lastIndexOf(float[] paramArrayOfFloat, float paramFloat)
  {
    return lastIndexOf(paramArrayOfFloat, paramFloat, 0, paramArrayOfFloat.length);
  }
  
  private static int lastIndexOf(float[] paramArrayOfFloat, float paramFloat, int paramInt1, int paramInt2)
  {
    for (int i = paramInt2 - 1; i >= paramInt1; i--) {
      if (paramArrayOfFloat[i] == paramFloat) {
        return i;
      }
    }
    return -1;
  }
  
  public static float min(float... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    float f = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      f = Math.min(f, paramVarArgs[i]);
    }
    return f;
  }
  
  public static float max(float... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    float f = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      f = Math.max(f, paramVarArgs[i]);
    }
    return f;
  }
  
  public static float[] concat(float[]... paramVarArgs)
  {
    int i = 0;
    for (Object localObject2 : paramVarArgs) {
      i += localObject2.length;
    }
    ??? = new float[i];
    ??? = 0;
    for (float[] arrayOfFloat1 : paramVarArgs)
    {
      System.arraycopy(arrayOfFloat1, 0, ???, ???, arrayOfFloat1.length);
      ??? += arrayOfFloat1.length;
    }
    return (float[])???;
  }
  
  public static float[] ensureCapacity(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument(paramInt1 >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(paramInt1) });
    Preconditions.checkArgument(paramInt2 >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(paramInt2) });
    return paramArrayOfFloat.length < paramInt1 ? copyOf(paramArrayOfFloat, paramInt1 + paramInt2) : paramArrayOfFloat;
  }
  
  private static float[] copyOf(float[] paramArrayOfFloat, int paramInt)
  {
    float[] arrayOfFloat = new float[paramInt];
    System.arraycopy(paramArrayOfFloat, 0, arrayOfFloat, 0, Math.min(paramArrayOfFloat.length, paramInt));
    return arrayOfFloat;
  }
  
  public static String join(String paramString, float... paramVarArgs)
  {
    Preconditions.checkNotNull(paramString);
    if (paramVarArgs.length == 0) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder(paramVarArgs.length * 12);
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
  
  public static float[] toArray(Collection paramCollection)
  {
    if ((paramCollection instanceof FloatArrayAsList)) {
      return ((FloatArrayAsList)paramCollection).toFloatArray();
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    float[] arrayOfFloat = new float[i];
    for (int j = 0; j < i; j++) {
      arrayOfFloat[j] = ((Number)Preconditions.checkNotNull(arrayOfObject[j])).floatValue();
    }
    return arrayOfFloat;
  }
  
  public static List asList(float... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return Collections.emptyList();
    }
    return new FloatArrayAsList(paramVarArgs);
  }
  
  @GwtIncompatible("regular expressions")
  @Beta
  public static Float tryParse(String paramString)
  {
    if (Doubles.FLOATING_POINT_PATTERN.matcher(paramString).matches()) {
      try
      {
        return Float.valueOf(Float.parseFloat(paramString));
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return null;
  }
  
  @GwtCompatible
  private static class FloatArrayAsList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final float[] array;
    final int start;
    final int end;
    private static final long serialVersionUID = 0L;
    
    FloatArrayAsList(float[] paramArrayOfFloat)
    {
      this(paramArrayOfFloat, 0, paramArrayOfFloat.length);
    }
    
    FloatArrayAsList(float[] paramArrayOfFloat, int paramInt1, int paramInt2)
    {
      this.array = paramArrayOfFloat;
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
    
    public Float get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return Float.valueOf(this.array[(this.start + paramInt)]);
    }
    
    public boolean contains(Object paramObject)
    {
      return ((paramObject instanceof Float)) && (Floats.indexOf(this.array, ((Float)paramObject).floatValue(), this.start, this.end) != -1);
    }
    
    public int indexOf(Object paramObject)
    {
      if ((paramObject instanceof Float))
      {
        int i = Floats.indexOf(this.array, ((Float)paramObject).floatValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      if ((paramObject instanceof Float))
      {
        int i = Floats.lastIndexOf(this.array, ((Float)paramObject).floatValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public Float set(int paramInt, Float paramFloat)
    {
      Preconditions.checkElementIndex(paramInt, size());
      float f = this.array[(this.start + paramInt)];
      this.array[(this.start + paramInt)] = ((Float)Preconditions.checkNotNull(paramFloat)).floatValue();
      return Float.valueOf(f);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      int i = size();
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, i);
      if (paramInt1 == paramInt2) {
        return Collections.emptyList();
      }
      return new FloatArrayAsList(this.array, this.start + paramInt1, this.start + paramInt2);
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof FloatArrayAsList))
      {
        FloatArrayAsList localFloatArrayAsList = (FloatArrayAsList)paramObject;
        int i = size();
        if (localFloatArrayAsList.size() != i) {
          return false;
        }
        for (int j = 0; j < i; j++) {
          if (this.array[(this.start + j)] != localFloatArrayAsList.array[(localFloatArrayAsList.start + j)]) {
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
        i = 31 * i + Floats.hashCode(this.array[j]);
      }
      return i;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(size() * 12);
      localStringBuilder.append('[').append(this.array[this.start]);
      for (int i = this.start + 1; i < this.end; i++) {
        localStringBuilder.append(", ").append(this.array[i]);
      }
      return ']';
    }
    
    float[] toFloatArray()
    {
      int i = size();
      float[] arrayOfFloat = new float[i];
      System.arraycopy(this.array, this.start, arrayOfFloat, 0, i);
      return arrayOfFloat;
    }
  }
  
  private static enum LexicographicalComparator
    implements Comparator
  {
    INSTANCE;
    
    public int compare(float[] paramArrayOfFloat1, float[] paramArrayOfFloat2)
    {
      int i = Math.min(paramArrayOfFloat1.length, paramArrayOfFloat2.length);
      for (int j = 0; j < i; j++)
      {
        int k = Floats.compare(paramArrayOfFloat1[j], paramArrayOfFloat2[j]);
        if (k != 0) {
          return k;
        }
      }
      return paramArrayOfFloat1.length - paramArrayOfFloat2.length;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\Floats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */