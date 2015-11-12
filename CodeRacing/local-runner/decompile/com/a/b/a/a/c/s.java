package com.a.b.a.a.c;

import com.codeforces.commons.reflection.Name;
import com.google.gson.annotations.Until;

public abstract class s
  extends u
{
  @Until(1.0D)
  private final double width;
  @Until(1.0D)
  private final double height;
  
  protected s(@Name("id") long paramLong, @Name("mass") double paramDouble1, @Name("x") double paramDouble2, @Name("y") double paramDouble3, @Name("speedX") double paramDouble4, @Name("speedY") double paramDouble5, @Name("angle") double paramDouble6, @Name("angularSpeed") double paramDouble7, @Name("width") double paramDouble8, @Name("height") double paramDouble9)
  {
    super(paramLong, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7);
    this.width = paramDouble8;
    this.height = paramDouble9;
  }
  
  public double getWidth()
  {
    return this.width;
  }
  
  public double getHeight()
  {
    return this.height;
  }
  
  protected static boolean areFieldEquals(s params1, s params2)
  {
    return (u.areFieldEquals(params1, params2)) && (Double.compare(params1.width, params2.width) == 0) && (Double.compare(params1.height, params2.height) == 0);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\s.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */