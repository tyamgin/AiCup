package com.a.a.b.c;

import com.a.a.b.a;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.math.Math;

public abstract class c
{
  private final f a;
  
  protected c(f paramf)
  {
    if (paramf == null) {
      throw new IllegalArgumentException("Argument 'shape' is null.");
    }
    this.a = paramf;
  }
  
  public f e()
  {
    return this.a;
  }
  
  public abstract double d();
  
  public abstract Point2D a(Point2D paramPoint2D, double paramDouble);
  
  public final Point2D a(a parama)
  {
    return a(parama.r(), parama.x());
  }
  
  public abstract double a(double paramDouble);
  
  public abstract String toString();
  
  public static String a(c paramc)
  {
    return paramc == null ? "Form {null}" : paramc.toString();
  }
  
  protected static double a(double paramDouble1, double paramDouble2)
  {
    return Math.abs(-1.0D - paramDouble1) < paramDouble2 ? -1.0D : Math.abs(1.0D - paramDouble1) < paramDouble2 ? 1.0D : Math.abs(paramDouble1) < paramDouble2 ? 0.0D : paramDouble1;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\c\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */