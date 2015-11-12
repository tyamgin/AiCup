package com.a.a.b.c;

import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.text.StringUtil;

public class a
  extends g
{
  private final double a;
  private final double b;
  private final double c;
  
  public a(double paramDouble1, double paramDouble2, double paramDouble3, boolean paramBoolean)
  {
    super(f.d, paramBoolean);
    if ((Double.isNaN(paramDouble1)) || (Double.isInfinite(paramDouble1)) || (paramDouble1 <= 0.0D)) {
      throw new IllegalArgumentException(String.format("Argument 'radius' should be a positive finite number but got %s.", new Object[] { Double.valueOf(paramDouble1) }));
    }
    if ((Double.isNaN(paramDouble2)) || (Double.isInfinite(paramDouble2))) {
      throw new IllegalArgumentException(String.format("Argument 'angle' should be a finite number but got %s.", new Object[] { Double.valueOf(paramDouble2) }));
    }
    if ((Double.isNaN(paramDouble3)) || (Double.isInfinite(paramDouble3)) || (paramDouble3 <= 0.0D) || (paramDouble3 > 6.283185307179586D)) {
      throw new IllegalArgumentException(String.format("Argument 'sector' should be between 0.0 exclusive and 2 * PI inclusive but got %s.", new Object[] { Double.valueOf(paramDouble3) }));
    }
    this.b = com.a.a.b.f.a.a(paramDouble2);
    this.a = paramDouble1;
    this.c = paramDouble3;
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
    if ((Double.isInfinite(paramDouble)) && (paramDouble != Double.NEGATIVE_INFINITY)) {
      return paramDouble;
    }
    throw new IllegalArgumentException("Arc form is only supported for static bodies.");
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "radius", "angle", "sector" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\c\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */