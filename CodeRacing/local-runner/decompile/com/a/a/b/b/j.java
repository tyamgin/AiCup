package com.a.a.b.b;

import com.a.a.b.c.c;
import com.a.a.b.c.d;
import com.codeforces.commons.geometry.Line2D;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class j
  extends e
{
  public j(double paramDouble)
  {
    super(paramDouble);
  }
  
  protected boolean a(com.a.a.b.a parama1, com.a.a.b.a parama2)
  {
    return (parama1.c().e() == com.a.a.b.c.f.c) && (parama2.c().e() == com.a.a.b.c.f.b);
  }
  
  protected f b(com.a.a.b.a parama1, com.a.a.b.a parama2)
  {
    d locald = (d)parama1.c();
    com.a.a.b.c.e locale = (com.a.a.b.c.e)parama2.c();
    Point2D localPoint2D1 = locald.a(parama1.r(), parama1.x(), this.a);
    Point2D localPoint2D2 = locald.b(parama1.r(), parama1.x(), this.a);
    Line2D localLine2D = Line2D.getLineByTwoPoints(localPoint2D1, localPoint2D2);
    if (localLine2D.getDistanceFrom(parama2.r()) > locale.d()) {
      return null;
    }
    Point2D[] arrayOfPoint2D = locale.a(parama2.r(), parama2.x(), this.a);
    int i = arrayOfPoint2D.length;
    Object localObject1 = null;
    ArrayList localArrayList = new ArrayList(i);
    int j = 0;
    Object localObject3;
    Object localObject5;
    double d4;
    double d5;
    Object localObject6;
    for (int k = 0; k < i; k++)
    {
      localObject3 = arrayOfPoint2D[k];
      Point2D localPoint2D3 = arrayOfPoint2D[(k + 1)];
      localObject4 = Line2D.getLineByTwoPoints((Point2D)localObject3, localPoint2D3);
      localObject5 = localLine2D.getIntersectionPoint((Line2D)localObject4, this.a);
      if (localObject5 != null)
      {
        d4 = Math.max(Math.min(localPoint2D1.getX(), localPoint2D2.getX()), Math.min(((Point2D)localObject3).getX(), localPoint2D3.getX()));
        d5 = Math.max(Math.min(localPoint2D1.getY(), localPoint2D2.getY()), Math.min(((Point2D)localObject3).getY(), localPoint2D3.getY()));
        d8 = Math.min(Math.max(localPoint2D1.getX(), localPoint2D2.getX()), Math.max(((Point2D)localObject3).getX(), localPoint2D3.getX()));
        d9 = Math.min(Math.max(localPoint2D1.getY(), localPoint2D2.getY()), Math.max(((Point2D)localObject3).getY(), localPoint2D3.getY()));
        if ((((Point2D)localObject5).getX() > d4 - this.a) && (((Point2D)localObject5).getX() < d8 + this.a) && (((Point2D)localObject5).getY() > d5 - this.a) && (((Point2D)localObject5).getY() < d9 + this.a))
        {
          localObject1 = localObject4;
          int n = 0;
          localObject6 = localArrayList.iterator();
          while (((Iterator)localObject6).hasNext())
          {
            Point2D localPoint2D5 = (Point2D)((Iterator)localObject6).next();
            if (localPoint2D5.nearlyEquals((Point2D)localObject5, this.a))
            {
              n = 1;
              break;
            }
          }
          if (n == 0) {
            localArrayList.add(localObject5);
          }
          j++;
        }
      }
    }
    if ((j == 1) && (locald.f()) && ((!com.a.a.b.f.a.a(localPoint2D1, arrayOfPoint2D, this.a)) || (!com.a.a.b.f.a.a(localPoint2D2, arrayOfPoint2D, this.a))))
    {
      localObject2 = new Vector2D(parama2.r(), ((Line2D)localObject1).getProjectionOf(parama2.r())).normalize();
      localObject3 = ((Line2D)localObject1).getParallelLine(localPoint2D1);
      double d2 = ((Line2D)localObject3).getDistanceFrom(parama2.r());
      localObject5 = ((Line2D)localObject1).getParallelLine(localPoint2D2);
      d4 = ((Line2D)localObject5).getDistanceFrom(parama2.r());
      d5 = (d2 < d4 ? localObject3 : (Line2D)localObject5).getDistanceFrom((Line2D)localObject1, this.a);
      return new f(parama1, parama2, (Point2D)localArrayList.get(0), (Vector2D)localObject2, d5, this.a);
    }
    Object localObject2 = arrayOfPoint2D[0];
    double d1 = localLine2D.getSignedDistanceFrom((Point2D)localObject2);
    Object localObject4 = localObject2;
    double d3 = d1;
    for (int m = 1; m < i; m++)
    {
      Point2D localPoint2D4 = arrayOfPoint2D[m];
      double d7 = localLine2D.getSignedDistanceFrom(localPoint2D4);
      if (d7 < d1)
      {
        d1 = d7;
        localObject2 = localPoint2D4;
      }
      if (d7 > d3)
      {
        d3 = d7;
        localObject4 = localPoint2D4;
      }
    }
    if (((d1 < 0.0D) && (d3 < 0.0D)) || ((d1 > 0.0D) && (d3 > 0.0D))) {
      return null;
    }
    if (localArrayList.isEmpty()) {
      return null;
    }
    Vector2D localVector2D;
    double d6;
    if (localLine2D.getSignedDistanceFrom(parama2.r()) > 0.0D)
    {
      localVector2D = localLine2D.getParallelLine((Point2D)localObject2).getUnitNormalFrom((Point2D)localObject4);
      d6 = Math.abs(d1);
    }
    else
    {
      localVector2D = localLine2D.getParallelLine((Point2D)localObject4).getUnitNormalFrom((Point2D)localObject2);
      d6 = d3;
    }
    double d8 = 0.0D;
    double d9 = 0.0D;
    Iterator localIterator = localArrayList.iterator();
    while (localIterator.hasNext())
    {
      localObject6 = (Point2D)localIterator.next();
      d8 += ((Point2D)localObject6).getX() / localArrayList.size();
      d9 += ((Point2D)localObject6).getY() / localArrayList.size();
    }
    return new f(parama1, parama2, new Point2D(d8, d9), localVector2D, d6, this.a);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b\j.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */