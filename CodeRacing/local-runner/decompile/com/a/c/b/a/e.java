package com.a.c.b.a;

import com.a.a.b.g;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.text.StringUtil;
import java.util.Set;

class e
{
  static com.a.a.b.a a(com.a.c.c paramc)
  {
    com.a.a.b.a locala = new com.a.a.b.a();
    locala.a(paramc.b());
    com.a.c.a.c localc = paramc.t();
    locala.a(a(localc));
    if ((localc instanceof com.a.c.a.d))
    {
      com.a.c.a.d locald = (com.a.c.a.d)localc;
      locala.a((locald.a() + locald.c()) / 2.0D, (locald.b() + locald.f()) / 2.0D);
      locala.k(new Vector2D(locald.a(), locald.b(), locald.c(), locald.f()).getAngle());
      paramc.a(locala.s());
      paramc.b(locala.t());
      paramc.c(locala.x());
    }
    if (paramc.u())
    {
      locala.a(Double.POSITIVE_INFINITY);
      paramc.l(Double.POSITIVE_INFINITY);
    }
    else
    {
      locala.a(paramc.s());
    }
    return locala;
  }
  
  static com.a.c.c a(com.a.a.b.a parama, b paramb)
  {
    double d = paramb.c().b();
    return new a(paramb.b(parama.a()).longValue(), parama, parama.b(), parama.d(), a(parama.c(), parama.r(), parama.x(), d), parama.e());
  }
  
  static void a(com.a.a.b.a parama, com.a.c.c paramc, b paramb)
  {
    if (!NumberUtil.equals(paramb.a(paramc.a()), Long.valueOf(parama.a()))) {
      throw new IllegalArgumentException("Can't update body ID.");
    }
    if (!StringUtil.equals(paramc.b(), parama.b())) {
      throw new IllegalArgumentException("Can't update body name.");
    }
    if (!paramc.v().isEmpty()) {
      throw new IllegalArgumentException("Excluded (for colliding) body IDs are not supported.");
    }
    if (Math.abs(parama.d() - paramc.s()) > 0.0D) {
      throw new IllegalArgumentException("Can't update body mass.");
    }
    double d1 = paramb.c().b();
    if (!(paramc instanceof a))
    {
      if (!parama.r().equals(Double.valueOf(paramc.c()), Double.valueOf(paramc.d()))) {
        parama.a(paramc.c(), paramc.d());
      }
      if (!NumberUtil.equals(Double.valueOf(paramc.e()), Double.valueOf(parama.x()))) {
        parama.k(paramc.e());
      }
      if (!parama.u().equals(paramc.f())) {
        parama.a(paramc.f().copy());
      }
      if (!parama.v().equals(paramc.g())) {
        parama.b(paramc.g().copy());
      }
      if (!NumberUtil.equals(Double.valueOf(paramc.h()), Double.valueOf(parama.y()))) {
        parama.l(paramc.h());
      }
      if (!NumberUtil.equals(Double.valueOf(paramc.i()), Double.valueOf(parama.z()))) {
        parama.m(paramc.i());
      }
      if (!parama.w().equals(paramc.j())) {
        parama.c(paramc.j().copy());
      }
      if (!NumberUtil.equals(Double.valueOf(paramc.k()), Double.valueOf(parama.A()))) {
        parama.n(paramc.k());
      }
      if (!NumberUtil.equals(Double.valueOf(parama.i()), Double.valueOf(paramc.l()))) {
        parama.b(paramc.l());
      }
      if (!NumberUtil.equals(Double.valueOf(parama.j()), Double.valueOf(paramc.m()))) {
        parama.c(paramc.m());
      }
      Object localObject;
      double d2;
      Double localDouble;
      if ((parama.k() instanceof com.a.a.b.e.b))
      {
        localObject = (com.a.a.b.e.b)parama.k();
        d2 = ((com.a.a.b.e.b)localObject).a();
        localDouble = null;
      }
      else if ((parama.k() instanceof com.a.a.b.e.a))
      {
        localObject = (com.a.a.b.e.a)parama.k();
        d2 = ((com.a.a.b.e.a)localObject).a();
        localDouble = Double.valueOf(((com.a.a.b.e.a)localObject).b());
      }
      else
      {
        throw new IllegalArgumentException(String.format("Unsupported movement friction provider: %s.", new Object[] { parama.k() }));
      }
      if ((!NumberUtil.equals(Double.valueOf(d2), Double.valueOf(paramc.n()))) || (!NumberUtil.equals(localDouble, paramc.o()))) {
        if (paramc.o() == null) {
          parama.d(paramc.n());
        } else {
          parama.a(new com.a.a.b.e.a(paramc.n(), paramc.o().doubleValue()));
        }
      }
      if (!NumberUtil.equals(Double.valueOf(parama.l()), Double.valueOf(paramc.p()))) {
        parama.f(paramc.p());
      }
      if (!NumberUtil.equals(Double.valueOf(parama.m()), Double.valueOf(paramc.q()))) {
        parama.g(paramc.q());
      }
      if (!NumberUtil.equals(Double.valueOf(parama.n()), Double.valueOf(paramc.r()))) {
        parama.h(paramc.r());
      }
    }
    com.a.c.a.c localc = paramc.t();
    if (!localc.a(a(parama.c(), parama.r(), parama.x(), d1), d1))
    {
      parama.a(a(localc));
      if ((localc instanceof com.a.c.a.d))
      {
        com.a.c.a.d locald = (com.a.c.a.d)localc;
        parama.a((locald.a() + locald.c()) / 2.0D, (locald.b() + locald.f()) / 2.0D);
        parama.k(new Vector2D(locald.a(), locald.b(), locald.c(), locald.f()).getAngle());
        paramc.a(parama.s());
        paramc.b(parama.t());
        paramc.c(parama.x());
      }
    }
  }
  
  private static com.a.a.b.c.c a(com.a.c.a.c paramc)
  {
    if (paramc == null) {
      return null;
    }
    Object localObject;
    if ((paramc instanceof com.a.c.a.b))
    {
      localObject = (com.a.c.a.b)paramc;
      return new com.a.a.b.c.b(((com.a.c.a.b)localObject).a());
    }
    if ((paramc instanceof com.a.c.a.e))
    {
      localObject = (com.a.c.a.e)paramc;
      return new com.a.a.b.c.e(((com.a.c.a.e)localObject).a(), ((com.a.c.a.e)localObject).b());
    }
    if ((paramc instanceof com.a.c.a.d))
    {
      localObject = (com.a.c.a.d)paramc;
      return new com.a.a.b.c.d(Math.hypot(((com.a.c.a.d)localObject).c() - ((com.a.c.a.d)localObject).a(), ((com.a.c.a.d)localObject).f() - ((com.a.c.a.d)localObject).b()), paramc.e());
    }
    if ((paramc instanceof com.a.c.a.a))
    {
      localObject = (com.a.c.a.a)paramc;
      return new com.a.a.b.c.a(((com.a.c.a.a)localObject).a(), ((com.a.c.a.a)localObject).b(), ((com.a.c.a.a)localObject).c(), paramc.e());
    }
    throw new IllegalArgumentException("Unsupported form: " + paramc + '.');
  }
  
  private static com.a.c.a.c a(com.a.a.b.c.c paramc, Point2D paramPoint2D, double paramDouble1, double paramDouble2)
  {
    if (paramc == null) {
      return null;
    }
    Object localObject;
    switch (f.a[paramc.e().ordinal()])
    {
    case 1: 
      com.a.a.b.c.b localb = (com.a.a.b.c.b)paramc;
      return new com.a.c.a.b(localb.a());
    case 2: 
      com.a.a.b.c.e locale = (com.a.a.b.c.e)paramc;
      return new com.a.c.a.e(locale.a(), locale.b());
    case 3: 
      com.a.a.b.c.d locald = (com.a.a.b.c.d)paramc;
      localObject = new com.a.c.a.d(locald.a(paramPoint2D, paramDouble1, paramDouble2), locald.b(paramPoint2D, paramDouble1, paramDouble2));
      ((com.a.c.a.c)localObject).a(locald.f());
      return (com.a.c.a.c)localObject;
    case 4: 
      com.a.a.b.c.a locala = (com.a.a.b.c.a)paramc;
      localObject = new com.a.c.a.a(locala.a(), locala.b(), locala.c());
      ((com.a.c.a.c)localObject).a(locala.f());
      return (com.a.c.a.c)localObject;
    }
    throw new IllegalArgumentException("Unsupported form: " + paramc + '.');
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\c\b\a\e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */