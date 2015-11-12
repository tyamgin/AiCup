package com.a.b.a.a.c;

import com.codeforces.commons.reflection.Name;

public class n
  extends e
{
  private final int remainingLifetime;
  
  public n(@Name("id") long paramLong, @Name("mass") double paramDouble1, @Name("x") double paramDouble2, @Name("y") double paramDouble3, @Name("speedX") double paramDouble4, @Name("speedY") double paramDouble5, @Name("angle") double paramDouble6, @Name("angularSpeed") double paramDouble7, @Name("radius") double paramDouble8, @Name("remainingLifetime") int paramInt)
  {
    super(paramLong, paramDouble1, paramDouble2, paramDouble3, paramDouble4, paramDouble5, paramDouble6, paramDouble7, paramDouble8);
    this.remainingLifetime = paramInt;
  }
  
  public int getRemainingLifetime()
  {
    return this.remainingLifetime;
  }
  
  public static boolean areFieldEquals(n paramn1, n paramn2)
  {
    return (paramn1 == paramn2) || ((paramn1 != null) && (paramn2 != null) && (e.areFieldEquals(paramn1, paramn2)) && (paramn1.remainingLifetime == paramn2.remainingLifetime));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\c\n.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */