package com.google.inject.internal;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.binder.AnnotatedConstantBindingBuilder;
import com.google.inject.binder.ConstantBindingBuilder;
import java.lang.annotation.Annotation;
import java.util.List;

public final class ConstantBindingBuilderImpl
  extends AbstractBindingBuilder
  implements AnnotatedConstantBindingBuilder, ConstantBindingBuilder
{
  public ConstantBindingBuilderImpl(Binder paramBinder, List paramList, Object paramObject)
  {
    super(paramBinder, paramList, paramObject, NULL_KEY);
  }
  
  public ConstantBindingBuilder annotatedWith(Class paramClass)
  {
    annotatedWithInternal(paramClass);
    return this;
  }
  
  public ConstantBindingBuilder annotatedWith(Annotation paramAnnotation)
  {
    annotatedWithInternal(paramAnnotation);
    return this;
  }
  
  public void to(String paramString)
  {
    toConstant(String.class, paramString);
  }
  
  public void to(int paramInt)
  {
    toConstant(Integer.class, Integer.valueOf(paramInt));
  }
  
  public void to(long paramLong)
  {
    toConstant(Long.class, Long.valueOf(paramLong));
  }
  
  public void to(boolean paramBoolean)
  {
    toConstant(Boolean.class, Boolean.valueOf(paramBoolean));
  }
  
  public void to(double paramDouble)
  {
    toConstant(Double.class, Double.valueOf(paramDouble));
  }
  
  public void to(float paramFloat)
  {
    toConstant(Float.class, Float.valueOf(paramFloat));
  }
  
  public void to(short paramShort)
  {
    toConstant(Short.class, Short.valueOf(paramShort));
  }
  
  public void to(char paramChar)
  {
    toConstant(Character.class, Character.valueOf(paramChar));
  }
  
  public void to(byte paramByte)
  {
    toConstant(Byte.class, Byte.valueOf(paramByte));
  }
  
  public void to(Class paramClass)
  {
    toConstant(Class.class, paramClass);
  }
  
  public void to(Enum paramEnum)
  {
    toConstant(paramEnum.getDeclaringClass(), paramEnum);
  }
  
  private void toConstant(Class paramClass, Object paramObject)
  {
    Class localClass = paramClass;
    Object localObject = paramObject;
    if (keyTypeIsSet())
    {
      this.binder.addError("Constant value is set more than once.", new Object[0]);
      return;
    }
    BindingImpl localBindingImpl = getBinding();
    Key localKey;
    if (localBindingImpl.getKey().getAnnotation() != null) {
      localKey = Key.get(localClass, localBindingImpl.getKey().getAnnotation());
    } else if (localBindingImpl.getKey().getAnnotationType() != null) {
      localKey = Key.get(localClass, localBindingImpl.getKey().getAnnotationType());
    } else {
      localKey = Key.get(localClass);
    }
    if (localObject == null) {
      this.binder.addError("Binding to null instances is not allowed. Use toProvider(Providers.of(null)) if this is your intended behaviour.", new Object[0]);
    }
    setBinding(new InstanceBindingImpl(localBindingImpl.getSource(), localKey, localBindingImpl.getScoping(), ImmutableSet.of(), localObject));
  }
  
  public String toString()
  {
    return "ConstantBindingBuilder";
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\ConstantBindingBuilderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */