package com.codeforces.commons.geometry;

import com.codeforces.commons.math.Math;
import com.codeforces.commons.text.StringUtil;

public class Line2D
{
  private final double a;
  private final double b;
  private final double c;
  private final double pseudoLength;
  
  public Line2D(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.a = paramDouble1;
    this.b = paramDouble2;
    this.c = paramDouble3;
    this.pseudoLength = Math.hypot(this.a, this.b);
  }
  
  public Line2D getParallelLine(double paramDouble1, double paramDouble2)
  {
    double d = this.a * paramDouble1 + this.b * paramDouble2 + this.c;
    return new Line2D(this.a, this.b, this.c - d);
  }
  
  public Line2D getParallelLine(Point2D paramPoint2D)
  {
    return getParallelLine(paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public double getDistanceFrom(double paramDouble1, double paramDouble2)
  {
    return Math.abs((this.a * paramDouble1 + this.b * paramDouble2 + this.c) / this.pseudoLength);
  }
  
  public double getDistanceFrom(Point2D paramPoint2D)
  {
    return getDistanceFrom(paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public double getDistanceFrom(Line2D paramLine2D, double paramDouble)
  {
    if (getIntersectionPoint(paramLine2D, paramDouble) != null) {
      return NaN.0D;
    }
    return Math.abs(this.c - paramLine2D.c) / this.pseudoLength;
  }
  
  public double getSignedDistanceFrom(double paramDouble1, double paramDouble2)
  {
    return (this.a * paramDouble1 + this.b * paramDouble2 + this.c) / this.pseudoLength;
  }
  
  public double getSignedDistanceFrom(Point2D paramPoint2D)
  {
    return getSignedDistanceFrom(paramPoint2D.getX(), paramPoint2D.getY());
  }
  
  public Vector2D getUnitNormal()
  {
    return new Vector2D(this.a / this.pseudoLength, this.b / this.pseudoLength);
  }
  
  public Vector2D getUnitNormalFrom(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    double d = getSignedDistanceFrom(paramDouble1, paramDouble2);
    if (d <= -paramDouble3) {
      return new Vector2D(this.a / this.pseudoLength, this.b / this.pseudoLength);
    }
    if (d >= paramDouble3) {
      return new Vector2D(-this.a / this.pseudoLength, -this.b / this.pseudoLength);
    }
    throw new IllegalArgumentException(String.format("Point {x=%s, y=%s} is on the %s.", new Object[] { Double.valueOf(paramDouble1), Double.valueOf(paramDouble2), this }));
  }
  
  public Vector2D getUnitNormalFrom(Point2D paramPoint2D, double paramDouble)
  {
    return getUnitNormalFrom(paramPoint2D.getX(), paramPoint2D.getY(), paramDouble);
  }
  
  public Vector2D getUnitNormalFrom(Point2D paramPoint2D)
  {
    return getUnitNormalFrom(paramPoint2D.getX(), paramPoint2D.getY(), 1.0E-6D);
  }
  
  public Point2D getProjectionOf(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    double d = getDistanceFrom(paramDouble1, paramDouble2);
    if (d < paramDouble3) {
      return new Point2D(paramDouble1, paramDouble2);
    }
    Vector2D localVector2D = getUnitNormalFrom(paramDouble1, paramDouble2, paramDouble3);
    return new Point2D(paramDouble1 + localVector2D.getX() * d, paramDouble2 + localVector2D.getY() * d);
  }
  
  public Point2D getProjectionOf(Point2D paramPoint2D, double paramDouble)
  {
    return getProjectionOf(paramPoint2D.getX(), paramPoint2D.getY(), paramDouble);
  }
  
  public Point2D getProjectionOf(Point2D paramPoint2D)
  {
    return getProjectionOf(paramPoint2D.getX(), paramPoint2D.getY(), 1.0E-6D);
  }
  
  public Point2D getIntersectionPoint(Line2D paramLine2D, double paramDouble)
  {
    double d = this.a * paramLine2D.b - paramLine2D.a * this.b;
    return Math.abs(d) < Math.abs(paramDouble) ? null : new Point2D((this.b * paramLine2D.c - paramLine2D.b * this.c) / d, (paramLine2D.a * this.c - this.a * paramLine2D.c) / d);
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "a", "b", "c" });
  }
  
  public static Line2D getLineByTwoPoints(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    return new Line2D(paramDouble4 - paramDouble2, paramDouble1 - paramDouble3, (paramDouble2 - paramDouble4) * paramDouble1 + (paramDouble3 - paramDouble1) * paramDouble2);
  }
  
  public static Line2D getLineByTwoPoints(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    return getLineByTwoPoints(paramPoint2D1.getX(), paramPoint2D1.getY(), paramPoint2D2.getX(), paramPoint2D2.getY());
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\geometry\Line2D.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */