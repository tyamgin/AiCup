package com.a.c.b.a;

import com.a.a.b.e.b;
import com.a.a.b.e.d;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.reflection.Name;

final class a
  extends com.a.c.c
{
  @Name("body")
  private final com.a.a.b.a a;
  
  a(long paramLong, com.a.a.b.a parama, String paramString, double paramDouble, com.a.c.a.c paramc, boolean paramBoolean)
  {
    super(paramLong);
    this.a = parama;
    a(paramString);
    l(paramDouble);
    a(paramc == null ? null : paramc.d());
    a(paramBoolean);
  }
  
  public double c()
  {
    return this.a.s();
  }
  
  public void a(double paramDouble)
  {
    this.a.i(paramDouble);
  }
  
  public double d()
  {
    return this.a.t();
  }
  
  public void b(double paramDouble)
  {
    this.a.j(paramDouble);
  }
  
  public double e()
  {
    return this.a.x();
  }
  
  public void c(double paramDouble)
  {
    this.a.k(paramDouble);
    this.a.C();
  }
  
  public Vector2D f()
  {
    return this.a.u();
  }
  
  public void a(Vector2D paramVector2D)
  {
    this.a.a(paramVector2D);
  }
  
  public Vector2D g()
  {
    return this.a.v();
  }
  
  public void b(Vector2D paramVector2D)
  {
    this.a.b(paramVector2D);
  }
  
  public double h()
  {
    return this.a.y();
  }
  
  public void d(double paramDouble)
  {
    this.a.l(paramDouble);
  }
  
  public double i()
  {
    return this.a.z();
  }
  
  public void e(double paramDouble)
  {
    this.a.m(paramDouble);
  }
  
  public Vector2D j()
  {
    return this.a.w();
  }
  
  public double k()
  {
    return this.a.A();
  }
  
  public double l()
  {
    return this.a.i();
  }
  
  public void f(double paramDouble)
  {
    this.a.b(paramDouble);
  }
  
  public double m()
  {
    return this.a.j();
  }
  
  public void g(double paramDouble)
  {
    this.a.c(paramDouble);
  }
  
  public double n()
  {
    d locald = this.a.k();
    if ((locald instanceof b)) {
      return ((b)locald).a();
    }
    if ((locald instanceof com.a.a.b.e.a)) {
      return ((com.a.a.b.e.a)locald).a();
    }
    throw new IllegalArgumentException(String.format("Unsupported movement friction provider: %s.", new Object[] { locald }));
  }
  
  public void h(double paramDouble)
  {
    Double localDouble = o();
    if (localDouble == null) {
      this.a.d(paramDouble);
    } else {
      this.a.a(new com.a.a.b.e.a(paramDouble, localDouble.doubleValue()));
    }
  }
  
  public Double o()
  {
    d locald = this.a.k();
    if ((locald instanceof b)) {
      return null;
    }
    if ((locald instanceof com.a.a.b.e.a)) {
      return Double.valueOf(((com.a.a.b.e.a)locald).b());
    }
    throw new IllegalArgumentException(String.format("Unsupported movement friction provider: %s.", new Object[] { locald }));
  }
  
  public void a(Double paramDouble)
  {
    double d = n();
    if (paramDouble == null) {
      this.a.d(d);
    } else {
      this.a.a(new com.a.a.b.e.a(d, paramDouble.doubleValue()));
    }
  }
  
  public double p()
  {
    return this.a.l();
  }
  
  public void i(double paramDouble)
  {
    this.a.f(paramDouble);
  }
  
  public double q()
  {
    return this.a.m();
  }
  
  public void j(double paramDouble)
  {
    this.a.g(paramDouble);
  }
  
  public double r()
  {
    return this.a.n();
  }
  
  public void k(double paramDouble)
  {
    this.a.h(paramDouble);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\c\b\a\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */