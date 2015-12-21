package com.a.a.b.b;

import com.a.a.b.c.b;
import com.a.a.b.c.c;
import com.codeforces.commons.geometry.Line2D;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;

public class m
  extends e
{
  public m(double paramDouble)
  {
    super(paramDouble);
  }
  
  protected boolean a(com.a.a.b.a parama1, com.a.a.b.a parama2)
  {
    return (parama1.c().e() == com.a.a.b.c.f.b) && (parama2.c().e() == com.a.a.b.c.f.a);
  }
  
  protected f b(com.a.a.b.a parama1, com.a.a.b.a parama2)
  {
    com.a.a.b.c.e locale = (com.a.a.b.c.e)parama1.c();
    b localb = (b)parama2.c();
    Point2D[] arrayOfPoint2D = locale.a(parama1.r(), parama1.x(), this.a);
    int i = arrayOfPoint2D.length;
    Object localObject2;
    Object localObject3;
    if (!com.a.a.b.f.a.a(parama2.r(), arrayOfPoint2D, this.a))
    {
      double d1 = Double.POSITIVE_INFINITY;
      localObject2 = null;
      for (int k = 0; k < i; k++)
      {
        localObject3 = arrayOfPoint2D[k];
        Point2D localPoint2D2 = arrayOfPoint2D[(k + 1)];
        Line2D localLine2D = Line2D.getLineByTwoPoints((Point2D)localObject3, localPoint2D2);
        double d2 = localLine2D.getDistanceFrom(parama2.r());
        if (d2 < d1)
        {
          d1 = d2;
          localObject2 = localLine2D;
        }
      }
      if (localObject2 != null) {
        return new f(parama1, parama2, parama2.r(), ((Line2D)localObject2).getUnitNormal().negate(), localb.a() - ((Line2D)localObject2).getSignedDistanceFrom(parama2.r()), this.a);
      }
    }
    Object localObject1 = null;
    for (int j = 0; j < i; j++)
    {
      localObject2 = arrayOfPoint2D[j];
      Point2D localPoint2D1 = arrayOfPoint2D[(j + 1)];
      localObject3 = h.a(parama1, parama2, (Point2D)localObject2, localPoint2D1, localb, this.a);
      if ((localObject3 != null) && ((localObject1 == null) || (((f)localObject3).e() > ((f)localObject1).e()))) {
        localObject1 = localObject3;
      }
    }
    return (f)localObject1;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b\m.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */