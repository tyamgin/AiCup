package com.a.c.a;

import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.text.StringUtil;

public class d
  extends c
{
  private double a;
  private double b;
  private double c;
  private double d;
  
  public d(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    this.a = paramDouble1;
    this.b = paramDouble2;
    this.c = paramDouble3;
    this.d = paramDouble4;
  }
  
  public d(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    this.a = paramPoint2D1.getX();
    this.b = paramPoint2D1.getY();
    this.c = paramPoint2D2.getX();
    this.d = paramPoint2D2.getY();
  }
  
  public d(d paramd)
  {
    this.a = paramd.a;
    this.b = paramd.b;
    this.c = paramd.c;
    this.d = paramd.d;
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
  
  public double f()
  {
    return this.d;
  }
  
  public c d()
  {
    return new d(this);
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
    d locald = (d)paramc;
    return (Math.abs(this.a - locald.a) < paramDouble) && (Math.abs(this.b - locald.b) < paramDouble) && (Math.abs(this.c - locald.c) < paramDouble) && (Math.abs(this.d - locald.d) < paramDouble);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\c\a\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */