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
public final class Doubles
{
  public static final int BYTES = 8;
  @GwtIncompatible("regular expressions")
  static final Pattern FLOATING_POINT_PATTERN = ;
  
  public static int hashCode(double paramDouble)
  {
    return Double.valueOf(paramDouble).hashCode();
  }
  
  public static int compare(double paramDouble1, double paramDouble2)
  {
    return Double.compare(paramDouble1, paramDouble2);
  }
  
  public static boolean isFinite(double paramDouble)
  {
    return (Double.NEGATIVE_INFINITY < paramDouble ? 1 : 0) & (paramDouble < Double.POSITIVE_INFINITY ? 1 : 0);
  }
  
  public static boolean contains(double[] paramArrayOfDouble, double paramDouble)
  {
    for (double d : paramArrayOfDouble) {
      if (d == paramDouble) {
        return true;
      }
    }
    return false;
  }
  
  public static int indexOf(double[] paramArrayOfDouble, double paramDouble)
  {
    return indexOf(paramArrayOfDouble, paramDouble, 0, paramArrayOfDouble.length);
  }
  
  private static int indexOf(double[] paramArrayOfDouble, double paramDouble, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt2; i++) {
      if (paramArrayOfDouble[i] == paramDouble) {
        return i;
      }
    }
    return -1;
  }
  
  public static int indexOf(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
  {
    Preconditions.checkNotNull(paramArrayOfDouble1, "array");
    Preconditions.checkNotNull(paramArrayOfDouble2, "target");
    if (paramArrayOfDouble2.length == 0) {
      return 0;
    }
    label65:
    for (int i = 0; i < paramArrayOfDouble1.length - paramArrayOfDouble2.length + 1; i++)
    {
      for (int j = 0; j < paramArrayOfDouble2.length; j++) {
        if (paramArrayOfDouble1[(i + j)] != paramArrayOfDouble2[j]) {
          break label65;
        }
      }
      return i;
    }
    return -1;
  }
  
  public static int lastIndexOf(double[] paramArrayOfDouble, double paramDouble)
  {
    return lastIndexOf(paramArrayOfDouble, paramDouble, 0, paramArrayOfDouble.length);
  }
  
  private static int lastIndexOf(double[] paramArrayOfDouble, double paramDouble, int paramInt1, int paramInt2)
  {
    for (int i = paramInt2 - 1; i >= paramInt1; i--) {
      if (paramArrayOfDouble[i] == paramDouble) {
        return i;
      }
    }
    return -1;
  }
  
  public static double min(double... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    double d = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      d = Math.min(d, paramVarArgs[i]);
    }
    return d;
  }
  
  public static double max(double... paramVarArgs)
  {
    Preconditions.checkArgument(paramVarArgs.length > 0);
    double d = paramVarArgs[0];
    for (int i = 1; i < paramVarArgs.length; i++) {
      d = Math.max(d, paramVarArgs[i]);
    }
    return d;
  }
  
  public static double[] concat(double[]... paramVarArgs)
  {
    int i = 0;
    for (Object localObject2 : paramVarArgs) {
      i += localObject2.length;
    }
    ??? = new double[i];
    ??? = 0;
    for (double[] arrayOfDouble1 : paramVarArgs)
    {
      System.arraycopy(arrayOfDouble1, 0, ???, ???, arrayOfDouble1.length);
      ??? += arrayOfDouble1.length;
    }
    return (double[])???;
  }
  
  public static double[] ensureCapacity(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
  {
    Preconditions.checkArgument(paramInt1 >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(paramInt1) });
    Preconditions.checkArgument(paramInt2 >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(paramInt2) });
    return paramArrayOfDouble.length < paramInt1 ? copyOf(paramArrayOfDouble, paramInt1 + paramInt2) : paramArrayOfDouble;
  }
  
  private static double[] copyOf(double[] paramArrayOfDouble, int paramInt)
  {
    double[] arrayOfDouble = new double[paramInt];
    System.arraycopy(paramArrayOfDouble, 0, arrayOfDouble, 0, Math.min(paramArrayOfDouble.length, paramInt));
    return arrayOfDouble;
  }
  
  public static String join(String paramString, double... paramVarArgs)
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
  
  public static double[] toArray(Collection paramCollection)
  {
    if ((paramCollection instanceof DoubleArrayAsList)) {
      return ((DoubleArrayAsList)paramCollection).toDoubleArray();
    }
    Object[] arrayOfObject = paramCollection.toArray();
    int i = arrayOfObject.length;
    double[] arrayOfDouble = new double[i];
    for (int j = 0; j < i; j++) {
      arrayOfDouble[j] = ((Number)Preconditions.checkNotNull(arrayOfObject[j])).doubleValue();
    }
    return arrayOfDouble;
  }
  
  public static List asList(double... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return Collections.emptyList();
    }
    return new DoubleArrayAsList(paramVarArgs);
  }
  
  @GwtIncompatible("regular expressions")
  private static Pattern fpPattern()
  {
    String str1 = "(?:\\d++(?:\\.\\d*+)?|\\.\\d++)";
    String str2 = str1 + "(?:[eE][+-]?\\d++)?[fFdD]?";
    String str3 = "(?:\\p{XDigit}++(?:\\.\\p{XDigit}*+)?|\\.\\p{XDigit}++)";
    String str4 = "0[xX]" + str3 + "[pP][+-]?\\d++[fFdD]?";
    String str5 = "[+-]?(?:NaN|Infinity|" + str2 + "|" + str4 + ")";
    return Pattern.compile(str5);
  }
  
  @GwtIncompatible("regular expressions")
  @Beta
  public static Double tryParse(String paramString)
  {
    if (FLOATING_POINT_PATTERN.matcher(paramString).matches()) {
      try
      {
        return Double.valueOf(Double.parseDouble(paramString));
      }
      catch (NumberFormatException localNumberFormatException) {}
    }
    return null;
  }
  
  @GwtCompatible
  private static class DoubleArrayAsList
    extends AbstractList
    implements Serializable, RandomAccess
  {
    final double[] array;
    final int start;
    final int end;
    private static final long serialVersionUID = 0L;
    
    DoubleArrayAsList(double[] paramArrayOfDouble)
    {
      this(paramArrayOfDouble, 0, paramArrayOfDouble.length);
    }
    
    DoubleArrayAsList(double[] paramArrayOfDouble, int paramInt1, int paramInt2)
    {
      this.array = paramArrayOfDouble;
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
    
    public Double get(int paramInt)
    {
      Preconditions.checkElementIndex(paramInt, size());
      return Double.valueOf(this.array[(this.start + paramInt)]);
    }
    
    public boolean contains(Object paramObject)
    {
      return ((paramObject instanceof Double)) && (Doubles.indexOf(this.array, ((Double)paramObject).doubleValue(), this.start, this.end) != -1);
    }
    
    public int indexOf(Object paramObject)
    {
      if ((paramObject instanceof Double))
      {
        int i = Doubles.indexOf(this.array, ((Double)paramObject).doubleValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public int lastIndexOf(Object paramObject)
    {
      if ((paramObject instanceof Double))
      {
        int i = Doubles.lastIndexOf(this.array, ((Double)paramObject).doubleValue(), this.start, this.end);
        if (i >= 0) {
          return i - this.start;
        }
      }
      return -1;
    }
    
    public Double set(int paramInt, Double paramDouble)
    {
      Preconditions.checkElementIndex(paramInt, size());
      double d = this.array[(this.start + paramInt)];
      this.array[(this.start + paramInt)] = ((Double)Preconditions.checkNotNull(paramDouble)).doubleValue();
      return Double.valueOf(d);
    }
    
    public List subList(int paramInt1, int paramInt2)
    {
      int i = size();
      Preconditions.checkPositionIndexes(paramInt1, paramInt2, i);
      if (paramInt1 == paramInt2) {
        return Collections.emptyList();
      }
      return new DoubleArrayAsList(this.array, this.start + paramInt1, this.start + paramInt2);
    }
    
    public boolean equals(Object paramObject)
    {
      if (paramObject == this) {
        return true;
      }
      if ((paramObject instanceof DoubleArrayAsList))
      {
        DoubleArrayAsList localDoubleArrayAsList = (DoubleArrayAsList)paramObject;
        int i = size();
        if (localDoubleArrayAsList.size() != i) {
          return false;
        }
        for (int j = 0; j < i; j++) {
          if (this.array[(this.start + j)] != localDoubleArrayAsList.array[(localDoubleArrayAsList.start + j)]) {
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
        i = 31 * i + Doubles.hashCode(this.array[j]);
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
    
    double[] toDoubleArray()
    {
      int i = size();
      double[] arrayOfDouble = new double[i];
      System.arraycopy(this.array, this.start, arrayOfDouble, 0, i);
      return arrayOfDouble;
    }
  }
  
  private static enum LexicographicalComparator
    implements Comparator
  {
    INSTANCE;
    
    public int compare(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    {
      int i = Math.min(paramArrayOfDouble1.length, paramArrayOfDouble2.length);
      for (int j = 0; j < i; j++)
      {
        int k = Doubles.compare(paramArrayOfDouble1[j], paramArrayOfDouble2[j]);
        if (k != 0) {
          return k;
        }
      }
      return paramArrayOfDouble1.length - paramArrayOfDouble2.length;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\Doubles.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */