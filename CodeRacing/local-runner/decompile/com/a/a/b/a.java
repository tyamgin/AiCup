package com.a.a.b;

import com.a.a.b.c.c;
import com.a.a.b.e.d;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.text.StringUtil;
import java.util.concurrent.atomic.AtomicLong;

public class a
{
  private static final AtomicLong a = new AtomicLong();
  private final long b = a.incrementAndGet();
  private String c;
  private c d;
  private double e;
  private double f;
  private double g;
  private double h;
  private d i = new com.a.a.b.e.b(0.0D);
  private double j;
  private double k = 1.0D;
  private double l;
  private final b m = new b();
  private b n;
  private b o;
  private double p;
  private double q;
  private Double r;
  private double s;
  private double t;
  private Double u;
  
  public long a()
  {
    return this.b;
  }
  
  public String b()
  {
    return this.c;
  }
  
  public void a(String paramString)
  {
    this.c = paramString;
  }
  
  public c c()
  {
    return this.d;
  }
  
  public void a(c paramc)
  {
    this.d = paramc;
  }
  
  public double d()
  {
    return this.e;
  }
  
  public void a(double paramDouble)
  {
    if ((Double.isNaN(paramDouble)) || (paramDouble == Double.NEGATIVE_INFINITY) || (paramDouble <= 0.0D)) {
      throw new IllegalArgumentException(this + ": argument 'mass' should be positive.");
    }
    this.e = paramDouble;
    if (Double.isInfinite(paramDouble)) {
      this.f = 0.0D;
    } else {
      this.f = (1.0D / paramDouble);
    }
  }
  
  public boolean e()
  {
    return Double.isInfinite(this.e);
  }
  
  public double f()
  {
    return this.f;
  }
  
  public double g()
  {
    if ((Double.isNaN(this.e)) || (this.e == Double.NEGATIVE_INFINITY) || (this.e <= 0.0D)) {
      throw new IllegalStateException(this + ": field 'mass' should be positive.");
    }
    if (Double.isInfinite(this.e)) {
      return Double.POSITIVE_INFINITY;
    }
    return this.d.a(this.e);
  }
  
  public double h()
  {
    double d1 = g();
    if (Double.isInfinite(d1)) {
      return 0.0D;
    }
    return 1.0D / d1;
  }
  
  public double i()
  {
    return this.g;
  }
  
  public void b(double paramDouble)
  {
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)) || (paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException(String.format("%s: argument 'movementAirFrictionFactor' should be between 0.0 and 1.0 both inclusive but got %s.", new Object[] { this, Double.valueOf(paramDouble) }));
    }
    this.g = paramDouble;
  }
  
  public double j()
  {
    return this.h;
  }
  
  public void c(double paramDouble)
  {
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)) || (paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException(String.format("%s: argument 'rotationAirFrictionFactor' should be between 0.0 and 1.0 both inclusive but got %s.", new Object[] { this, Double.valueOf(paramDouble) }));
    }
    this.h = paramDouble;
  }
  
  public d k()
  {
    return this.i;
  }
  
  public void a(d paramd)
  {
    if (paramd == null) {
      throw new IllegalArgumentException(String.format("%s: argument 'movementFrictionProvider' is null.", new Object[] { this }));
    }
    this.i = paramd;
  }
  
  public void d(double paramDouble)
  {
    a(new com.a.a.b.e.b(paramDouble));
  }
  
  public void e(double paramDouble)
  {
    this.i.a(this, paramDouble);
  }
  
  public double l()
  {
    return this.j;
  }
  
  public void f(double paramDouble)
  {
    if ((Double.isNaN(paramDouble)) || (paramDouble < 0.0D)) {
      throw new IllegalArgumentException(String.format("%s: argument 'rotationFrictionFactor' should be zero or positive but got %s.", new Object[] { this, Double.valueOf(paramDouble) }));
    }
    this.j = paramDouble;
  }
  
  public double m()
  {
    return this.k;
  }
  
  public void g(double paramDouble)
  {
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)) || (paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException(String.format("%s: argument 'momentumTransferFactor' should be between 0.0 and 1.0 both inclusive but got %s.", new Object[] { this, Double.valueOf(paramDouble) }));
    }
    this.k = paramDouble;
  }
  
  public double n()
  {
    return this.l;
  }
  
  public void h(double paramDouble)
  {
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)) || (paramDouble < 0.0D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException(String.format("%s: argument 'surfaceFrictionFactor' should be between 0.0 and 1.0 both inclusive but got %s.", new Object[] { this, Double.valueOf(paramDouble) }));
    }
    this.l = paramDouble;
  }
  
  public b o()
  {
    return this.m;
  }
  
  public void p()
  {
    this.n = new b(this.m);
  }
  
  public void q()
  {
    this.o = new b(this.m);
  }
  
  public Point2D r()
  {
    return this.m.g();
  }
  
  public void a(double paramDouble1, double paramDouble2)
  {
    Point2D localPoint2D = this.m.g();
    if (localPoint2D == null)
    {
      this.m.a(new Point2D(paramDouble1, paramDouble2));
    }
    else
    {
      localPoint2D.setX(paramDouble1);
      localPoint2D.setY(paramDouble2);
    }
  }
  
  public double s()
  {
    Point2D localPoint2D = this.m.g();
    return localPoint2D == null ? 0.0D : localPoint2D.getX();
  }
  
  public void i(double paramDouble)
  {
    Point2D localPoint2D = this.m.g();
    if (localPoint2D == null) {
      this.m.a(new Point2D(paramDouble, 0.0D));
    } else {
      localPoint2D.setX(paramDouble);
    }
  }
  
  public double t()
  {
    Point2D localPoint2D = this.m.g();
    return localPoint2D == null ? 0.0D : localPoint2D.getY();
  }
  
  public void j(double paramDouble)
  {
    Point2D localPoint2D = this.m.g();
    if (localPoint2D == null) {
      this.m.a(new Point2D(0.0D, paramDouble));
    } else {
      localPoint2D.setY(paramDouble);
    }
  }
  
  public Vector2D u()
  {
    return this.m.a();
  }
  
  public void a(Vector2D paramVector2D)
  {
    this.m.a(paramVector2D);
  }
  
  public void b(double paramDouble1, double paramDouble2)
  {
    Vector2D localVector2D = this.m.a();
    if (localVector2D == null)
    {
      this.m.a(new Vector2D(paramDouble1, paramDouble2));
    }
    else
    {
      localVector2D.setX(paramDouble1);
      localVector2D.setY(paramDouble2);
    }
  }
  
  public Vector2D v()
  {
    return this.m.b();
  }
  
  public void b(Vector2D paramVector2D)
  {
    this.m.b(paramVector2D);
  }
  
  public Vector2D w()
  {
    return this.m.c();
  }
  
  public void c(Vector2D paramVector2D)
  {
    this.m.c(paramVector2D);
  }
  
  public void c(double paramDouble1, double paramDouble2)
  {
    Vector2D localVector2D = this.m.c();
    if (localVector2D == null)
    {
      this.m.c(new Vector2D(paramDouble1, paramDouble2));
    }
    else
    {
      localVector2D.setX(paramDouble1);
      localVector2D.setY(paramDouble2);
    }
  }
  
  public double x()
  {
    return this.m.h();
  }
  
  public void k(double paramDouble)
  {
    this.m.d(paramDouble);
  }
  
  public double y()
  {
    return this.m.d();
  }
  
  public void l(double paramDouble)
  {
    this.m.a(paramDouble);
  }
  
  public double z()
  {
    return this.m.e();
  }
  
  public void m(double paramDouble)
  {
    this.m.b(paramDouble);
  }
  
  public double A()
  {
    return this.m.f();
  }
  
  public void n(double paramDouble)
  {
    this.m.c(paramDouble);
  }
  
  public double a(Point2D paramPoint2D)
  {
    return this.m.g().getDistanceTo(paramPoint2D);
  }
  
  public double a(a parama)
  {
    return this.m.g().getSquaredDistanceTo(parama.m.g());
  }
  
  public Point2D B()
  {
    return this.d == null ? null : this.d.a(this);
  }
  
  public void C()
  {
    this.m.i();
  }
  
  void o(double paramDouble)
  {
    if ((this.r == null) || (!NumberUtil.equals(Double.valueOf(this.g), Double.valueOf(this.p))) || (!NumberUtil.equals(Double.valueOf(paramDouble), Double.valueOf(this.q))))
    {
      this.p = this.g;
      this.q = paramDouble;
      this.r = Double.valueOf(Math.pow(1.0D - this.g, paramDouble));
    }
    u().subtract(v()).multiply(this.r.doubleValue()).add(v());
  }
  
  void p(double paramDouble)
  {
    if ((this.u == null) || (!NumberUtil.equals(Double.valueOf(this.h), Double.valueOf(this.s))) || (!NumberUtil.equals(Double.valueOf(paramDouble), Double.valueOf(this.t))))
    {
      this.s = this.h;
      this.t = paramDouble;
      this.u = Double.valueOf(Math.pow(1.0D - this.h, paramDouble));
    }
    l((y() - z()) * this.u.doubleValue() + z());
  }
  
  public boolean b(a parama)
  {
    return (parama != null) && (this.b == parama.b);
  }
  
  public int hashCode()
  {
    return (int)(this.b ^ this.b >>> 32);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    a locala = (a)paramObject;
    return this.b == locala.b;
  }
  
  public String toString()
  {
    return c(this);
  }
  
  public static String c(a parama)
  {
    return StringUtil.toString(a.class, parama, true, new String[] { "id", "name", "position", "angle", "velocity", "angularVelocity" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */