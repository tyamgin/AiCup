package com.a.c;

import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.reflection.Name;

public final class f
  extends c
{
  @Name("x")
  private double a;
  @Name("y")
  private double b;
  @Name("angle")
  private double c;
  @Name("speed")
  private Vector2D d = new Vector2D(0.0D, 0.0D);
  @Name("medianSpeed")
  private Vector2D e = new Vector2D(0.0D, 0.0D);
  @Name("angularSpeed")
  private double f;
  @Name("angularSpeed")
  private double g;
  @Name("force")
  private Vector2D h = new Vector2D(0.0D, 0.0D);
  @Name("torque")
  private double i;
  @Name("movementAirFrictionFactor")
  private double j;
  @Name("rotationAirFrictionFactor")
  private double k;
  @Name("movementFrictionFactor")
  private double l;
  @Name("crosswiseMovementFrictionFactor")
  private Double m;
  @Name("rotationFrictionFactor")
  private double n;
  @Name("momentumTransferFactor")
  private double o = 1.0D;
  @Name("surfaceFriction")
  private double p;
  
  public f(long paramLong)
  {
    super(paramLong);
  }
  
  public double c()
  {
    return this.a;
  }
  
  public void a(double paramDouble)
  {
    this.a = paramDouble;
  }
  
  public double d()
  {
    return this.b;
  }
  
  public void b(double paramDouble)
  {
    this.b = paramDouble;
  }
  
  public double e()
  {
    return this.c;
  }
  
  public void c(double paramDouble)
  {
    while (paramDouble > 3.141592653589793D) {
      paramDouble -= 6.283185307179586D;
    }
    while (paramDouble < -3.141592653589793D) {
      paramDouble += 6.283185307179586D;
    }
    this.c = paramDouble;
  }
  
  public Vector2D f()
  {
    return this.d;
  }
  
  public void a(Vector2D paramVector2D)
  {
    this.d = paramVector2D;
  }
  
  public Vector2D g()
  {
    return this.e;
  }
  
  public void b(Vector2D paramVector2D)
  {
    this.e = paramVector2D;
  }
  
  public double h()
  {
    return this.f;
  }
  
  public void d(double paramDouble)
  {
    this.f = paramDouble;
  }
  
  public double i()
  {
    return this.g;
  }
  
  public void e(double paramDouble)
  {
    this.g = paramDouble;
  }
  
  public Vector2D j()
  {
    return this.h;
  }
  
  public double k()
  {
    return this.i;
  }
  
  public double l()
  {
    return this.j;
  }
  
  public void f(double paramDouble)
  {
    this.j = paramDouble;
  }
  
  public double m()
  {
    return this.k;
  }
  
  public void g(double paramDouble)
  {
    this.k = paramDouble;
  }
  
  public double n()
  {
    return this.l;
  }
  
  public void h(double paramDouble)
  {
    this.l = paramDouble;
  }
  
  public Double o()
  {
    return this.m;
  }
  
  public void a(Double paramDouble)
  {
    this.m = paramDouble;
  }
  
  public double p()
  {
    return this.n;
  }
  
  public void i(double paramDouble)
  {
    this.n = paramDouble;
  }
  
  public double q()
  {
    return this.o;
  }
  
  public void j(double paramDouble)
  {
    this.o = paramDouble;
  }
  
  public double r()
  {
    return this.p;
  }
  
  public void k(double paramDouble)
  {
    this.p = paramDouble;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\c\f.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */