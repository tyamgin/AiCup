package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.util.Arrays;

@GwtCompatible
public final class Objects
{
  public static boolean equal(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)));
  }
  
  public static int hashCode(Object... paramVarArgs)
  {
    return Arrays.hashCode(paramVarArgs);
  }
  
  public static ToStringHelper toStringHelper(Object paramObject)
  {
    return new ToStringHelper(simpleName(paramObject.getClass()), null);
  }
  
  public static ToStringHelper toStringHelper(Class paramClass)
  {
    return new ToStringHelper(simpleName(paramClass), null);
  }
  
  public static ToStringHelper toStringHelper(String paramString)
  {
    return new ToStringHelper(paramString, null);
  }
  
  private static String simpleName(Class paramClass)
  {
    String str = paramClass.getName();
    str = str.replaceAll("\\$[0-9]+", "\\$");
    int i = str.lastIndexOf('$');
    if (i == -1) {
      i = str.lastIndexOf('.');
    }
    return str.substring(i + 1);
  }
  
  public static Object firstNonNull(Object paramObject1, Object paramObject2)
  {
    return paramObject1 != null ? paramObject1 : Preconditions.checkNotNull(paramObject2);
  }
  
  public static final class ToStringHelper
  {
    private final String className;
    private ValueHolder holderHead = new ValueHolder(null);
    private ValueHolder holderTail = this.holderHead;
    private boolean omitNullValues = false;
    
    private ToStringHelper(String paramString)
    {
      this.className = ((String)Preconditions.checkNotNull(paramString));
    }
    
    public ToStringHelper omitNullValues()
    {
      this.omitNullValues = true;
      return this;
    }
    
    public ToStringHelper add(String paramString, Object paramObject)
    {
      return addHolder(paramString, paramObject);
    }
    
    public ToStringHelper add(String paramString, boolean paramBoolean)
    {
      return addHolder(paramString, String.valueOf(paramBoolean));
    }
    
    public ToStringHelper add(String paramString, char paramChar)
    {
      return addHolder(paramString, String.valueOf(paramChar));
    }
    
    public ToStringHelper add(String paramString, double paramDouble)
    {
      return addHolder(paramString, String.valueOf(paramDouble));
    }
    
    public ToStringHelper add(String paramString, float paramFloat)
    {
      return addHolder(paramString, String.valueOf(paramFloat));
    }
    
    public ToStringHelper add(String paramString, int paramInt)
    {
      return addHolder(paramString, String.valueOf(paramInt));
    }
    
    public ToStringHelper add(String paramString, long paramLong)
    {
      return addHolder(paramString, String.valueOf(paramLong));
    }
    
    public ToStringHelper addValue(Object paramObject)
    {
      return addHolder(paramObject);
    }
    
    public ToStringHelper addValue(boolean paramBoolean)
    {
      return addHolder(String.valueOf(paramBoolean));
    }
    
    public ToStringHelper addValue(char paramChar)
    {
      return addHolder(String.valueOf(paramChar));
    }
    
    public ToStringHelper addValue(double paramDouble)
    {
      return addHolder(String.valueOf(paramDouble));
    }
    
    public ToStringHelper addValue(float paramFloat)
    {
      return addHolder(String.valueOf(paramFloat));
    }
    
    public ToStringHelper addValue(int paramInt)
    {
      return addHolder(String.valueOf(paramInt));
    }
    
    public ToStringHelper addValue(long paramLong)
    {
      return addHolder(String.valueOf(paramLong));
    }
    
    public String toString()
    {
      boolean bool = this.omitNullValues;
      String str = "";
      StringBuilder localStringBuilder = new StringBuilder(32).append(this.className).append('{');
      for (ValueHolder localValueHolder = this.holderHead.next; localValueHolder != null; localValueHolder = localValueHolder.next) {
        if ((!bool) || (localValueHolder.value != null))
        {
          localStringBuilder.append(str);
          str = ", ";
          if (localValueHolder.name != null) {
            localStringBuilder.append(localValueHolder.name).append('=');
          }
          localStringBuilder.append(localValueHolder.value);
        }
      }
      return '}';
    }
    
    private ValueHolder addHolder()
    {
      ValueHolder localValueHolder = new ValueHolder(null);
      this.holderTail = (this.holderTail.next = localValueHolder);
      return localValueHolder;
    }
    
    private ToStringHelper addHolder(Object paramObject)
    {
      ValueHolder localValueHolder = addHolder();
      localValueHolder.value = paramObject;
      return this;
    }
    
    private ToStringHelper addHolder(String paramString, Object paramObject)
    {
      ValueHolder localValueHolder = addHolder();
      localValueHolder.value = paramObject;
      localValueHolder.name = ((String)Preconditions.checkNotNull(paramString));
      return this;
    }
    
    private static final class ValueHolder
    {
      String name;
      Object value;
      ValueHolder next;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\base\Objects.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */