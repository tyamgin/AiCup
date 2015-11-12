package com.a.b.a.a.b.c;

import com.a.b.a.a.b.d.c.b;
import com.a.b.a.a.b.d.d.a;
import com.a.b.a.a.b.e.h;
import com.a.b.f;
import com.a.b.g;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;

public class e
  extends g
{
  public boolean beforeResolvingCollision(f paramf)
  {
    b localb = (b)paramf.b();
    a locala = (a)paramf.c();
    if ((localb.N()) || (localb.G() > 0) || (locala.k() <= 0) || (localb.a(locala) > h.a(locala.b().t()))) {
      return false;
    }
    com.a.c.c localc = localb.b();
    int i = Math.min(locala.k(), 60);
    localb.k(i);
    locala.c(locala.k() - i);
    localc.a(Double.valueOf(0.001D));
    localc.i(0.0017453292519943296D);
    localc.d(localc.h() + (com.a.a.a.a.c.c() ? 0.0023271056693257726D : -0.0023271056693257726D) * localc.f().getLength());
    return false;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\c\e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */