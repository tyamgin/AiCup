package com.a.b.a.a.b.d.c;

import com.a.b.a.a.b.n;
import com.a.b.a.a.c.d;
import com.a.c.c;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.pair.IntPair;
import com.codeforces.commons.reflection.Name;
import com.codeforces.commons.text.StringUtil;

public abstract class b
  extends com.a.b.e
{
  @Name("player")
  private final n a;
  private final int b;
  @Name("type")
  private final d c;
  private double d;
  private Integer e;
  private double f;
  private double g;
  private final double h;
  private final double i;
  private IntPair j;
  private int k;
  private int l;
  private int m;
  private int n;
  private int o;
  private int p;
  private int q;
  private int r;
  private int s;
  private int t;
  private boolean u;
  private Integer v;
  private int w;
  private Integer x;
  
  protected b(n paramn, int paramInt, d paramd, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5, double paramDouble6, IntPair paramIntPair)
  {
    super(new com.a.c.a.e(210.0D, 140.0D));
    if ((Double.isNaN(paramDouble4)) || (Double.isInfinite(paramDouble4)) || (paramDouble4 <= 0.0D)) {
      throw new IllegalArgumentException("Argument 'mass' is not a positive number.");
    }
    this.a = paramn;
    this.b = paramInt;
    this.c = paramd;
    this.d = 1.0D;
    this.f = 0.0D;
    this.g = 0.0D;
    this.h = paramDouble5;
    this.i = paramDouble6;
    this.j = new IntPair(paramIntPair);
    this.k = 1;
    this.l = 1;
    this.m = 1;
    this.n = 1;
    b().a(paramDouble1);
    b().b(paramDouble2);
    b().c(paramDouble3);
    b().l(paramDouble4);
    b().f(0.0075D);
    b().g(0.0075D);
    b().h(0.001D);
    b().a(Double.valueOf(0.25D));
    b().i(0.008726646259971648D);
    b().j(0.5D);
    b().k(0.25D);
  }
  
  public n k()
  {
    return this.a;
  }
  
  public int l()
  {
    return this.b;
  }
  
  public d m()
  {
    return this.c;
  }
  
  public double n()
  {
    return this.d;
  }
  
  public void a(double paramDouble)
  {
    this.d = paramDouble;
  }
  
  public Integer o()
  {
    return this.e;
  }
  
  public void a(Integer paramInteger)
  {
    this.e = paramInteger;
  }
  
  public double p()
  {
    return this.f;
  }
  
  public void a(double paramDouble, boolean paramBoolean)
  {
    this.f = Math.min(Math.max(paramDouble, -1.0D), paramBoolean ? 2.0D : 1.0D);
  }
  
  public void b(double paramDouble)
  {
    a(this.f, false);
    paramDouble = Math.min(Math.max(paramDouble, -1.0D), 1.0D);
    double d1 = paramDouble - this.f;
    if (d1 > 0.025D) {
      d1 = 0.025D;
    } else if (d1 < -0.025D) {
      d1 = -0.025D;
    }
    a(this.f + d1, false);
  }
  
  public double q()
  {
    return this.g;
  }
  
  public void c(double paramDouble)
  {
    this.g = Math.min(Math.max(paramDouble, -1.0D), 1.0D);
  }
  
  public void d(double paramDouble)
  {
    c(this.g);
    paramDouble = Math.min(Math.max(paramDouble, -1.0D), 1.0D);
    double d1 = paramDouble - this.g;
    if (d1 > 0.05D) {
      d1 = 0.05D;
    } else if (d1 < -0.05D) {
      d1 = -0.05D;
    }
    c(this.g + d1);
  }
  
  public double r()
  {
    return this.h;
  }
  
  public double s()
  {
    return this.i;
  }
  
  public IntPair t()
  {
    return new IntPair(this.j);
  }
  
  public void a(IntPair paramIntPair)
  {
    this.j = new IntPair(paramIntPair);
  }
  
  public int u()
  {
    return this.k;
  }
  
  public void c(int paramInt)
  {
    this.k = paramInt;
  }
  
  public int v()
  {
    return this.l;
  }
  
  public void d(int paramInt)
  {
    this.l = paramInt;
  }
  
  public int w()
  {
    return this.m;
  }
  
  public void e(int paramInt)
  {
    this.m = paramInt;
  }
  
  public int x()
  {
    return this.n;
  }
  
  public void f(int paramInt)
  {
    this.n = paramInt;
  }
  
  public int y()
  {
    return this.o;
  }
  
  public void g(int paramInt)
  {
    this.o = paramInt;
  }
  
  public void z()
  {
    if (this.o > 0) {
      this.o -= 1;
    }
  }
  
  public int A()
  {
    return this.p;
  }
  
  public void h(int paramInt)
  {
    this.p = paramInt;
  }
  
  public void B()
  {
    if (this.p > 0) {
      this.p -= 1;
    }
  }
  
  public int C()
  {
    return this.q;
  }
  
  public void i(int paramInt)
  {
    this.q = paramInt;
  }
  
  public void D()
  {
    if (this.q > 0) {
      this.q -= 1;
    }
  }
  
  public int E()
  {
    return this.r;
  }
  
  public void j(int paramInt)
  {
    this.r = paramInt;
  }
  
  public void F()
  {
    if (this.r > 0) {
      this.r -= 1;
    }
  }
  
  public int G()
  {
    return this.s;
  }
  
  public void k(int paramInt)
  {
    this.s = paramInt;
  }
  
  public void H()
  {
    if (this.s > 0) {
      this.s -= 1;
    }
  }
  
  public int I()
  {
    return this.t;
  }
  
  public void l(int paramInt)
  {
    this.t = paramInt;
  }
  
  public void J()
  {
    if (this.t > 0) {
      this.t -= 1;
    }
  }
  
  public boolean K()
  {
    return this.u;
  }
  
  public void a(boolean paramBoolean)
  {
    this.u = paramBoolean;
  }
  
  public Integer L()
  {
    return this.v;
  }
  
  public void b(Integer paramInteger)
  {
    this.v = paramInteger;
  }
  
  public Integer M()
  {
    return this.x;
  }
  
  public boolean N()
  {
    return this.x != null;
  }
  
  public void m(int paramInt)
  {
    this.l += 1;
    this.m += 1;
    this.n += 1;
    this.w += 1;
    if (this.w >= 4) {
      this.x = Integer.valueOf(paramInt);
    }
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "id", "player.name", "type" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\d\c\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */