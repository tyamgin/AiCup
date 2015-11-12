package com.google.inject.internal.asm;

public final class $Handle
{
  final int a;
  final String b;
  final String c;
  final String d;
  
  public $Handle(int paramInt, String paramString1, String paramString2, String paramString3)
  {
    this.a = paramInt;
    this.b = paramString1;
    this.c = paramString2;
    this.d = paramString3;
  }
  
  public int getTag()
  {
    return this.a;
  }
  
  public String getOwner()
  {
    return this.b;
  }
  
  public String getName()
  {
    return this.c;
  }
  
  public String getDesc()
  {
    return this.d;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof Handle)) {
      return false;
    }
    Handle localHandle = (Handle)paramObject;
    return (this.a == localHandle.a) && (this.b.equals(localHandle.b)) && (this.c.equals(localHandle.c)) && (this.d.equals(localHandle.d));
  }
  
  public int hashCode()
  {
    return this.a + this.b.hashCode() * this.c.hashCode() * this.d.hashCode();
  }
  
  public String toString()
  {
    return this.b + '.' + this.c + this.d + " (" + this.a + ')';
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\inject\internal\asm\$Handle.class
 * Java compiler version: 2 (46.0)
 * JD-Core Version:       0.7.1
 */