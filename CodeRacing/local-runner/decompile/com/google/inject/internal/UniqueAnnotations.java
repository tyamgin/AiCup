package com.google.inject.internal;

import com.google.inject.BindingAnnotation;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

public class UniqueAnnotations
{
  private static final AtomicInteger nextUniqueValue = new AtomicInteger(1);
  
  public static Annotation create()
  {
    return create(nextUniqueValue.getAndIncrement());
  }
  
  static Annotation create(int paramInt)
  {
    new Internal()
    {
      public int value()
      {
        return this.val$value;
      }
      
      public Class annotationType()
      {
        return UniqueAnnotations.Internal.class;
      }
      
      public String toString()
      {
        String str = String.valueOf(String.valueOf(UniqueAnnotations.Internal.class.getName()));
        int i = this.val$value;
        return 20 + str.length() + "@" + str + "(value=" + i + ")";
      }
      
      public boolean equals(Object paramAnonymousObject)
      {
        return ((paramAnonymousObject instanceof UniqueAnnotations.Internal)) && (((UniqueAnnotations.Internal)paramAnonymousObject).value() == value());
      }
      
      public int hashCode()
      {
        return 127 * "value".hashCode() ^ this.val$value;
      }
    };
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @BindingAnnotation
  static @interface Internal
  {
    int value();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\UniqueAnnotations.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */