package com.a.a.b.b;

import com.a.a.b.c.c;
import com.codeforces.commons.geometry.Line2D;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;

public class b
  extends e
{
  public b(double paramDouble)
  {
    super(paramDouble);
  }
  
  protected boolean a(com.a.a.b.a parama1, com.a.a.b.a parama2)
  {
    return (parama1.c().e() == com.a.a.b.c.f.d) && (parama2.c().e() == com.a.a.b.c.f.a);
  }
  
  protected f b(com.a.a.b.a parama1, com.a.a.b.a parama2)
  {
    com.a.a.b.c.a locala = (com.a.a.b.c.a)parama1.c();
    com.a.a.b.c.b localb = (com.a.a.b.c.b)parama2.c();
    double d1 = locala.a();
    double d2 = localb.a();
    double d3 = parama1.r().getDistanceTo(parama2.r());
    if (d3 > d1 + d2) {
      return null;
    }
    if (d3 < Math.abs(d1 - d2)) {
      return null;
    }
    parama1.C();
    parama2.C();
    double d4 = parama1.x() + locala.b();
    double d5 = d4 + locala.c();
    f localf = a(parama1, parama2, d1, d2, d3, d4, d5);
    if (localf != null) {
      return localf;
    }
    if (d3 >= this.a)
    {
      double d6 = Math.sqrt((d3 + d1 + d2) * (d3 + d1 - d2) * (d3 - d1 + d2) * (-d3 + d1 + d2)) / 4.0D;
      double d7 = parama1.r().getSquaredDistanceTo(parama2.r());
      double d8 = Math.sqr(d1) - Math.sqr(d2);
      double d9 = (parama1.s() + parama2.s()) / 2.0D + (parama2.s() - parama1.s()) * d8 / (2.0D * d7);
      double d10 = (parama1.t() + parama2.t()) / 2.0D + (parama2.t() - parama1.t()) * d8 / (2.0D * d7);
      double d11 = 2.0D * (parama1.t() - parama2.t()) * d6 / d7;
      double d12 = 2.0D * (parama1.s() - parama2.s()) * d6 / d7;
      Point2D localPoint2D1 = new Point2D(d9, d10);
      if ((Math.abs(d11) < this.a) && (Math.abs(d12) < this.a))
      {
        double d13 = new Vector2D(parama1.r(), localPoint2D1).getAngle();
        if (d13 < d4) {
          d13 += 6.283185307179586D;
        }
        if ((d13 >= d4) && (d13 <= d5)) {
          return new f(parama1, parama2, localPoint2D1, new Vector2D(parama2.r(), localPoint2D1).normalize(), d2 - parama2.a(localPoint2D1), this.a);
        }
      }
      else
      {
        Point2D localPoint2D2 = localPoint2D1.copy().add(d11, -d12);
        Point2D localPoint2D3 = localPoint2D1.copy().add(-d11, d12);
        double d14 = new Vector2D(parama1.r(), localPoint2D2).getAngle();
        if (d14 < d4) {
          d14 += 6.283185307179586D;
        }
        double d15 = new Vector2D(parama1.r(), localPoint2D3).getAngle();
        if (d15 < d4) {
          d15 += 6.283185307179586D;
        }
        if ((d14 >= d4) && (d14 <= d5) && (d15 >= d4) && (d15 <= d5))
        {
          if (d3 > d1 - this.a) {
            return new f(parama1, parama2, localPoint2D1, new Vector2D(parama2.r(), parama1.r()).normalize(), d1 + d2 - d3, this.a);
          }
          return new f(parama1, parama2, localPoint2D1, new Vector2D(parama1.r(), parama2.r()).normalize(), d3 + d2 - d1, this.a);
        }
      }
      return null;
    }
    return a(parama2, parama1, locala, d1, d4, d5, d2);
  }
  
  private f a(com.a.a.b.a parama1, com.a.a.b.a parama2, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, double paramDouble5)
  {
    Point2D localPoint2D1 = parama1.r().copy().add(new Vector2D(paramDouble1, 0.0D).setAngle(paramDouble4));
    Point2D localPoint2D2 = parama1.r().copy().add(new Vector2D(paramDouble1, 0.0D).setAngle(paramDouble5));
    double d1 = parama2.a(localPoint2D1);
    double d2 = parama2.a(localPoint2D2);
    if ((d1 <= paramDouble2) && (d2 <= paramDouble2))
    {
      Point2D localPoint2D3 = new Point2D((localPoint2D1.getX() + localPoint2D2.getX()) / 2.0D, (localPoint2D1.getY() + localPoint2D2.getY()) / 2.0D);
      Vector2D localVector2D;
      Line2D localLine2D;
      if (parama2.a(localPoint2D3) >= this.a)
      {
        localVector2D = new Vector2D(parama2.r(), localPoint2D3).normalize();
        localLine2D = Line2D.getLineByTwoPoints(parama2.r(), localPoint2D3);
      }
      else
      {
        localVector2D = new Vector2D(parama2.r(), parama1.r()).normalize();
        localLine2D = Line2D.getLineByTwoPoints(parama2.r(), parama1.r());
      }
      Point2D localPoint2D4 = localLine2D.getProjectionOf(localPoint2D1, this.a);
      double d3 = localLine2D.getDistanceFrom(localPoint2D1);
      double d4 = Math.sqrt(Math.sqr(paramDouble2) - Math.sqr(d3)) - parama2.a(localPoint2D4);
      Point2D localPoint2D5 = localLine2D.getProjectionOf(localPoint2D2, this.a);
      double d5 = localLine2D.getDistanceFrom(localPoint2D2);
      double d6 = Math.sqrt(Math.sqr(paramDouble2) - Math.sqr(d5)) - parama2.a(localPoint2D5);
      return new f(parama1, parama2, localPoint2D3, localVector2D, Math.max(d4, d6), this.a);
    }
    if (d1 <= paramDouble2)
    {
      if (d1 >= this.a) {
        return new f(parama1, parama2, localPoint2D1, new Vector2D(parama2.r(), localPoint2D1).normalize(), paramDouble2 - d1, this.a);
      }
      return new f(parama1, parama2, localPoint2D1, new Vector2D(parama2.r(), parama1.r()).normalize(), paramDouble1 + paramDouble2 - paramDouble3, this.a);
    }
    if (d2 <= paramDouble2)
    {
      if (d2 >= this.a) {
        return new f(parama1, parama2, localPoint2D2, new Vector2D(parama2.r(), localPoint2D2).normalize(), paramDouble2 - d2, this.a);
      }
      return new f(parama1, parama2, localPoint2D2, new Vector2D(parama2.r(), parama1.r()).normalize(), paramDouble1 + paramDouble2 - paramDouble3, this.a);
    }
    return null;
  }
  
  private f a(com.a.a.b.a parama1, com.a.a.b.a parama2, com.a.a.b.c.a parama, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
  {
    if (paramDouble4 >= paramDouble1)
    {
      Vector2D localVector2D1 = parama1.u().copy().subtract(parama2.u());
      Vector2D localVector2D2;
      if ((localVector2D1.getLength() >= this.a) && (com.a.a.b.f.a.a(localVector2D1.getAngle(), paramDouble2, paramDouble3))) {
        localVector2D2 = localVector2D1.normalize();
      } else if ((parama1.u().getLength() >= this.a) && (com.a.a.b.f.a.a(parama1.u().getAngle(), paramDouble2, paramDouble3))) {
        localVector2D2 = parama1.u().copy().normalize();
      } else {
        localVector2D2 = new Vector2D(1.0D, 0.0D).setAngle(parama2.x() + parama.b() + parama.c() / 2.0D);
      }
      return new f(parama2, parama1, parama1.r().copy(), localVector2D2, paramDouble4 - paramDouble1, this.a);
    }
    return null;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */