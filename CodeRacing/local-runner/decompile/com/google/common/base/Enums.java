package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import java.io.Serializable;
import java.lang.reflect.Field;

@GwtCompatible(emulated=true)
@Beta
public final class Enums
{
  @GwtIncompatible("reflection")
  public static Field getField(Enum paramEnum)
  {
    Class localClass = paramEnum.getDeclaringClass();
    try
    {
      return localClass.getDeclaredField(paramEnum.name());
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      throw new AssertionError(localNoSuchFieldException);
    }
  }
  
  public static Function valueOfFunction(Class paramClass)
  {
    return new ValueOfFunction(paramClass, null);
  }
  
  public static Optional getIfPresent(Class paramClass, String paramString)
  {
    Preconditions.checkNotNull(paramClass);
    Preconditions.checkNotNull(paramString);
    try
    {
      return Optional.of(Enum.valueOf(paramClass, paramString));
    }
    catch (IllegalArgumentException localIllegalArgumentException) {}
    return Optional.absent();
  }
  
  private static final class ValueOfFunction
    implements Function, Serializable
  {
    private final Class enumClass;
    private static final long serialVersionUID = 0L;
    
    private ValueOfFunction(Class paramClass)
    {
      this.enumClass = ((Class)Preconditions.checkNotNull(paramClass));
    }
    
    public Enum apply(String paramString)
    {
      try
      {
        return Enum.valueOf(this.enumClass, paramString);
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
      return null;
    }
    
    public boolean equals(Object paramObject)
    {
      return ((paramObject instanceof ValueOfFunction)) && (this.enumClass.equals(((ValueOfFunction)paramObject).enumClass));
    }
    
    public int hashCode()
    {
      return this.enumClass.hashCode();
    }
    
    public String toString()
    {
      return "Enums.valueOf(" + this.enumClass + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Enums.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */