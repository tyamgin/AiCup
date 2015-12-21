package com.a.a.b.b;

import com.a.a.b.a;
import com.a.a.b.c.b;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;

public class c
  extends e
{
  public c(double paramDouble)
  {
    super(paramDouble);
  }
  
  protected boolean a(a parama1, a parama2)
  {
    return (parama1.c().e() == com.a.a.b.c.f.a) && (parama2.c().e() == com.a.a.b.c.f.a);
  }
  
  protected f b(a parama1, a parama2)
  {
    b localb1 = (b)parama1.c();
    b localb2 = (b)parama2.c();
    double d1 = localb1.a();
    double d2 = localb2.a();
    double d3 = parama1.r().getDistanceTo(parama2.r());
    if (d3 > d1 + d2) {
      return null;
    }
    Vector2D localVector2D2;
    Vector2D localVector2D1;
    Point2D localPoint2D;
    if (d3 >= this.a)
    {
      localVector2D2 = new Vector2D(parama2.r(), parama1.r());
      localVector2D1 = localVector2D2.copy().normalize();
      localPoint2D = parama2.r().copy().add(localVector2D2.copy().multiply(d2 / (d1 + d2)));
    }
    else
    {
      localVector2D2 = parama2.u().copy().subtract(parama1.u());
      if (localVector2D2.getLength() >= this.a) {
        localVector2D1 = localVector2D2.normalize();
      } else if (parama2.u().getLength() >= this.a) {
        localVector2D1 = parama2.u().copy().normalize();
      } else {
        localVector2D1 = new Vector2D(1.0D, 0.0D);
      }
      localPoint2D = parama2.r().copy();
    }
    return new f(parama1, parama2, localPoint2D, localVector2D1, d1 + d2 - d3, this.a);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */