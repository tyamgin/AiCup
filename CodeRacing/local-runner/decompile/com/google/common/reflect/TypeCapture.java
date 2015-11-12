package com.google.common.reflect;

import com.google.common.base.Preconditions;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class TypeCapture
{
  final Type capture()
  {
    Type localType = getClass().getGenericSuperclass();
    Preconditions.checkArgument(localType instanceof ParameterizedType, "%s isn't parameterized", new Object[] { localType });
    return ((ParameterizedType)localType).getActualTypeArguments()[0];
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\TypeCapture.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */