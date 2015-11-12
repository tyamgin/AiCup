package com.codeforces.commons.geometry;

import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.pair.DoublePair;
import com.codeforces.commons.text.StringUtil;

public class Point2D
  extends DoublePair
{
  public Point2D(double paramDouble1, double paramDouble2)
  {
    super(Double.valueOf(paramDouble1), Double.valueOf(paramDouble2));
  }
  
  public Point2D(Point2D paramPoint2D)
  {
    super(Double.valueOf(paramPoint2D.getX()), Double.valueOf(paramPoint2D.getY()));
  }
  
  public double getX()
  {
    return ((Double)getFirst()).doubleValue();
  }
  
  public void setX(double paramDouble)
  {
    setFirst(Double.valueOf(paramDouble));
  }
  
  public double getY()
  {
    return ((Double)getSecond()).doubleValue();
  }
  
  public void setY(double paramDouble)
  {
    setSecond(Double.valueOf(paramDouble));
  }
  
  public Point2D add(Vector2D paramVector2D)
  {
    setX(getX() + paramVector2D.getX());
    setY(getY() + paramVector2D.getY());
    return this;
  }
  
  public Point2D add(double paramDouble1, double paramDouble2)
  {
    setX(getX() + paramDouble1);
    setY(getY() + paramDouble2);
    return this;
  }
  
  public Point2D subtract(Vector2D paramVector2D)
  {
    setX(getX() - paramVector2D.getX());
    setY(getY() - paramVector2D.getY());
    return this;
  }
  
  public double getDistanceTo(Point2D paramPoint2D)
  {
    return Math.hypot(getX() - paramPoint2D.getX(), getY() - paramPoint2D.getY());
  }
  
  public double getSquaredDistanceTo(Point2D paramPoint2D)
  {
    return Math.sumSqr(getX() - paramPoint2D.getX(), getY() - paramPoint2D.getY());
  }
  
  public Point2D copy()
  {
    return new Point2D(this);
  }
  
  public boolean nearlyEquals(Point2D paramPoint2D, double paramDouble)
  {
    return (paramPoint2D != null) && (NumberUtil.nearlyEquals(Double.valueOf(getX()), Double.valueOf(paramPoint2D.getX()), paramDouble)) && (NumberUtil.nearlyEquals(Double.valueOf(getY()), Double.valueOf(paramPoint2D.getY()), paramDouble));
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "x", "y" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\geometry\Point2D.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */