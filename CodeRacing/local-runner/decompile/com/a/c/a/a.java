package com.a.c.a;

import com.codeforces.commons.math.Math;
import com.codeforces.commons.text.StringUtil;

public class a
  extends c
{
  private double a;
  private double b;
  private double c;
  
  public a(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.a = paramDouble1;
    this.b = paramDouble2;
    this.c = paramDouble3;
  }
  
  public a(a parama)
  {
    this.a = parama.a;
    this.b = parama.b;
    this.c = parama.c;
  }
  
  public double a()
  {
    return this.a;
  }
  
  public double b()
  {
    return this.b;
  }
  
  public double c()
  {
    return this.c;
  }
  
  public c d()
  {
    return new a(this);
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
    a locala = (a)paramc;
    return (Math.abs(this.a - locala.a) < paramDouble) && (Math.abs(this.c - locala.c) < paramDouble);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\c\a\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */