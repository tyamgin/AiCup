package com.a.a.b.b;

import com.a.a.b.c.c;
import com.codeforces.commons.geometry.Line2D;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.holder.Mutable;
import com.codeforces.commons.holder.SimpleMutable;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.pair.SimplePair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class k
  extends e
{
  public k(double paramDouble)
  {
    super(paramDouble);
  }
  
  protected boolean a(com.a.a.b.a parama1, com.a.a.b.a parama2)
  {
    return (parama1.c().e() == com.a.a.b.c.f.b) && (parama2.c().e() == com.a.a.b.c.f.d);
  }
  
  protected f b(com.a.a.b.a parama1, com.a.a.b.a parama2)
  {
    com.a.a.b.c.e locale = (com.a.a.b.c.e)parama1.c();
    com.a.a.b.c.a locala = (com.a.a.b.c.a)parama2.c();
    double d1 = locale.d();
    double d2 = locala.a();
    double d3 = parama1.r().getDistanceTo(parama2.r());
    if (d3 > d1 + d2) {
      return null;
    }
    if (d3 < Math.abs(d1 - d2)) {
      return null;
    }
    Point2D[] arrayOfPoint2D = locale.a(parama1.r(), parama1.x(), this.a);
    int i = arrayOfPoint2D.length;
    double d4 = d2 * d2;
    double d5 = parama2.x() + locala.b();
    double d6 = d5 + locala.c();
    Point2D localPoint2D1 = parama2.r().copy().add(new Vector2D(d2, 0.0D).setAngle(d5));
    Point2D localPoint2D2 = parama2.r().copy().add(new Vector2D(d2, 0.0D).setAngle(d6));
    ArrayList localArrayList = new ArrayList();
    double d7;
    double d8;
    for (int j = 0; j < i; j++)
    {
      localObject1 = arrayOfPoint2D[j];
      Point2D localPoint2D3 = arrayOfPoint2D[(j + 1)];
      localObject2 = Line2D.getLineByTwoPoints((Point2D)localObject1, localPoint2D3);
      if (((Line2D)localObject2).getSignedDistanceFrom(parama1.r()) > -this.a) {
        throw new IllegalStateException(String.format("%s of %s is too small, does not represent a convex polygon, or its points are going in wrong order.", new Object[] { c.a(parama1.c()), parama1 }));
      }
      d7 = ((Line2D)localObject2).getSignedDistanceFrom(parama2.r());
      if (d7 <= d2)
      {
        d8 = Math.min(((Point2D)localObject1).getX(), localPoint2D3.getX());
        double d9 = Math.min(((Point2D)localObject1).getY(), localPoint2D3.getY());
        double d10 = Math.max(((Point2D)localObject1).getX(), localPoint2D3.getX());
        double d11 = Math.max(((Point2D)localObject1).getY(), localPoint2D3.getY());
        Point2D localPoint2D6 = ((Line2D)localObject2).getProjectionOf(parama2.r());
        double d12 = Math.sqrt(d4 - d7 * d7);
        Vector2D localVector2D2 = new Vector2D((Point2D)localObject1, localPoint2D3).copy().setLength(d12);
        Point2D localPoint2D7 = localPoint2D6.copy().add(localVector2D2);
        if (a(localPoint2D7, d8, d9, d10, d11, parama2, d5, d6)) {
          a(localPoint2D7, (Point2D)localObject1, localPoint2D3, (Line2D)localObject2, localArrayList);
        }
        Point2D localPoint2D8 = localPoint2D6.copy().add(localVector2D2.copy().negate());
        if (a(localPoint2D8, d8, d9, d10, d11, parama2, d5, d6)) {
          a(localPoint2D8, (Point2D)localObject1, localPoint2D3, (Line2D)localObject2, localArrayList);
        }
      }
    }
    j = localArrayList.size();
    if (j == 0) {
      return null;
    }
    if ((j == 1) && (locala.f()) && ((!com.a.a.b.f.a.a(localPoint2D1, arrayOfPoint2D, this.a)) || (!com.a.a.b.f.a.a(localPoint2D2, arrayOfPoint2D, this.a))))
    {
      localObject1 = (a)localArrayList.get(0);
      int k = ((a)localObject1).b.size();
      if ((k == 1) || (k == 2))
      {
        localObject2 = (Line2D)((a)localObject1).b.get(0);
        d7 = ((Line2D)localObject2).getSignedDistanceFrom(localPoint2D1);
        d8 = ((Line2D)localObject2).getSignedDistanceFrom(localPoint2D2);
        for (int m = 0; m < i; m++)
        {
          Point2D localPoint2D4 = arrayOfPoint2D[m];
          Point2D localPoint2D5 = arrayOfPoint2D[(m + 1)];
          Line2D localLine2D = Line2D.getLineByTwoPoints(localPoint2D4, localPoint2D5);
          if (localLine2D.getSignedDistanceFrom(localPoint2D1) >= this.a) {
            return new f(parama1, parama2, localPoint2D2, ((Line2D)localObject2).getUnitNormal().negate(), -d8, this.a);
          }
          if (localLine2D.getSignedDistanceFrom(localPoint2D2) >= this.a) {
            return new f(parama1, parama2, localPoint2D1, ((Line2D)localObject2).getUnitNormal().negate(), -d7, this.a);
          }
        }
        if (d7 < d8) {
          return new f(parama1, parama2, localPoint2D1, ((Line2D)localObject2).getUnitNormal().negate(), -d7, this.a);
        }
        return new f(parama1, parama2, localPoint2D2, ((Line2D)localObject2).getUnitNormal().negate(), -d8, this.a);
      }
      throw new IllegalStateException(String.format("%s of %s is too small, does not represent a convex polygon, or its points are going in wrong order.", new Object[] { c.a(parama1.c()), parama1 }));
    }
    Object localObject1 = new Vector2D(((a)localArrayList.get(0)).a, parama2.r());
    Vector2D localVector2D1 = new Vector2D(((a)localArrayList.get(0)).a, parama1.r());
    a locala1;
    Iterator localIterator2;
    SimplePair localSimplePair;
    if ((d3 > d2 - this.a) && (((Vector2D)localObject1).dotProduct(localVector2D1) < 0.0D))
    {
      localObject2 = new SimpleMutable();
      localSimpleMutable = new SimpleMutable();
      localIterator1 = localArrayList.iterator();
      while (localIterator1.hasNext())
      {
        locala1 = (a)localIterator1.next();
        a(parama2, locala1.a, (Mutable)localObject2, localSimpleMutable);
        localIterator2 = locala1.c.iterator();
        while (localIterator2.hasNext())
        {
          localSimplePair = (SimplePair)localIterator2.next();
          a(parama2, (Point2D)localSimplePair.getFirst(), (Mutable)localObject2, localSimpleMutable);
          a(parama2, (Point2D)localSimplePair.getSecond(), (Mutable)localObject2, localSimpleMutable);
        }
      }
      return ((Mutable)localObject2).get() == null ? null : new f(parama1, parama2, (Point2D)((Mutable)localObject2).get(), new Vector2D(parama2.r(), (Point2D)((Mutable)localObject2).get()).normalize(), d2 - ((Double)localSimpleMutable.get()).doubleValue(), this.a);
    }
    Object localObject2 = new SimpleMutable();
    SimpleMutable localSimpleMutable = new SimpleMutable();
    Iterator localIterator1 = localArrayList.iterator();
    while (localIterator1.hasNext())
    {
      locala1 = (a)localIterator1.next();
      a(parama2, locala1.a, (Mutable)localObject2, localSimpleMutable, d5, d6);
      localIterator2 = locala1.c.iterator();
      while (localIterator2.hasNext())
      {
        localSimplePair = (SimplePair)localIterator2.next();
        a(parama2, (Point2D)localSimplePair.getFirst(), (Mutable)localObject2, localSimpleMutable, d5, d6);
        a(parama2, (Point2D)localSimplePair.getSecond(), (Mutable)localObject2, localSimpleMutable, d5, d6);
      }
    }
    return ((Mutable)localObject2).get() == null ? null : new f(parama1, parama2, (Point2D)((Mutable)localObject2).get(), new Vector2D((Point2D)((Mutable)localObject2).get(), parama2.r()).normalize(), ((Double)localSimpleMutable.get()).doubleValue() - d2, this.a);
  }
  
  private void a(com.a.a.b.a parama, Point2D paramPoint2D, Mutable paramMutable1, Mutable paramMutable2)
  {
    double d = parama.a(paramPoint2D);
    if ((d >= this.a) && ((paramMutable1.get() == null) || (d < ((Double)paramMutable2.get()).doubleValue())))
    {
      paramMutable1.set(paramPoint2D);
      paramMutable2.set(Double.valueOf(d));
    }
  }
  
  private static void a(com.a.a.b.a parama, Point2D paramPoint2D, Mutable paramMutable1, Mutable paramMutable2, double paramDouble1, double paramDouble2)
  {
    double d = parama.a(paramPoint2D);
    if ((com.a.a.b.f.a.a(new Vector2D(parama.r(), paramPoint2D).getAngle(), paramDouble1, paramDouble2)) && ((paramMutable1.get() == null) || (d > ((Double)paramMutable2.get()).doubleValue())))
    {
      paramMutable1.set(paramPoint2D);
      paramMutable2.set(Double.valueOf(d));
    }
  }
  
  private boolean a(Point2D paramPoint2D, double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, com.a.a.b.a parama, double paramDouble5, double paramDouble6)
  {
    int i = (paramPoint2D.getX() > paramDouble1 - this.a) && (paramPoint2D.getX() < paramDouble3 + this.a) && (paramPoint2D.getY() > paramDouble2 - this.a) && (paramPoint2D.getY() < paramDouble4 + this.a) ? 1 : 0;
    double d = new Vector2D(parama.r(), paramPoint2D).getAngle();
    if (d < paramDouble5) {
      d += 6.283185307179586D;
    }
    int j = (d >= paramDouble5) && (d <= paramDouble6) ? 1 : 0;
    return (i != 0) && (j != 0);
  }
  
  private void a(Point2D paramPoint2D1, Point2D paramPoint2D2, Point2D paramPoint2D3, Line2D paramLine2D, List paramList)
  {
    int i = 0;
    Object localObject = paramList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      a locala = (a)((Iterator)localObject).next();
      if (locala.a.nearlyEquals(paramPoint2D1, this.a))
      {
        locala.b.add(paramLine2D);
        locala.c.add(new SimplePair(paramPoint2D2, paramPoint2D3));
        i = 1;
        break;
      }
    }
    if (i == 0)
    {
      localObject = new a(paramPoint2D1, null);
      ((a)localObject).b.add(paramLine2D);
      ((a)localObject).c.add(new SimplePair(paramPoint2D2, paramPoint2D3));
      paramList.add(localObject);
    }
  }
  
  private static final class a
  {
    public final Point2D a;
    public final List b = new ArrayList();
    public final List c = new ArrayList();
    
    private a(Point2D paramPoint2D)
    {
      this.a = paramPoint2D;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b\k.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */