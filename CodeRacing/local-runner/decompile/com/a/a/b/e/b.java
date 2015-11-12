package com.a.a.b.e;

import com.a.a.b.a;
import com.codeforces.commons.geometry.Vector2D;

public class b
  implements d
{
  private final double a;
  
  public b(double paramDouble)
  {
    if (paramDouble < 0.0D) {
      throw new IllegalArgumentException("Argument 'movementFrictionFactor' should be zero or positive.");
    }
    this.a = paramDouble;
  }
  
  public double a()
  {
    return this.a;
  }
  
  public void a(a parama, double paramDouble)
  {
    if (this.a <= 0.0D) {
      return;
    }
    double d1 = parama.u().getLength();
    if (d1 <= 0.0D) {
      return;
    }
    double d2 = this.a * paramDouble;
    if (d2 >= d1) {
      parama.b(0.0D, 0.0D);
    } else if (d2 > 0.0D) {
      parama.u().multiply(1.0D - d2 / d1);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\e\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */