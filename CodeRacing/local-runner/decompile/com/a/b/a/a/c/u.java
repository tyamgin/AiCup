package com.a.b.a.a.c;

import com.codeforces.commons.math.Math;
import com.codeforces.commons.reflection.Name;
import com.google.gson.annotations.Until;

public abstract class u
{
  private final long id;
  @Until(1.0D)
  private final double mass;
  private final double x;
  private final double y;
  private final double speedX;
  private final double speedY;
  private final double angle;
  private final double angularSpeed;
  
  protected u(@Name("id") long paramLong, @Name("mass") double paramDouble1, @Name("x") double paramDouble2, @Name("y") double paramDouble3, @Name("speedX") double paramDouble4, @Name("speedY") double paramDouble5, @Name("angle") double paramDouble6, @Name("angularSpeed") double paramDouble7)
  {
    this.id = paramLong;
    this.mass = paramDouble1;
    this.x = paramDouble2;
    this.y = paramDouble3;
    this.speedX = paramDouble4;
    this.speedY = paramDouble5;
    this.angle = paramDouble6;
    this.angularSpeed = paramDouble7;
  }
  
  public long getId()
  {
    return this.id;
  }
  
  public double getMass()
  {
    return this.mass;
  }
  
  public final double getX()
  {
    return this.x;
  }
  
  public final double getY()
  {
    return this.y;
  }
  
  public final double getSpeedX()
  {
    return this.speedX;
  }
  
  public final double getSpeedY()
  {
    return this.speedY;
  }
  
  public final double getAngle()
  {
    return this.angle;
  }
  
  public double getAngularSpeed()
  {
    return this.angularSpeed;
  }
  
  public double getAngleTo(double paramDouble1, double paramDouble2)
  {
    double d1 = Math.atan2(paramDouble2 - this.y, paramDouble1 - this.x);
    for (double d2 = d1 - this.angle; d2 > 3.141592653589793D; d2 -= 6.283185307179586D) {}
    while (d2 < -3.141592653589793D) {
      d2 += 6.283185307179586D;
    }
    return d2;
  }
  
  public double getAngleTo(u paramu)
  {
    return getAngleTo(paramu.x, paramu.y);
  }
  
  public double getDistanceTo(double paramDouble1, double paramDouble2)
  {
    return Math.hypot(paramDouble1 - this.x, paramDouble2 - this.y);
  }
  
  public double getDistanceTo(u paramu)
  {
    return getDistanceTo(paramu.x, paramu.y);
  }
  
  protected static boolean areFieldEquals(u paramu1, u paramu2)
  {
    return (paramu1.id == paramu2.id) && (Double.compare(paramu1.mass, paramu2.mass) == 0) && (Double.compare(paramu1.x, paramu2.x) == 0) && (Double.compare(paramu1.y, paramu2.y) == 0) && (Double.compare(paramu1.speedX, paramu2.speedX) == 0) && (Double.compare(paramu1.speedY, paramu2.speedY) == 0) && (Double.compare(paramu1.angle, paramu2.angle) == 0) && (Double.compare(paramu1.angularSpeed, paramu2.angularSpeed) == 0);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\u.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */