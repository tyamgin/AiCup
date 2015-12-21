package com.a.a.a.a;

import com.codeforces.commons.reflection.ReflectionUtil;
import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.Primitives;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class d
{
  private static final Set a = ImmutableSet.of(Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, new Class[] { Float.TYPE, Double.TYPE });
  private static final Set b = ImmutableSet.of(Boolean.class, Character.class, Byte.class, Short.class, Integer.class, Long.class, new Class[] { Float.class, Double.class });
  
  public static Object a(Object paramObject, Class paramClass)
    throws IllegalAccessException, InvocationTargetException, InstantiationException
  {
    if (paramObject == null)
    {
      if (a.contains(paramClass)) {
        throw new IllegalArgumentException("Can't get null object as primitive class.");
      }
      return null;
    }
    Class localClass1 = paramObject.getClass();
    boolean bool1 = a.contains(localClass1);
    boolean bool2 = b.contains(localClass1);
    if (((bool1) || (bool2) || (localClass1 == String.class)) && ((localClass1 == paramClass) || ((bool1) && (Primitives.wrap(localClass1) == paramClass)) || ((bool2) && (Primitives.unwrap(localClass1) == paramClass)))) {
      return paramObject;
    }
    if ((localClass1.isEnum()) && (paramClass.isEnum())) {
      return a(paramObject, paramClass, localClass1);
    }
    if ((localClass1.isArray()) && (paramClass.isArray())) {
      return b(paramObject, paramClass);
    }
    Constructor[] arrayOfConstructor = paramClass.getConstructors();
    if (arrayOfConstructor.length != 1) {
      throw new IllegalArgumentException("Too many constructors in target class '" + paramClass + "'.");
    }
    Constructor localConstructor = arrayOfConstructor[0];
    Class[] arrayOfClass = localConstructor.getParameterTypes();
    int i = arrayOfClass.length;
    Map localMap = ReflectionUtil.getFieldsByNameMap(localClass1);
    int j = localMap.size();
    if (j != i)
    {
      while ((j > i) && ((localClass1 = localClass1.getSuperclass()) != null))
      {
        localMap = ReflectionUtil.getFieldsByNameMap(localClass1);
        j = localMap.size();
      }
      if (j != i) {
        throw new IllegalArgumentException(String.format("Source object %s and target class %s aren't compatible.", new Object[] { paramObject, paramClass }));
      }
    }
    Object[] arrayOfObject = new Object[j];
    Iterator localIterator = localMap.values().iterator();
    for (int k = 0; k < j; k++)
    {
      List localList = (List)localIterator.next();
      if (localList.size() != 1) {
        throw new IllegalArgumentException(String.format("There are multiple fields with name '%s' in class '%s'.", new Object[] { ((Field)localList.get(0)).getName(), localClass1.getName() }));
      }
      Field localField = (Field)localList.get(0);
      Class localClass2 = localField.getType();
      Class localClass3 = arrayOfClass[k];
      boolean bool3 = a.contains(localClass2);
      boolean bool4 = b.contains(localClass2);
      if ((bool3) || (bool4) || (localClass2 == String.class))
      {
        if ((localClass2 == localClass3) || ((bool3) && (Primitives.wrap(localClass2) == localClass3)) || ((bool4) && (Primitives.unwrap(localClass2) == localClass3)))
        {
          arrayOfObject[k] = localField.get(paramObject);
          continue;
        }
      }
      else
      {
        if ((localClass2.isEnum()) && (localClass3.isEnum()))
        {
          arrayOfObject[k] = a(localField.get(paramObject), localClass3, localClass2);
          continue;
        }
        if ((localClass2.isArray()) && (localClass3.isArray()))
        {
          arrayOfObject[k] = b(localField.get(paramObject), localClass3);
          continue;
        }
      }
      throw new IllegalArgumentException(String.format("Field '%s' of source object and constructor parameter of target class '%s' aren't compatible.", new Object[] { localField.getName(), paramClass.getName() }));
    }
    return localConstructor.newInstance(arrayOfObject);
  }
  
  private static Object a(Object paramObject, Class paramClass1, Class paramClass2)
  {
    if ((paramObject == null) || (paramClass2 == paramClass1)) {
      return paramObject;
    }
    Class localClass = paramClass1;
    return Enum.valueOf(localClass, ((Enum)paramObject).name());
  }
  
  private static Object b(Object paramObject, Class paramClass)
    throws IllegalAccessException, InvocationTargetException, InstantiationException
  {
    if (paramObject == null) {
      return null;
    }
    Class localClass = paramClass.getComponentType();
    int i = Array.getLength(paramObject);
    Object localObject = Array.newInstance(localClass, i);
    for (int j = 0; j < i; j++) {
      Array.set(localObject, j, a(Array.get(paramObject, j), localClass));
    }
    return localObject;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\a\a\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */