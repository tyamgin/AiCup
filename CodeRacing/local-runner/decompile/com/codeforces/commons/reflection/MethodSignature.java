package com.codeforces.commons.reflection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class MethodSignature
{
  private final String name;
  private final List parameterTypes;
  private final int hashCode;
  
  public MethodSignature(String paramString, Class... paramVarArgs)
  {
    this.name = paramString;
    this.parameterTypes = Arrays.asList(paramVarArgs);
    int i = this.name.hashCode();
    i = 31 * i + this.parameterTypes.hashCode();
    this.hashCode = i;
  }
  
  public String getName()
  {
    return this.name;
  }
  
  public List getParameterTypes()
  {
    return Collections.unmodifiableList(this.parameterTypes);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    MethodSignature localMethodSignature = (MethodSignature)paramObject;
    return (this.name.equals(localMethodSignature.name)) && (this.parameterTypes.equals(localMethodSignature.parameterTypes));
  }
  
  public int hashCode()
  {
    return this.hashCode;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\reflection\MethodSignature.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */