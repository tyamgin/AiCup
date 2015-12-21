package com.a.b.a.a.b.c;

import com.a.b.a.a.b.a.a;
import com.a.b.a.a.c.k;
import com.a.b.f;
import com.a.b.g;
import com.a.b.h;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.holder.Readable;

public class c
  extends g
{
  private final Readable a;
  
  public c(Readable paramReadable)
  {
    this.a = paramReadable;
  }
  
  public boolean beforeResolvingCollision(f paramf)
  {
    com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)paramf.b();
    double d1 = paramf.e().copyNegate().dotProduct(localb.b().f());
    if (d1 > 0.0D)
    {
      double d2 = d1 * 0.003D;
      if (d2 > 0.01D)
      {
        com.a.b.a.a.b.e.b.a(localb, null, d2, 2.0D);
        paramf.a().a(new a(k.CAR_AND_BORDER_IMPACT, ((Integer)this.a.get()).intValue(), paramf.d().getX(), paramf.d().getY(), 0.0D));
      }
    }
    return true;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\c\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */