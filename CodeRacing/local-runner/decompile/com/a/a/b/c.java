package com.a.a.b;

import com.codeforces.commons.text.StringUtil;

class c
{
  public final String a;
  
  c(String paramString)
  {
    this.a = paramString;
  }
  
  public int hashCode()
  {
    return this.a.hashCode();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    c localc = (c)paramObject;
    return this.a.equals(localc.a);
  }
  
  static void a(String paramString)
  {
    if (StringUtil.isBlank(paramString)) {
      throw new IllegalArgumentException("Argument 'name' is blank.");
    }
    if (!StringUtil.trim(paramString).equals(paramString)) {
      throw new IllegalArgumentException("Argument 'name' should not contain neither leading nor trailing whitespace characters.");
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */