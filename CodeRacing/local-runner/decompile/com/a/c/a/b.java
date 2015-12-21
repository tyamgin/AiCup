package com.a.c.a;

import com.codeforces.commons.math.Math;
import com.codeforces.commons.text.StringUtil;

public class b
  extends c
{
  private double a;
  
  public b(double paramDouble)
  {
    this.a = paramDouble;
  }
  
  public b(b paramb)
  {
    this.a = paramb.a;
  }
  
  public double a()
  {
    return this.a;
  }
  
  public c d()
  {
    return new b(this);
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[0]);
  }
  
  public boolean a(c paramc, double paramDouble)
  {
    if ((paramc == null) || (getClass() != paramc.getClass())) {
      return false;
    }
    b localb = (b)paramc;
    return Math.abs(this.a - localb.a) < paramDouble;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\c\a\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */