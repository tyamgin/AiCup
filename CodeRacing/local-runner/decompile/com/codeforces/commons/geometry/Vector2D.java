package com.codeforces.commons.geometry;

import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.pair.DoublePair;
import com.codeforces.commons.text.StringUtil;
import org.apache.commons.math3.util.MathArrays;

public class Vector2D
  extends DoublePair
{
  public Vector2D(double paramDouble1, double paramDouble2)
  {
    super(Double.valueOf(paramDouble1), Double.valueOf(paramDouble2));
  }
  
  public Vector2D(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    super(Double.valueOf(paramDouble3 - paramDouble1), Double.valueOf(paramDouble4 - paramDouble2));
  }
  
  public Vector2D(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    super(Double.valueOf(paramPoint2D2.getX() - paramPoint2D1.getX()), Double.valueOf(paramPoint2D2.getY() - paramPoint2D1.getY()));
  }
  
  public Vector2D(Vector2D paramVector2D)
  {
    super(Double.valueOf(paramVector2D.getX()), Double.valueOf(paramVector2D.getY()));
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
  
  public Vector2D add(Vector2D paramVector2D)
  {
    setX(getX() + paramVector2D.getX());
    setY(getY() + paramVector2D.getY());
    return this;
  }
  
  public Vector2D add(double paramDouble1, double paramDouble2)
  {
    setX(getX() + paramDouble1);
    setY(getY() + paramDouble2);
    return this;
  }
  
  public Vector2D subtract(Vector2D paramVector2D)
  {
    setX(getX() - paramVector2D.getX());
    setY(getY() - paramVector2D.getY());
    return this;
  }
  
  public Vector2D multiply(double paramDouble)
  {
    setX(paramDouble * getX());
    setY(paramDouble * getY());
    return this;
  }
  
  public Vector2D rotate(double paramDouble)
  {
    double d1 = Math.cos(paramDouble);
    double d2 = Math.sin(paramDouble);
    double d3 = getX();
    double d4 = getY();
    setX(d3 * d1 - d4 * d2);
    setY(d3 * d2 + d4 * d1);
    return this;
  }
  
  public double dotProduct(Vector2D paramVector2D)
  {
    return MathArrays.linearCombination(getX(), paramVector2D.getX(), getY(), paramVector2D.getY());
  }
  
  public Vector2D negate()
  {
    setX(-getX());
    setY(-getY());
    return this;
  }
  
  public Vector2D normalize()
  {
    double d = getLength();
    if (d == 0.0D) {
      throw new IllegalStateException("Can't set angle of zero-width vector.");
    }
    setX(getX() / d);
    setY(getY() / d);
    return this;
  }
  
  public double getAngle()
  {
    return Math.atan2(getY(), getX());
  }
  
  public Vector2D setAngle(double paramDouble)
  {
    double d = getLength();
    if (d == 0.0D) {
      throw new IllegalStateException("Can't set angle of zero-width vector.");
    }
    setX(Math.cos(paramDouble) * d);
    setY(Math.sin(paramDouble) * d);
    return this;
  }
  
  public double getLength()
  {
    return Math.hypot(getX(), getY());
  }
  
  public Vector2D setLength(double paramDouble)
  {
    double d = getLength();
    if (d == 0.0D) {
      throw new IllegalStateException("Can't resize zero-width vector.");
    }
    return multiply(paramDouble / d);
  }
  
  public double getSquaredLength()
  {
    return getX() * getX() + getY() * getY();
  }
  
  public Vector2D copy()
  {
    return new Vector2D(this);
  }
  
  public Vector2D copyNegate()
  {
    return new Vector2D(-getX(), -getY());
  }
  
  public boolean nearlyEquals(Vector2D paramVector2D, double paramDouble)
  {
    return (paramVector2D != null) && (NumberUtil.nearlyEquals(Double.valueOf(getX()), Double.valueOf(paramVector2D.getX()), paramDouble)) && (NumberUtil.nearlyEquals(Double.valueOf(getY()), Double.valueOf(paramVector2D.getY()), paramDouble));
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "x", "y" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\geometry\Vector2D.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */