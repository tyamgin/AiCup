package com.a.c.a;

import com.codeforces.commons.math.Math;
import com.codeforces.commons.text.StringUtil;

public class e
  extends c
{
  private double a;
  private double b;
  
  public e(double paramDouble1, double paramDouble2)
  {
    this.a = paramDouble1;
    this.b = paramDouble2;
  }
  
  public e(e parame)
  {
    this.a = parame.a;
    this.b = parame.b;
  }
  
  public double a()
  {
    return this.a;
  }
  
  public double b()
  {
    return this.b;
  }
  
  public c d()
  {
    return new e(this);
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
    e locale = (e)paramc;
    return (Math.abs(this.a - locale.a) < paramDouble) && (Math.abs(this.b - locale.b) < paramDouble);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\c\a\e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */