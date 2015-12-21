package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm..Type;

public abstract class $MethodInfo
{
  public abstract .ClassInfo getClassInfo();
  
  public abstract int getModifiers();
  
  public abstract .Signature getSignature();
  
  public abstract .Type[] getExceptionTypes();
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof MethodInfo)) {
      return false;
    }
    return getSignature().equals(((MethodInfo)paramObject).getSignature());
  }
  
  public int hashCode()
  {
    return getSignature().hashCode();
  }
  
  public String toString()
  {
    return getSignature().toString();
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\core\$MethodInfo.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */