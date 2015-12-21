package com.a.c;

import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.reflection.Name;
import com.codeforces.commons.text.StringUtil;
import java.util.Collections;
import java.util.Set;

public abstract class c
{
  @Name("id")
  private final long a;
  @Name("name")
  private String b;
  @Name("mass")
  private double c;
  @Name("form")
  private com.a.c.a.c d;
  @Name("staticBody")
  private boolean e;
  @Name("excludedBodyIds")
  private Set f;
  
  protected c(long paramLong)
  {
    this.a = paramLong;
  }
  
  public final long a()
  {
    return this.a;
  }
  
  public final String b()
  {
    return this.b;
  }
  
  public final void a(String paramString)
  {
    this.b = paramString;
  }
  
  public abstract double c();
  
  public abstract void a(double paramDouble);
  
  public abstract double d();
  
  public abstract void b(double paramDouble);
  
  public abstract double e();
  
  public abstract void c(double paramDouble);
  
  public abstract Vector2D f();
  
  public abstract void a(Vector2D paramVector2D);
  
  public abstract Vector2D g();
  
  public abstract void b(Vector2D paramVector2D);
  
  public abstract double h();
  
  public abstract void d(double paramDouble);
  
  public abstract double i();
  
  public abstract void e(double paramDouble);
  
  public abstract Vector2D j();
  
  public abstract double k();
  
  public abstract double l();
  
  public abstract void f(double paramDouble);
  
  public abstract double m();
  
  public abstract void g(double paramDouble);
  
  public abstract double n();
  
  public abstract void h(double paramDouble);
  
  public abstract Double o();
  
  public abstract void a(Double paramDouble);
  
  public abstract double p();
  
  public abstract void i(double paramDouble);
  
  public abstract double q();
  
  public abstract void j(double paramDouble);
  
  public abstract double r();
  
  public abstract void k(double paramDouble);
  
  public double s()
  {
    return this.c;
  }
  
  public void l(double paramDouble)
  {
    this.c = paramDouble;
  }
  
  public com.a.c.a.c t()
  {
    return this.d;
  }
  
  public void a(com.a.c.a.c paramc)
  {
    this.d = paramc;
  }
  
  public boolean u()
  {
    return this.e;
  }
  
  public void a(boolean paramBoolean)
  {
    this.e = paramBoolean;
  }
  
  public Set v()
  {
    return this.f == null ? Collections.emptySet() : Collections.unmodifiableSet(this.f);
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[] { "id", "name", "x", "y" });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\c\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */