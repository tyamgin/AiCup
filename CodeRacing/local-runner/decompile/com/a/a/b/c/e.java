package com.a.a.b.c;

import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.text.StringUtil;

public class e
  extends c
{
  private final double a;
  private final double b;
  private final double c;
  private final double d;
  private final double e;
  private final double f;
  
  public e(double paramDouble1, double paramDouble2)
  {
    super(f.b);
    if ((Double.isNaN(paramDouble1)) || (Double.isInfinite(paramDouble1)) || (paramDouble1 <= 0.0D)) {
      throw new IllegalArgumentException(String.format("Argument 'width' should be positive finite number but got %s.", new Object[] { Double.valueOf(paramDouble1) }));
    }
    if ((Double.isNaN(paramDouble2)) || (Double.isInfinite(paramDouble2)) || (paramDouble2 <= 0.0D)) {
      throw new IllegalArgumentException(String.format("Argument 'height' should be positive finite number but got %s.", new Object[] { Double.valueOf(paramDouble2) }));
    }
    this.a = paramDouble1;
    this.b = paramDouble2;
    this.c = (paramDouble1 / 2.0D);
    this.d = (paramDouble2 / 2.0D);
    this.e = (Math.hypot(paramDouble1, paramDouble2) / 2.0D);
    this.f = (Math.sumSqr(paramDouble1, paramDouble2) / 12.0D);
  }
  
  public double a()
  {
    return this.a;
  }
  
  public double b()
  {
    return this.b;
  }
  
  public Point2D[] a(Point2D paramPoint2D, double paramDouble1, double paramDouble2)
  {
    if ((Double.isNaN(paramDouble1)) || (Double.isInfinite(paramDouble1))) {
      throw new IllegalArgumentException("Argument 'angle' is not a finite number.");
    }
    if ((Double.isNaN(paramDouble2)) || (Double.isInfinite(paramDouble2)) || (paramDouble2 < 1.0E-100D) || (paramDouble2 > 1.0D)) {
      throw new IllegalArgumentException("Argument 'epsilon' should be between 1.0E-100 and 1.0.");
    }
    double d1 = a(Math.sin(paramDouble1), paramDouble2);
    double d2 = a(Math.cos(paramDouble1), paramDouble2);
    double d3 = d2 * this.c;
    double d4 = d1 * this.c;
    double d5 = d1 * this.d;
    double d6 = -d2 * this.d;
    return new Point2D[] { new Point2D(paramPoint2D.getX() - d3 + d5, paramPoint2D.getY() - d4 + d6), new Point2D(paramPoint2D.getX() + d3 + d5, paramPoint2D.getY() + d4 + d6), new Point2D(paramPoint2D.getX() + d3 - d5, paramPoint2D.getY() + d4 - d6), new Point2D(paramPoint2D.getX() - d3 - d5, paramPoint2D.getY() - d4 - d6) };
  }
  
  public double d()
  {
    return this.e;
  }
  
  public Point2D a(Point2D paramPoint2D, double paramDouble)
  {
    return paramPoint2D;
  }
  
  public double a(double paramDouble)
  {
    return paramDouble * this.f;
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "width", "height" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\c\e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */