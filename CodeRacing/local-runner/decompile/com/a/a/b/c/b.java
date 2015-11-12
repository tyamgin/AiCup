package com.a.a.b.c;

import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.text.StringUtil;

public class b
  extends c
{
  private final double a;
  private final double b;
  
  public b(double paramDouble)
  {
    super(f.a);
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)) || (paramDouble <= 0.0D)) {
      throw new IllegalArgumentException(String.format("Argument 'radius' should be positive finite number but got %s.", new Object[] { Double.valueOf(paramDouble) }));
    }
    this.a = paramDouble;
    this.b = (paramDouble * paramDouble / 2.0D);
  }
  
  public double a()
  {
    return this.a;
  }
  
  public double d()
  {
    return this.a;
  }
  
  public Point2D a(Point2D paramPoint2D, double paramDouble)
  {
    return paramPoint2D;
  }
  
  public double a(double paramDouble)
  {
    return paramDouble * this.b;
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "radius" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\c\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */