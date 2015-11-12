package com.google.common.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Defaults
{
  private static final Map DEFAULTS;
  
  private static void put(Map paramMap, Class paramClass, Object paramObject)
  {
    paramMap.put(paramClass, paramObject);
  }
  
  public static Object defaultValue(Class paramClass)
  {
    return DEFAULTS.get(Preconditions.checkNotNull(paramClass));
  }
  
  static
  {
    HashMap localHashMap = new HashMap();
    put(localHashMap, Boolean.TYPE, Boolean.valueOf(false));
    put(localHashMap, Character.TYPE, Character.valueOf('\000'));
    put(localHashMap, Byte.TYPE, Byte.valueOf((byte)0));
    put(localHashMap, Short.TYPE, Short.valueOf((short)0));
    put(localHashMap, Integer.TYPE, Integer.valueOf(0));
    put(localHashMap, Long.TYPE, Long.valueOf(0L));
    put(localHashMap, Float.TYPE, Float.valueOf(0.0F));
    put(localHashMap, Double.TYPE, Double.valueOf(0.0D));
    DEFAULTS = Collections.unmodifiableMap(localHashMap);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Defaults.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */