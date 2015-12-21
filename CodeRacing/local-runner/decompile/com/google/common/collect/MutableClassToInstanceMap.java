package com.google.common.collect;

import com.google.common.primitives.Primitives;
import java.util.HashMap;
import java.util.Map;

public final class MutableClassToInstanceMap
  extends MapConstraints.ConstrainedMap
  implements ClassToInstanceMap
{
  private static final MapConstraint VALUE_CAN_BE_CAST_TO_KEY = new MapConstraint()
  {
    public void checkKeyValue(Class paramAnonymousClass, Object paramAnonymousObject)
    {
      MutableClassToInstanceMap.cast(paramAnonymousClass, paramAnonymousObject);
    }
  };
  private static final long serialVersionUID = 0L;
  
  public static MutableClassToInstanceMap create()
  {
    return new MutableClassToInstanceMap(new HashMap());
  }
  
  public static MutableClassToInstanceMap create(Map paramMap)
  {
    return new MutableClassToInstanceMap(paramMap);
  }
  
  private MutableClassToInstanceMap(Map paramMap)
  {
    super(paramMap, VALUE_CAN_BE_CAST_TO_KEY);
  }
  
  public Object putInstance(Class paramClass, Object paramObject)
  {
    return cast(paramClass, put(paramClass, paramObject));
  }
  
  public Object getInstance(Class paramClass)
  {
    return cast(paramClass, get(paramClass));
  }
  
  private static Object cast(Class paramClass, Object paramObject)
  {
    return Primitives.wrap(paramClass).cast(paramObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\collect\MutableClassToInstanceMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */