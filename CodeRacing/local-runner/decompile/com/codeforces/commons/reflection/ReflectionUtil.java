package com.codeforces.commons.reflection;

import com.codeforces.commons.text.StringUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.StringUtils;

public class ReflectionUtil
{
  private static final ConcurrentMap fieldsByNameByClass = new ConcurrentHashMap();
  private static final ConcurrentMap publicMethodBySignatureByClass = new ConcurrentHashMap();
  
  public static Object getDeepValue(Object paramObject, String paramString)
  {
    return getDeepValue(paramObject, paramString, false, false, false);
  }
  
  public static Object getDeepValue(Object paramObject, String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    Object localObject1 = null;
    Object localObject2 = paramObject;
    String[] arrayOfString = StringUtil.split(paramString, '.');
    int i = 0;
    int j = arrayOfString.length;
    while (i < j)
    {
      String str = arrayOfString[i];
      if (StringUtil.isBlank(str)) {
        throw new IllegalArgumentException("Field name can not be neither 'null' nor blank.");
      }
      int k = 0;
      List localList = (List)getFieldsByNameMap(localObject2.getClass()).get(str);
      if ((localList != null) && (!localList.isEmpty()))
      {
        localObject1 = getFieldValue((Field)localList.get(0), localObject2);
        k = 1;
      }
      if ((k == 0) && (!paramBoolean1))
      {
        Method localMethod = findPublicGetter(str, localObject2.getClass());
        try
        {
          if (localMethod != null)
          {
            localObject1 = localMethod.invoke(localObject2, new Object[0]);
            k = 1;
          }
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new IllegalStateException("This exception is unexpected because method should be public.", localIllegalAccessException);
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          if ((localInvocationTargetException.getTargetException() instanceof RuntimeException)) {
            throw ((RuntimeException)localInvocationTargetException.getTargetException());
          }
          throw new IllegalStateException("This type of exception is unexpected.", localInvocationTargetException);
        }
      }
      if ((k == 0) && (!paramBoolean2) && ((localObject2 instanceof Map)))
      {
        localObject1 = ((Map)localObject2).get(str);
        k = 1;
      }
      if ((k == 0) && (!paramBoolean3)) {
        try
        {
          int m = Integer.parseInt(str);
          Object localObject3;
          if ((localObject2 instanceof List))
          {
            localObject3 = (List)localObject2;
            localObject1 = ((List)localObject3).get(m < 0 ? ((List)localObject3).size() + m : m);
            k = 1;
          }
          else if ((localObject2 instanceof Collection))
          {
            localObject3 = (Collection)localObject2;
            Iterator localIterator = ((Collection)localObject3).iterator();
            if (m < 0) {
              m += ((Collection)localObject3).size();
            }
            for (int n = 0; n <= m; n++) {
              localObject1 = localIterator.next();
            }
            k = 1;
          }
        }
        catch (NumberFormatException localNumberFormatException) {}
      }
      if (k == 0) {
        throw new IllegalArgumentException(String.format("Can't find '%s' in %s.", new Object[] { str, localObject2.getClass() }));
      }
      if (localObject1 == null) {
        break;
      }
      localObject2 = localObject1;
      i++;
    }
    return localObject1;
  }
  
  public static Method findPublicGetter(String paramString, Class paramClass)
  {
    Map localMap = getPublicMethodBySignatureMap(paramClass);
    String str = StringUtils.capitalize(paramString);
    Method localMethod = (Method)localMap.get(new MethodSignature("is" + str, new Class[0]));
    if ((localMethod != null) && (localMethod.getReturnType() == Boolean.TYPE) && (throwsOnlyRuntimeExceptions(localMethod))) {
      return localMethod;
    }
    localMethod = (Method)localMap.get(new MethodSignature("get" + str, new Class[0]));
    if ((localMethod != null) && (localMethod.getReturnType() != Void.TYPE) && (localMethod.getReturnType() != Void.class) && (throwsOnlyRuntimeExceptions(localMethod))) {
      return localMethod;
    }
    localMethod = (Method)localMap.get(new MethodSignature(paramString, new Class[0]));
    if ((localMethod != null) && (localMethod.getReturnType() != Void.TYPE) && (localMethod.getReturnType() != Void.class) && (throwsOnlyRuntimeExceptions(localMethod))) {
      return localMethod;
    }
    return null;
  }
  
  public static Map getFieldsByNameMap(Class paramClass)
  {
    Object localObject1 = (Map)fieldsByNameByClass.get(paramClass);
    if (localObject1 == null)
    {
      localObject1 = new LinkedHashMap();
      Class localClass = paramClass.getSuperclass();
      if (localClass != null) {
        ((Map)localObject1).putAll(getFieldsByNameMap(localClass));
      }
      for (Field localField : paramClass.getDeclaredFields()) {
        if ((!localField.isEnumConstant()) && (!Modifier.isStatic(localField.getModifiers())) && (!localField.isSynthetic()))
        {
          localField.setAccessible(true);
          Name localName = (Name)localField.getAnnotation(Name.class);
          String str = localName == null ? localField.getName() : localName.value();
          Object localObject2 = (List)((Map)localObject1).get(str);
          if (localObject2 == null)
          {
            localObject2 = new ArrayList(1);
            ((List)localObject2).add(localField);
          }
          else
          {
            Object localObject3 = localObject2;
            localObject2 = new ArrayList(((List)localObject3).size() + 1);
            ((List)localObject2).add(localField);
            ((List)localObject2).addAll((Collection)localObject3);
          }
          ((Map)localObject1).put(str, Collections.unmodifiableList((List)localObject2));
        }
      }
      fieldsByNameByClass.putIfAbsent(paramClass, Collections.unmodifiableMap((Map)localObject1));
      return (Map)fieldsByNameByClass.get(paramClass);
    }
    return (Map)localObject1;
  }
  
  private static boolean throwsOnlyRuntimeExceptions(Method paramMethod)
  {
    for (Class localClass : paramMethod.getExceptionTypes()) {
      if (!RuntimeException.class.isAssignableFrom(localClass)) {
        return false;
      }
    }
    return true;
  }
  
  public static Map getPublicMethodBySignatureMap(Class paramClass)
  {
    Object localObject = (Map)publicMethodBySignatureByClass.get(paramClass);
    if (localObject == null)
    {
      Method[] arrayOfMethod = paramClass.getMethods();
      int i = arrayOfMethod.length;
      localObject = new LinkedHashMap(i);
      for (int j = 0; j < i; j++)
      {
        Method localMethod = arrayOfMethod[j];
        Name localName = (Name)localMethod.getAnnotation(Name.class);
        String str = localName == null ? localMethod.getName() : localName.value();
        localMethod.setAccessible(true);
        ((Map)localObject).put(new MethodSignature(str, localMethod.getParameterTypes()), localMethod);
      }
      publicMethodBySignatureByClass.putIfAbsent(paramClass, Collections.unmodifiableMap((Map)localObject));
      return (Map)publicMethodBySignatureByClass.get(paramClass);
    }
    return (Map)localObject;
  }
  
  private static Object getFieldValue(Field paramField, Object paramObject)
  {
    try
    {
      return paramField.get(paramObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Name localName = (Name)paramField.getAnnotation(Name.class);
      String str = localName == null ? paramField.getName() : localName.value();
      throw new IllegalArgumentException("Can't get value of inaccessible field '" + str + "'.", localIllegalAccessException);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\reflection\ReflectionUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */