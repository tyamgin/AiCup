package com.a.b;

import com.a.c.f;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.reflection.Name;
import com.codeforces.commons.text.StringUtil;
import java.util.concurrent.atomic.AtomicLong;

public abstract class e
{
  private static final AtomicLong a = new AtomicLong();
  @Name("id")
  private final long b = a.incrementAndGet();
  @Name("body")
  private com.a.c.c c = new f(this.b);
  private double d;
  private int e;
  private double f;
  private int g;
  private Double h;
  private Double i;
  private Double j;
  private Vector2D k = new Vector2D(0.0D, 0.0D);
  
  protected e(com.a.c.a.c paramc)
  {
    this.c.a(getClass().getSimpleName() + '#' + this.b);
    this.c.a(paramc);
  }
  
  public final long a()
  {
    return this.b;
  }
  
  public com.a.c.c b()
  {
    return this.c;
  }
  
  public void a(com.a.c.c paramc)
  {
    this.c = paramc;
  }
  
  public double c()
  {
    return Math.sqrt(this.d);
  }
  
  public int d()
  {
    return this.e;
  }
  
  public void a(int paramInt)
  {
    double d1 = this.c.f().getSquaredLength();
    if (this.d < d1)
    {
      this.d = d1;
      this.e = paramInt;
    }
  }
  
  public double e()
  {
    return this.f;
  }
  
  public int f()
  {
    return this.g;
  }
  
  public void b(int paramInt)
  {
    double d1 = Math.abs(this.c.h());
    if (this.f < d1)
    {
      this.f = d1;
      this.g = paramInt;
    }
  }
  
  public Double g()
  {
    return this.h;
  }
  
  public void a(Double paramDouble)
  {
    this.h = paramDouble;
  }
  
  public Double h()
  {
    return this.i;
  }
  
  public void b(Double paramDouble)
  {
    this.i = paramDouble;
  }
  
  public Double i()
  {
    return this.j;
  }
  
  public void c(Double paramDouble)
  {
    this.j = paramDouble;
  }
  
  public Vector2D j()
  {
    return this.k;
  }
  
  public void a(Vector2D paramVector2D)
  {
    this.k = paramVector2D;
  }
  
  public double a(double paramDouble1, double paramDouble2)
  {
    return Math.hypot(paramDouble1 - this.c.c(), paramDouble2 - this.c.d());
  }
  
  public double a(e parame)
  {
    return a(parame.c.c(), parame.c.d());
  }
  
  public final boolean equals(Object paramObject)
  {
    return (this == paramObject) || ((paramObject != null) && (getClass() == paramObject.getClass()) && (this.b == ((e)paramObject).b));
  }
  
  public final int hashCode()
  {
    return (int)(this.b ^ this.b >>> 32);
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "id", "body.name" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */