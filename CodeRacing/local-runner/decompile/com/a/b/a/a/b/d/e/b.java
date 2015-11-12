package com.a.b.a.a.b.d.e;

import com.a.b.a.a.b.n;
import com.a.b.a.a.c.r;
import com.a.b.f;
import com.a.c.c;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;

public class b
  extends a
{
  public b(com.a.b.a.a.b.d.c.b paramb, n paramn, int paramInt, Point2D paramPoint2D, double paramDouble)
  {
    super(new com.a.c.a.b(70.0D), paramb, paramn, r.TIRE, paramInt, paramPoint2D, new Vector2D(60.0D, 0.0D).rotate(paramDouble), paramDouble, 1000.0D);
  }
  
  public double a(f paramf, int paramInt)
  {
    Vector2D localVector2D1 = ((a)paramf.b()).b().f();
    Vector2D localVector2D2 = ((com.a.b.a.a.b.d.c.b)paramf.c()).b().f();
    double d1 = paramf.e().copyNegate().dotProduct(localVector2D1);
    double d2 = paramf.e().dotProduct(localVector2D2);
    return Math.max(d1 + d2, 0.0D) / 60.0D * 0.25D;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\d\e\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */