package com.a.a.b.c;

import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.text.StringUtil;

public class d
  extends g
{
  private final double a;
  private final double b;
  private final double c;
  private double d;
  private double e;
  private Double f;
  private Double g;
  
  public d(double paramDouble, boolean paramBoolean)
  {
    super(f.c, paramBoolean);
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)) || (paramDouble <= 0.0D)) {
      throw new IllegalArgumentException(String.format("Argument 'length' should be positive finite number but got %s.", new Object[] { Double.valueOf(paramDouble) }));
    }
    this.a = paramDouble;
    this.b = (paramDouble / 2.0D);
    this.c = (paramDouble * paramDouble / 12.0D);
  }
  
  public Point2D a(Point2D paramPoint2D, double paramDouble1, double paramDouble2)
  {
    b(paramDouble1, paramDouble2);
    return new Point2D(paramPoint2D.getX() - this.f.doubleValue(), paramPoint2D.getY() - this.g.doubleValue());
  }
  
  public Point2D b(Point2D paramPoint2D, double paramDouble1, double paramDouble2)
  {
    b(paramDouble1, paramDouble2);
    return new Point2D(paramPoint2D.getX() + this.f.doubleValue(), paramPoint2D.getY() + this.g.doubleValue());
  }
  
  public double d()
  {
    return this.b;
  }
  
  public Point2D a(Point2D paramPoint2D, double paramDouble)
  {
    return paramPoint2D;
  }
  
  public double a(double paramDouble)
  {
    return paramDouble * this.c;
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "length" });
  }
  
  private void b(double paramDouble1, double paramDouble2)
  {
    if ((this.f == null) || (this.g == null) || (paramDouble1 != this.d) || (paramDouble2 != this.e))
    {
      if ((Double.isNaN(paramDouble1)) || (Double.isInfinite(paramDouble1))) {
        throw new IllegalArgumentException("Argument 'angle' is not a finite number.");
      }
      if ((Double.isNaN(paramDouble2)) || (Double.isInfinite(paramDouble2)) || (paramDouble2 < 1.0E-100D) || (paramDouble2 > 1.0D)) {
        throw new IllegalArgumentException("Argument 'epsilon' should be between 1.0E-100 and 1.0.");
      }
      this.d = paramDouble1;
      this.e = paramDouble2;
      if (Math.abs(this.a) < paramDouble2)
      {
        this.f = Double.valueOf(0.0D);
        this.g = Double.valueOf(0.0D);
      }
      else
      {
        if (Math.abs(1.5707963267948966D - Math.abs(paramDouble1)) < paramDouble2) {
          this.f = Double.valueOf(0.0D);
        } else {
          this.f = Double.valueOf(a(Math.cos(paramDouble1), paramDouble2) * this.b);
        }
        if ((Math.abs(3.141592653589793D - Math.abs(paramDouble1)) < paramDouble2) || (Math.abs(paramDouble1) < paramDouble2)) {
          this.g = Double.valueOf(0.0D);
        } else {
          this.g = Double.valueOf(a(Math.sin(paramDouble1), paramDouble2) * this.b);
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\c\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */