package com.a.a.b.e;

import com.codeforces.commons.geometry.Vector2D;

public class a
  implements d
{
  private final double a;
  private final double b;
  
  public a(double paramDouble1, double paramDouble2)
  {
    if (paramDouble1 < 0.0D) {
      throw new IllegalArgumentException("Argument 'lengthwiseMovementFrictionFactor' should be zero or positive.");
    }
    if (paramDouble2 < 0.0D) {
      throw new IllegalArgumentException("Argument 'crosswiseMovementFrictionFactor' should be zero or positive.");
    }
    this.a = paramDouble1;
    this.b = paramDouble2;
  }
  
  public double a()
  {
    return this.a;
  }
  
  public double b()
  {
    return this.b;
  }
  
  public void a(com.a.a.b.a parama, double paramDouble)
  {
    double d1 = parama.u().getLength();
    if (d1 <= 0.0D) {
      return;
    }
    double d2 = this.a * paramDouble;
    double d3 = this.b * paramDouble;
    Vector2D localVector2D1 = new Vector2D(1.0D, 0.0D).rotate(parama.x());
    Vector2D localVector2D2 = new Vector2D(0.0D, 1.0D).rotate(parama.x());
    double d4 = parama.u().dotProduct(localVector2D1);
    if (d4 >= 0.0D)
    {
      d4 -= d2;
      if (d4 < 0.0D) {
        d4 = 0.0D;
      }
    }
    else
    {
      d4 += d2;
      if (d4 > 0.0D) {
        d4 = 0.0D;
      }
    }
    double d5 = parama.u().dotProduct(localVector2D2);
    if (d5 >= 0.0D)
    {
      d5 -= d3;
      if (d5 < 0.0D) {
        d5 = 0.0D;
      }
    }
    else
    {
      d5 += d3;
      if (d5 > 0.0D) {
        d5 = 0.0D;
      }
    }
    parama.a(localVector2D1.copy().multiply(d4).add(localVector2D2.copy().multiply(d5)));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\e\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */