package com.a.b.a.a.c;

import com.codeforces.commons.reflection.Name;
import com.google.gson.annotations.Until;

public abstract class e
  extends u
{
  @Until(1.0D)
  private final double radius;
  
  protected e(@Name("id") long paramLong, @Name("mass") double paramDouble1, @Name("x") double paramDouble2, @Name("y") double paramDouble3, @Name("speedX") double paramDouble4, @Name("speedY") double paramDouble5, @Name("angle") double paramDouble6, @Name("angularSpeed") double paramDouble7, @Name("radius") double paramDouble8)
  {
    super(paramLong, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7);
    this.radius = paramDouble8;
  }
  
  public double getRadius()
  {
    return this.radius;
  }
  
  protected static boolean areFieldEquals(e parame1, e parame2)
  {
    return (u.areFieldEquals(parame1, parame2)) && (Double.compare(parame1.radius, parame2.radius) == 0);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */