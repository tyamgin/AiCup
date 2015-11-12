package com.a.b.a.a.b.c;

import com.a.b.a.a.b.d.c.b;
import com.a.b.a.a.b.n;
import com.a.b.f;
import com.a.b.g;
import com.a.b.h;
import com.codeforces.commons.math.Math;

public class a
  extends g
{
  public void afterCollision(f paramf)
  {
    b localb = (b)paramf.b();
    com.a.b.a.a.b.d.a.a locala = (com.a.b.a.a.b.d.a.a)paramf.c();
    switch (b.a[locala.k().ordinal()])
    {
    case 1: 
      localb.a(Math.min(1.0D, localb.n() + 1.0D));
      break;
    case 2: 
      localb.d(localb.v() + 1);
      break;
    case 3: 
      localb.e(localb.w() + 1);
      break;
    case 4: 
      localb.f(localb.x() + 1);
      break;
    case 5: 
      localb.k().a(100);
      break;
    default: 
      throw new IllegalArgumentException("Unsupported bonus type: " + locala.k() + '.');
    }
    paramf.a().b(locala);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\c\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */