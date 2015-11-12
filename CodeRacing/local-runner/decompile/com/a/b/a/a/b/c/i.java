package com.a.b.a.a.b.c;

import com.a.b.a.a.b.d.e.a;
import com.a.b.a.a.b.d.e.b;
import com.a.b.a.a.c.r;
import com.a.b.e;
import com.a.b.f;
import com.a.b.g;
import com.a.b.h;
import com.a.c.c;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;

public class i
  extends g
{
  public void afterCollision(f paramf)
  {
    a locala = (a)paramf.b();
    locala.a(false);
    Object localObject;
    if (locala.m() == r.TIRE)
    {
      if (a(locala)) {
        paramf.a().b(locala);
      }
      if ((paramf.c() instanceof a))
      {
        localObject = (a)paramf.c();
        ((a)localObject).a(false);
        if (((a)localObject).m() == r.TIRE)
        {
          if (a((a)localObject)) {
            paramf.a().b((e)localObject);
          }
        }
        else if (((a)localObject).m() == r.WASHER) {
          paramf.a().b((e)localObject);
        }
      }
    }
    else if ((locala.m() == r.WASHER) && ((paramf.c() instanceof b)))
    {
      localObject = (b)paramf.c();
      ((b)localObject).a(false);
      paramf.a().b(locala);
      if (a((a)localObject)) {
        paramf.a().b((e)localObject);
      }
    }
  }
  
  private static boolean a(a parama)
  {
    return parama.b().f().getSquaredLength() <= Math.sqr(15.0D);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\c\i.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */