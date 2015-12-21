package com.google.inject.internal.cglib.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class $MethodInfoTransformer
  implements .Transformer
{
  private static final MethodInfoTransformer INSTANCE = new MethodInfoTransformer();
  
  public static MethodInfoTransformer getInstance()
  {
    return INSTANCE;
  }
  
  public Object transform(Object paramObject)
  {
    if ((paramObject instanceof Method)) {
      return .ReflectUtils.getMethodInfo((Method)paramObject);
    }
    if ((paramObject instanceof Constructor)) {
      return .ReflectUtils.getMethodInfo((Constructor)paramObject);
    }
    throw new IllegalArgumentException("cannot get method info for " + paramObject);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$MethodInfoTransformer.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */