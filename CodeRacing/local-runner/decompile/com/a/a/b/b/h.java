package com.a.a.b.b;

import com.a.a.b.a;
import com.a.a.b.c.b;
import com.a.a.b.c.c;
import com.a.a.b.c.d;
import com.codeforces.commons.geometry.Line2D;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;

public class h
  extends e
{
  public h(double paramDouble)
  {
    super(paramDouble);
  }
  
  protected boolean a(a parama1, a parama2)
  {
    return (parama1.c().e() == com.a.a.b.c.f.c) && (parama2.c().e() == com.a.a.b.c.f.a);
  }
  
  protected f b(a parama1, a parama2)
  {
    d locald = (d)parama1.c();
    b localb = (b)parama2.c();
    Point2D localPoint2D1 = locald.a(parama1.r(), parama1.x(), this.a);
    Point2D localPoint2D2 = locald.b(parama1.r(), parama1.x(), this.a);
    return a(parama1, parama2, localPoint2D1, localPoint2D2, localb, this.a);
  }
  
  static f a(a parama1, a parama2, Point2D paramPoint2D1, Point2D paramPoint2D2, b paramb, double paramDouble)
  {
    Line2D localLine2D = Line2D.getLineByTwoPoints(paramPoint2D1, paramPoint2D2);
    double d1 = localLine2D.getDistanceFrom(parama2.r());
    double d2 = paramb.a();
    if (d1 > d2) {
      return null;
    }
    double d3 = Math.min(paramPoint2D1.getX(), paramPoint2D2.getX());
    double d4 = Math.min(paramPoint2D1.getY(), paramPoint2D2.getY());
    double d5 = Math.max(paramPoint2D1.getX(), paramPoint2D2.getX());
    double d6 = Math.max(paramPoint2D1.getY(), paramPoint2D2.getY());
    Point2D localPoint2D1 = localLine2D.getProjectionOf(parama2.r());
    int i = (localPoint2D1.getX() > d3 - paramDouble) && (localPoint2D1.getX() < d5 + paramDouble) && (localPoint2D1.getY() > d4 - paramDouble) && (localPoint2D1.getY() < d6 + paramDouble) ? 1 : 0;
    if (i != 0)
    {
      Object localObject;
      if (d1 >= paramDouble)
      {
        localObject = new Vector2D(parama2.r(), localPoint2D1).normalize();
      }
      else
      {
        Vector2D localVector2D1 = localLine2D.getUnitNormal();
        Vector2D localVector2D2 = parama2.u().copy().subtract(parama1.u());
        if (localVector2D2.getLength() >= paramDouble) {
          localObject = localVector2D2.dotProduct(localVector2D1) >= paramDouble ? localVector2D1 : localVector2D1.negate();
        } else if (parama2.u().getLength() >= paramDouble) {
          localObject = parama2.u().dotProduct(localVector2D1) >= paramDouble ? localVector2D1 : localVector2D1.negate();
        } else {
          localObject = localVector2D1;
        }
      }
      return new f(parama1, parama2, localPoint2D1, (Vector2D)localObject, d2 - d1, paramDouble);
    }
    double d7 = parama2.a(paramPoint2D1);
    double d8 = parama2.a(paramPoint2D2);
    Point2D localPoint2D2;
    double d9;
    if (d7 < d8)
    {
      localPoint2D2 = paramPoint2D1;
      d9 = d7;
    }
    else
    {
      localPoint2D2 = paramPoint2D2;
      d9 = d8;
    }
    if (d9 > d2) {
      return null;
    }
    return new f(parama1, parama2, localPoint2D2, new Vector2D(parama2.r(), localPoint2D2).normalize(), d2 - d9, paramDouble);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b\h.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */