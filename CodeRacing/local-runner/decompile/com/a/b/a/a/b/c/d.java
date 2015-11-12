package com.a.b.a.a.b.c;

import com.a.b.a.a.b.a.a;
import com.a.b.a.a.c.k;
import com.a.b.f;
import com.a.b.g;
import com.a.b.h;
import com.a.c.c;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.holder.Readable;

public class d
  extends g
{
  private final Readable a;
  
  public d(Readable paramReadable)
  {
    this.a = paramReadable;
  }
  
  public boolean beforeResolvingCollision(f paramf)
  {
    com.a.b.a.a.b.d.c.b localb1 = (com.a.b.a.a.b.d.c.b)paramf.b();
    com.a.b.a.a.b.d.c.b localb2 = (com.a.b.a.a.b.d.c.b)paramf.c();
    c localc1 = localb1.b();
    c localc2 = localb2.b();
    Vector2D localVector2D1 = localc1.f();
    Vector2D localVector2D2 = localc2.f();
    double d1 = paramf.e().copyNegate().dotProduct(localVector2D1);
    double d2 = paramf.e().dotProduct(localVector2D2);
    double d3 = d1 + d2;
    if (d3 > 0.0D)
    {
      double d4 = d3 * localc2.s() / localc1.s() * 0.003D;
      double d5 = d3 * localc1.s() / localc2.s() * 0.003D;
      if ((d4 > 0.01D) || (d5 > 0.01D))
      {
        if (d4 > 0.01D) {
          com.a.b.a.a.b.e.b.a(localb1, localb2.k(), d4, 2.0D);
        }
        if (d5 > 0.01D) {
          com.a.b.a.a.b.e.b.a(localb2, localb1.k(), d5, 2.0D);
        }
        paramf.a().a(new a(k.CAR_AND_CAR_IMPACT, ((Integer)this.a.get()).intValue(), paramf.d().getX(), paramf.d().getY(), 0.0D));
      }
    }
    return true;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\c\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */