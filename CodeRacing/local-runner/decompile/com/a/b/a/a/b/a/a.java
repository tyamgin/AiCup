package com.a.b.a.a.b.a;

import com.a.b.a.a.c.k;
import com.a.b.e;
import com.a.c.c;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class a
{
  private static final AtomicLong a = new AtomicLong();
  private final long b = a.incrementAndGet();
  private final k c;
  private final int d;
  private int e;
  private e f;
  private Double g;
  private Double h;
  private Double i;
  private final Map j;
  
  public a(k paramk, int paramInt, double paramDouble1, double paramDouble2, double paramDouble3)
  {
    this.c = paramk;
    this.d = paramInt;
    this.g = Double.valueOf(paramDouble1);
    this.h = Double.valueOf(paramDouble2);
    this.i = Double.valueOf(paramDouble3);
    this.j = new HashMap();
  }
  
  public a(k paramk, int paramInt, e parame)
  {
    this.c = paramk;
    this.d = paramInt;
    this.f = parame;
    this.g = Double.valueOf(0.0D);
    this.h = Double.valueOf(0.0D);
    this.i = Double.valueOf(0.0D);
    this.j = new HashMap();
  }
  
  public a(k paramk, int paramInt, e parame, Map paramMap)
  {
    this.c = paramk;
    this.d = paramInt;
    this.f = parame;
    this.g = Double.valueOf(0.0D);
    this.h = Double.valueOf(0.0D);
    this.i = Double.valueOf(0.0D);
    this.j = new HashMap(paramMap);
  }
  
  public final long a()
  {
    return this.b;
  }
  
  public k b()
  {
    return this.c;
  }
  
  public int c()
  {
    return this.d;
  }
  
  public int d()
  {
    return this.e;
  }
  
  public void e()
  {
    this.e += 1;
  }
  
  public boolean f()
  {
    return this.e >= this.c.getDuration();
  }
  
  public e g()
  {
    return this.f;
  }
  
  public Double h()
  {
    return (this.f == null) || (this.g == null) ? this.g : Double.valueOf(this.f.b().c() + this.g.doubleValue());
  }
  
  public Double i()
  {
    return (this.f == null) || (this.h == null) ? this.h : Double.valueOf(this.f.b().d() + this.h.doubleValue());
  }
  
  public Double j()
  {
    return (this.f == null) || (this.i == null) ? this.i : Double.valueOf(this.f.b().e() + this.i.doubleValue());
  }
  
  public Map k()
  {
    return Collections.unmodifiableMap(this.j);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\a\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */