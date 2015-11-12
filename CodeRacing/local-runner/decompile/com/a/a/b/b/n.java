package com.a.a.b.b;

import com.a.a.b.a;
import com.a.a.b.c.c;
import com.codeforces.commons.geometry.Line2D;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;

public class n
  extends e
{
  public n(double paramDouble)
  {
    super(paramDouble);
  }
  
  protected boolean a(a parama1, a parama2)
  {
    return (parama1.c().e() == com.a.a.b.c.f.b) && (parama2.c().e() == com.a.a.b.c.f.b);
  }
  
  protected f b(a parama1, a parama2)
  {
    com.a.a.b.c.e locale1 = (com.a.a.b.c.e)parama1.c();
    com.a.a.b.c.e locale2 = (com.a.a.b.c.e)parama2.c();
    Point2D[] arrayOfPoint2D1 = locale1.a(parama1.r(), parama1.x(), this.a);
    Point2D[] arrayOfPoint2D2 = locale2.a(parama2.r(), parama2.x(), this.a);
    f localf1 = a(parama1, parama2, arrayOfPoint2D1, arrayOfPoint2D2);
    if (localf1 == null) {
      return null;
    }
    f localf2 = a(parama2, parama1, arrayOfPoint2D2, arrayOfPoint2D1);
    if (localf2 == null) {
      return null;
    }
    if (localf2.e() < localf1.e()) {
      return new f(parama1, parama2, localf2.c(), localf2.d().negate(), localf2.e(), this.a);
    }
    return localf1;
  }
  
  private f a(a parama1, a parama2, Point2D[] paramArrayOfPoint2D1, Point2D[] paramArrayOfPoint2D2)
  {
    int i = paramArrayOfPoint2D1.length;
    int j = paramArrayOfPoint2D2.length;
    double d1 = Double.POSITIVE_INFINITY;
    Object localObject1 = null;
    Object localObject2 = null;
    for (int k = 0; k < i; k++)
    {
      Point2D localPoint2D1 = paramArrayOfPoint2D1[k];
      Point2D localPoint2D2 = paramArrayOfPoint2D1[(k + 1)];
      Line2D localLine2D = Line2D.getLineByTwoPoints(localPoint2D1, localPoint2D2);
      if (localLine2D.getSignedDistanceFrom(parama1.r()) > -this.a) {
        throw new IllegalStateException(String.format("%s of %s is too small, does not represent a convex polygon, or its points are going in wrong order.", new Object[] { c.a(parama1.c()), parama1 }));
      }
      double d2 = Double.POSITIVE_INFINITY;
      Object localObject3 = null;
      Vector2D localVector2D = null;
      for (int m = 0; m < j; m++)
      {
        Point2D localPoint2D3 = paramArrayOfPoint2D2[m];
        double d4 = localLine2D.getSignedDistanceFrom(localPoint2D3);
        if (d4 < d2)
        {
          d2 = d4;
          localObject3 = localPoint2D3;
          localVector2D = localLine2D.getUnitNormalFrom(parama1.r(), this.a).negate();
        }
      }
      if (d2 > 0.0D) {
        return null;
      }
      double d3 = -d2;
      if (d3 < d1)
      {
        d1 = d3;
        localObject1 = localObject3;
        localObject2 = localVector2D;
      }
    }
    if ((localObject1 == null) || (localObject2 == null)) {
      return null;
    }
    return new f(parama1, parama2, (Point2D)localObject1, (Vector2D)localObject2, d1, this.a);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b\n.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */