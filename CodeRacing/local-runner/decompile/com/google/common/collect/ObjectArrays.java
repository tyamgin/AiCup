package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;

@GwtCompatible(emulated=true)
public final class ObjectArrays
{
  static final Object[] EMPTY_ARRAY = new Object[0];
  
  @GwtIncompatible("Array.newInstance(Class, int)")
  public static Object[] newArray(Class paramClass, int paramInt)
  {
    return (Object[])Array.newInstance(paramClass, paramInt);
  }
  
  public static Object[] newArray(Object[] paramArrayOfObject, int paramInt)
  {
    return Platform.newArray(paramArrayOfObject, paramInt);
  }
  
  @GwtIncompatible("Array.newInstance(Class, int)")
  public static Object[] concat(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2, Class paramClass)
  {
    Object[] arrayOfObject = newArray(paramClass, paramArrayOfObject1.length + paramArrayOfObject2.length);
    System.arraycopy(paramArrayOfObject1, 0, arrayOfObject, 0, paramArrayOfObject1.length);
    System.arraycopy(paramArrayOfObject2, 0, arrayOfObject, paramArrayOfObject1.length, paramArrayOfObject2.length);
    return arrayOfObject;
  }
  
  public static Object[] concat(Object paramObject, Object[] paramArrayOfObject)
  {
    Object[] arrayOfObject = newArray(paramArrayOfObject, paramArrayOfObject.length + 1);
    arrayOfObject[0] = paramObject;
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 1, paramArrayOfObject.length);
    return arrayOfObject;
  }
  
  public static Object[] concat(Object[] paramArrayOfObject, Object paramObject)
  {
    Object[] arrayOfObject = arraysCopyOf(paramArrayOfObject, paramArrayOfObject.length + 1);
    arrayOfObject[paramArrayOfObject.length] = paramObject;
    return arrayOfObject;
  }
  
  static Object[] arraysCopyOf(Object[] paramArrayOfObject, int paramInt)
  {
    Object[] arrayOfObject = newArray(paramArrayOfObject, paramInt);
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, Math.min(paramArrayOfObject.length, paramInt));
    return arrayOfObject;
  }
  
  static Object[] toArrayImpl(Collection paramCollection, Object[] paramArrayOfObject)
  {
    int i = paramCollection.size();
    if (paramArrayOfObject.length < i) {
      paramArrayOfObject = newArray(paramArrayOfObject, i);
    }
    fillArray(paramCollection, paramArrayOfObject);
    if (paramArrayOfObject.length > i) {
      paramArrayOfObject[i] = null;
    }
    return paramArrayOfObject;
  }
  
  static Object[] toArrayImpl(Collection paramCollection)
  {
    return fillArray(paramCollection, new Object[paramCollection.size()]);
  }
  
  private static Object[] fillArray(Iterable paramIterable, Object[] paramArrayOfObject)
  {
    int i = 0;
    Iterator localIterator = paramIterable.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = localIterator.next();
      paramArrayOfObject[(i++)] = localObject;
    }
    return paramArrayOfObject;
  }
  
  static void swap(Object[] paramArrayOfObject, int paramInt1, int paramInt2)
  {
    Object localObject = paramArrayOfObject[paramInt1];
    paramArrayOfObject[paramInt1] = paramArrayOfObject[paramInt2];
    paramArrayOfObject[paramInt2] = localObject;
  }
  
  static Object checkElementNotNull(Object paramObject, int paramInt)
  {
    if (paramObject == null) {
      throw new NullPointerException("at index " + paramInt);
    }
    return paramObject;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\ObjectArrays.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */