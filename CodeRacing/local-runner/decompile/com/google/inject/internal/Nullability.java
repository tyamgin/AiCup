package com.google.inject.internal;

import java.lang.annotation.Annotation;

public class Nullability
{
  public static boolean allowsNull(Annotation[] paramArrayOfAnnotation)
  {
    for (Annotation localAnnotation : paramArrayOfAnnotation)
    {
      Class localClass = localAnnotation.annotationType();
      if ("Nullable".equals(localClass.getSimpleName())) {
        return true;
      }
    }
    return false;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\Nullability.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */