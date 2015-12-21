package com.google.inject.internal.cglib.reflect;

import java.lang.reflect.Member;

public abstract class $FastMember
{
  protected .FastClass fc;
  protected Member member;
  protected int index;
  
  protected $FastMember(.FastClass paramFastClass, Member paramMember, int paramInt)
  {
    this.fc = paramFastClass;
    this.member = paramMember;
    this.index = paramInt;
  }
  
  public abstract Class[] getParameterTypes();
  
  public abstract Class[] getExceptionTypes();
  
  public int getIndex()
  {
    return this.index;
  }
  
  public String getName()
  {
    return this.member.getName();
  }
  
  public Class getDeclaringClass()
  {
    return this.fc.getJavaClass();
  }
  
  public int getModifiers()
  {
    return this.member.getModifiers();
  }
  
  public String toString()
  {
    return this.member.toString();
  }
  
  public int hashCode()
  {
    return this.member.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject == null) || (!(paramObject instanceof FastMember))) {
      return false;
    }
    return this.member.equals(((FastMember)paramObject).member);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\cglib\reflect\$FastMember.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */