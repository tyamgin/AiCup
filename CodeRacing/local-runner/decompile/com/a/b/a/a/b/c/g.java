package com.a.b.a.a.b.c;

import com.a.b.a.a.c.k;
import com.a.b.f;
import com.a.b.h;
import com.a.c.c;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.holder.Readable;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;

public class g
  extends com.a.b.g
{
  private final Readable a;
  
  public g(Readable paramReadable)
  {
    this.a = paramReadable;
  }
  
  public boolean a(h paramh, com.a.b.a.a.b.d.e.a parama, com.a.b.a.a.b.d.c.b paramb)
  {
    return (!parama.o()) || (!parama.k().equals(paramb));
  }
  
  public boolean beforeResolvingCollision(f paramf)
  {
    com.a.b.a.a.b.d.e.a locala = (com.a.b.a.a.b.d.e.a)paramf.b();
    com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)paramf.c();
    com.a.b.a.a.b.e.b.a(localb, locala.l(), locala.a(paramf, ((Integer)this.a.get()).intValue()));
    return true;
  }
  
  public void afterCollision(f paramf)
  {
    com.a.b.a.a.b.d.e.a locala = (com.a.b.a.a.b.d.e.a)paramf.b();
    switch (h.a[locala.m().ordinal()])
    {
    case 1: 
      paramf.a().a(new com.a.b.a.a.b.a.a(k.CAR_AND_WASHER_IMPACT, ((Integer)this.a.get()).intValue(), paramf.d().getX(), paramf.d().getY(), paramf.e().copyNegate().getAngle()));
      paramf.a().b(locala);
      break;
    case 2: 
      paramf.a().a(new com.a.b.a.a.b.a.a(k.CAR_AND_TIRE_IMPACT, ((Integer)this.a.get()).intValue(), paramf.d().getX(), paramf.d().getY(), paramf.e().copyNegate().getAngle()));
      if ((locala.b().f().getSquaredLength() <= Math.sqr(15.0D)) || (NumberUtil.equals((Integer)this.a.get(), Integer.valueOf(locala.n())))) {
        paramf.a().b(locala);
      }
      break;
    default: 
      throw new IllegalArgumentException("Unsupported projectile type: " + locala.m() + '.');
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\c\g.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */