package com.google.common.primitives;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Primitives
{
  private static final Map PRIMITIVE_TO_WRAPPER_TYPE;
  private static final Map WRAPPER_TO_PRIMITIVE_TYPE;
  
  private static void add(Map paramMap1, Map paramMap2, Class paramClass1, Class paramClass2)
  {
    paramMap1.put(paramClass1, paramClass2);
    paramMap2.put(paramClass2, paramClass1);
  }
  
  public static Set allPrimitiveTypes()
  {
    return PRIMITIVE_TO_WRAPPER_TYPE.keySet();
  }
  
  public static Set allWrapperTypes()
  {
    return WRAPPER_TO_PRIMITIVE_TYPE.keySet();
  }
  
  public static boolean isWrapperType(Class paramClass)
  {
    return WRAPPER_TO_PRIMITIVE_TYPE.containsKey(Preconditions.checkNotNull(paramClass));
  }
  
  public static Class wrap(Class paramClass)
  {
    Preconditions.checkNotNull(paramClass);
    Class localClass = (Class)PRIMITIVE_TO_WRAPPER_TYPE.get(paramClass);
    return localClass == null ? paramClass : localClass;
  }
  
  public static Class unwrap(Class paramClass)
  {
    Preconditions.checkNotNull(paramClass);
    Class localClass = (Class)WRAPPER_TO_PRIMITIVE_TYPE.get(paramClass);
    return localClass == null ? paramClass : localClass;
  }
  
  static
  {
    HashMap localHashMap1 = new HashMap(16);
    HashMap localHashMap2 = new HashMap(16);
    add(localHashMap1, localHashMap2, Boolean.TYPE, Boolean.class);
    add(localHashMap1, localHashMap2, Byte.TYPE, Byte.class);
    add(localHashMap1, localHashMap2, Character.TYPE, Character.class);
    add(localHashMap1, localHashMap2, Double.TYPE, Double.class);
    add(localHashMap1, localHashMap2, Float.TYPE, Float.class);
    add(localHashMap1, localHashMap2, Integer.TYPE, Integer.class);
    add(localHashMap1, localHashMap2, Long.TYPE, Long.class);
    add(localHashMap1, localHashMap2, Short.TYPE, Short.class);
    add(localHashMap1, localHashMap2, Void.TYPE, Void.class);
    PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(localHashMap1);
    WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(localHashMap2);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\primitives\Primitives.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */