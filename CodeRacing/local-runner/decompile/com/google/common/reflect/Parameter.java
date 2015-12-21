package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;

@Beta
public final class Parameter
  implements AnnotatedElement
{
  private final Invokable declaration;
  private final int position;
  private final TypeToken type;
  private final ImmutableList annotations;
  
  Parameter(Invokable paramInvokable, int paramInt, TypeToken paramTypeToken, Annotation[] paramArrayOfAnnotation)
  {
    this.declaration = paramInvokable;
    this.position = paramInt;
    this.type = paramTypeToken;
    this.annotations = ImmutableList.copyOf(paramArrayOfAnnotation);
  }
  
  public TypeToken getType()
  {
    return this.type;
  }
  
  public Invokable getDeclaringInvokable()
  {
    return this.declaration;
  }
  
  public boolean isAnnotationPresent(Class paramClass)
  {
    return getAnnotation(paramClass) != null;
  }
  
  public Annotation getAnnotation(Class paramClass)
  {
    Preconditions.checkNotNull(paramClass);
    Iterator localIterator = this.annotations.iterator();
    while (localIterator.hasNext())
    {
      Annotation localAnnotation = (Annotation)localIterator.next();
      if (paramClass.isInstance(localAnnotation)) {
        return (Annotation)paramClass.cast(localAnnotation);
      }
    }
    return null;
  }
  
  public Annotation[] getAnnotations()
  {
    return getDeclaredAnnotations();
  }
  
  public Annotation[] getDeclaredAnnotations()
  {
    return (Annotation[])this.annotations.toArray(new Annotation[this.annotations.size()]);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Parameter))
    {
      Parameter localParameter = (Parameter)paramObject;
      return (this.position == localParameter.position) && (this.declaration.equals(localParameter.declaration));
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.position;
  }
  
  public String toString()
  {
    return this.type + " arg" + this.position;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\reflect\Parameter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */