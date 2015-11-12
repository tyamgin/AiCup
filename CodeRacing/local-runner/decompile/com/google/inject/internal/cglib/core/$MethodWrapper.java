package com.google.inject.internal.cglib.core;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class $MethodWrapper
{
  private static final MethodWrapperKey KEY_FACTORY = (MethodWrapperKey).KeyFactory.create(MethodWrapperKey.class);
  
  public static Object create(Method paramMethod)
  {
    return KEY_FACTORY.newInstance(paramMethod.getName(), .ReflectUtils.getNames(paramMethod.getParameterTypes()), paramMethod.getReturnType().getName());
  }
  
  public static Set createSet(Collection paramCollection)
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext()) {
      localHashSet.add(create((Method)localIterator.next()));
    }
    return localHashSet;
  }
  
  public static abstract interface MethodWrapperKey
  {
    public abstract Object newInstance(String paramString1, String[] paramArrayOfString, String paramString2);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$MethodWrapper.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */