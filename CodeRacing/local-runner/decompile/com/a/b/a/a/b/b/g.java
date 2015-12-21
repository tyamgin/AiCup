package com.a.b.a.a.b.b;

import com.a.b.e;
import com.a.b.h;
import com.a.c.c;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;
import java.util.Iterator;
import java.util.List;

public class g
  implements com.a.b.b
{
  private final boolean a;
  
  public g(com.a.b.a.a.a.b paramb)
  {
    this.a = paramb.A();
  }
  
  public void a(h paramh, int paramInt)
  {
    Iterator localIterator = paramh.a().iterator();
    while (localIterator.hasNext())
    {
      e locale = (e)localIterator.next();
      double d1 = locale.b().c();
      double d2 = locale.b().d();
      if (locale.i() == null)
      {
        locale.c(Double.valueOf(0.0D));
        locale.a(new Vector2D(0.0D, 0.0D));
      }
      else
      {
        double d3 = Math.hypot(d1 - locale.g().doubleValue(), d2 - locale.h().doubleValue());
        locale.c(Double.valueOf(locale.i().doubleValue() + d3));
        locale.a(new Vector2D(locale.g().doubleValue(), locale.h().doubleValue(), d1, d2));
      }
      locale.a(Double.valueOf(d1));
      locale.b(Double.valueOf(d2));
      if (this.a)
      {
        locale.a(paramInt);
        locale.b(paramInt);
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\b\g.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */