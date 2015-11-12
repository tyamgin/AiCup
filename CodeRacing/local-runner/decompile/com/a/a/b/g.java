package com.a.a.b;

import com.a.a.b.b.b;
import com.a.a.b.b.d;
import com.a.a.b.b.h;
import com.a.a.b.b.k;
import com.a.a.b.b.m;
import com.a.a.b.b.n;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.pair.LongPair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.math3.geometry.Vector;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class g
{
  private static final Logger a = Logger.getLogger(g.class);
  private static final com.a.a.b.b.f b = new com.a.a.b.b.f(null, null, null, null, 0.0D, 0.0D);
  private final int c;
  private final int d;
  private final double e;
  private final double f;
  private final double g;
  private final com.a.a.b.a.a h;
  private final com.a.a.b.e.c i;
  private final Map j = new HashMap();
  private final SortedSet k = new TreeSet(a.a());
  private final Map l = new HashMap();
  private final SortedSet m = new TreeSet(b.a());
  
  public g()
  {
    this(10);
  }
  
  public g(int paramInt)
  {
    this(paramInt, 60);
  }
  
  public g(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, 1.0E-7D);
  }
  
  public g(int paramInt1, int paramInt2, double paramDouble)
  {
    this(paramInt1, paramInt2, paramDouble, new com.a.a.b.a.f());
  }
  
  public g(int paramInt1, int paramInt2, double paramDouble, com.a.a.b.a.a parama)
  {
    this(paramInt1, paramInt2, paramDouble, parama, null);
  }
  
  public g(int paramInt1, int paramInt2, double paramDouble, com.a.a.b.a.a parama, com.a.a.b.e.c paramc)
  {
    if (paramInt1 < 1) {
      throw new IllegalArgumentException("Argument 'iterationCountPerStep' is zero or negative.");
    }
    if (paramInt2 < 1) {
      throw new IllegalArgumentException("Argument 'stepCountPerTimeUnit' is zero or negative.");
    }
    if ((Double.isNaN(paramDouble)) || (Double.isInfinite(paramDouble)) || (paramDouble < 1.0E-100D) || (paramDouble > 1.0D)) {
      throw new IllegalArgumentException("Argument 'epsilon' should be between 1.0E-100 and 1.0.");
    }
    if (parama == null) {
      throw new IllegalArgumentException("Argument 'bodyList' is null.");
    }
    this.d = paramInt2;
    this.c = paramInt1;
    this.e = (1.0D / (paramInt2 * paramInt1));
    this.f = paramDouble;
    this.g = (paramDouble * paramDouble);
    this.h = parama;
    this.i = paramc;
    a(new com.a.a.b.b.a(paramDouble));
    a(new b(paramDouble));
    a(new com.a.a.b.b.c(paramDouble));
    a(new com.a.a.b.b.g(paramDouble));
    a(new h(paramDouble));
    a(new com.a.a.b.b.i(paramDouble));
    a(new com.a.a.b.b.j(paramDouble));
    a(new k(paramDouble));
    a(new m(paramDouble));
    a(new n(paramDouble));
  }
  
  public int a()
  {
    return this.d;
  }
  
  public double b()
  {
    return this.f;
  }
  
  public void a(a parama)
  {
    if ((parama.c() == null) || (parama.d() == 0.0D)) {
      throw new IllegalArgumentException("Specify form and mass of 'body' before adding to the world.");
    }
    this.h.a(parama);
  }
  
  public void b(a parama)
  {
    this.h.b(parama);
  }
  
  public boolean c(a parama)
  {
    return this.h.c(parama);
  }
  
  public List c()
  {
    return this.h.a();
  }
  
  public void d()
  {
    ArrayList localArrayList = new ArrayList(c());
    Iterator localIterator1 = localArrayList.iterator();
    Object localObject1;
    while (localIterator1.hasNext())
    {
      localObject1 = (a)localIterator1.next();
      if (c((a)localObject1))
      {
        ((a)localObject1).C();
        ((a)localObject1).p();
      }
    }
    for (int n = 1; n <= this.c; n++)
    {
      localObject1 = localArrayList.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (a)((Iterator)localObject1).next();
        if (c((a)localObject2))
        {
          ((a)localObject2).q();
          d((a)localObject2);
          ((a)localObject2).C();
        }
      }
      localObject1 = new HashMap();
      Object localObject2 = localArrayList.iterator();
      while (((Iterator)localObject2).hasNext())
      {
        a locala1 = (a)((Iterator)localObject2).next();
        if ((!locala1.e()) && (c(locala1)))
        {
          Iterator localIterator3 = this.h.d(locala1).iterator();
          while (localIterator3.hasNext())
          {
            a locala2 = (a)localIterator3.next();
            if ((c(locala1)) && (c(locala2))) {
              a(locala1, locala2, (Map)localObject1);
            }
          }
        }
      }
    }
    Iterator localIterator2 = localArrayList.iterator();
    while (localIterator2.hasNext())
    {
      localObject1 = (a)localIterator2.next();
      if (c((a)localObject1))
      {
        ((a)localObject1).c(0.0D, 0.0D);
        ((a)localObject1).n(0.0D);
      }
    }
  }
  
  private void a(a parama1, a parama2, Map paramMap)
  {
    a locala1;
    a locala2;
    if (parama1.a() > parama2.a())
    {
      locala1 = parama2;
      locala2 = parama1;
    }
    else
    {
      locala1 = parama1;
      locala2 = parama2;
    }
    LongPair localLongPair = new LongPair(Long.valueOf(locala1.a()), Long.valueOf(locala2.a()));
    com.a.a.b.b.f localf = (com.a.a.b.b.f)paramMap.get(localLongPair);
    if (localf != null) {
      return;
    }
    Iterator localIterator = this.m.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (b)localIterator.next();
      if (!((b)localObject).c.a(locala1, locala2))
      {
        paramMap.put(localLongPair, b);
        return;
      }
      if ((!c(locala1)) || (!c(locala2))) {
        return;
      }
    }
    localIterator = this.k.iterator();
    while (localIterator.hasNext())
    {
      localObject = (a)localIterator.next();
      if (((a)localObject).c.c(locala1, locala2))
      {
        localf = ((a)localObject).c.d(locala1, locala2);
        break;
      }
    }
    if (localf == null)
    {
      paramMap.put(localLongPair, b);
    }
    else
    {
      paramMap.put(localLongPair, localf);
      a(localf);
    }
  }
  
  private void a(com.a.a.b.b.f paramf)
  {
    a locala1 = paramf.a();
    a locala2 = paramf.b();
    if ((locala1.e()) && (locala2.e())) {
      throw new IllegalArgumentException("Both " + locala1 + " and " + locala2 + " are static.");
    }
    Object localObject1 = this.m.iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (b)((Iterator)localObject1).next();
      if (!((b)localObject2).c.a(paramf)) {
        return;
      }
      if ((!c(locala1)) || (!c(locala2))) {
        return;
      }
    }
    b(paramf);
    localObject1 = a(paramf.d());
    Object localObject2 = a(locala1.B(), paramf.c());
    Vector3D localVector3D1 = a(locala2.B(), paramf.c());
    Vector3D localVector3D2 = a(locala1.y()).crossProduct((Vector)localObject2);
    Vector3D localVector3D3 = a(locala2.y()).crossProduct(localVector3D1);
    Vector3D localVector3D4 = a(locala1.u()).add(localVector3D2);
    Vector3D localVector3D5 = a(locala2.u()).add(localVector3D3);
    Vector3D localVector3D6 = localVector3D4.subtract(localVector3D5);
    double d1 = -localVector3D6.dotProduct((Vector)localObject1);
    if (d1 > -this.f)
    {
      a(locala1, locala2, (Vector3D)localObject1, (Vector3D)localObject2, localVector3D1, localVector3D6);
      b(locala1, locala2, (Vector3D)localObject1, (Vector3D)localObject2, localVector3D1, localVector3D6);
    }
    if (paramf.e() >= this.f) {
      a(locala1, locala2, paramf);
    }
    locala1.C();
    locala2.C();
    Iterator localIterator = this.m.iterator();
    while (localIterator.hasNext())
    {
      b localb = (b)localIterator.next();
      localb.c.b(paramf);
    }
  }
  
  private void a(a parama1, a parama2, Vector3D paramVector3D1, Vector3D paramVector3D2, Vector3D paramVector3D3, Vector3D paramVector3D4)
  {
    Double localDouble;
    if ((this.i == null) || ((localDouble = this.i.a(parama1, parama2)) == null)) {
      localDouble = Double.valueOf(parama1.m() * parama2.m());
    }
    Vector3D localVector3D1 = paramVector3D2.crossProduct(paramVector3D1).scalarMultiply(parama1.h()).crossProduct(paramVector3D2);
    Vector3D localVector3D2 = paramVector3D3.crossProduct(paramVector3D1).scalarMultiply(parama2.h()).crossProduct(paramVector3D3);
    double d1 = parama1.f() + parama2.f() + paramVector3D1.dotProduct(localVector3D1.add(localVector3D2));
    double d2 = -1.0D * (1.0D + localDouble.doubleValue()) * paramVector3D4.dotProduct(paramVector3D1) / d1;
    if (Math.abs(d2) < this.f) {
      return;
    }
    Vector3D localVector3D3;
    Vector3D localVector3D4;
    Vector3D localVector3D5;
    Vector3D localVector3D6;
    if (!parama1.e())
    {
      localVector3D3 = paramVector3D1.scalarMultiply(d2 * parama1.f());
      localVector3D4 = a(parama1.u()).add(localVector3D3);
      parama1.b(localVector3D4.getX(), localVector3D4.getY());
      localVector3D5 = paramVector3D2.crossProduct(paramVector3D1.scalarMultiply(d2)).scalarMultiply(parama1.h());
      localVector3D6 = a(parama1.y()).add(localVector3D5);
      parama1.l(localVector3D6.getZ());
    }
    if (!parama2.e())
    {
      localVector3D3 = paramVector3D1.scalarMultiply(d2 * parama2.f());
      localVector3D4 = a(parama2.u()).subtract(localVector3D3);
      parama2.b(localVector3D4.getX(), localVector3D4.getY());
      localVector3D5 = paramVector3D3.crossProduct(paramVector3D1.scalarMultiply(d2)).scalarMultiply(parama2.h());
      localVector3D6 = a(parama2.y()).subtract(localVector3D5);
      parama2.l(localVector3D6.getZ());
    }
  }
  
  private void b(a parama1, a parama2, Vector3D paramVector3D1, Vector3D paramVector3D2, Vector3D paramVector3D3, Vector3D paramVector3D4)
  {
    Vector3D localVector3D1 = paramVector3D4.subtract(paramVector3D1.scalarMultiply(paramVector3D4.dotProduct(paramVector3D1)));
    if (localVector3D1.getNormSq() < this.g) {
      return;
    }
    localVector3D1 = localVector3D1.normalize();
    double d1 = Math.sqrt(parama1.n() * parama2.n()) * Math.SQRT_2 * Math.abs(paramVector3D4.dotProduct(paramVector3D1)) / paramVector3D4.getNorm();
    if (d1 < this.f) {
      return;
    }
    Vector3D localVector3D2 = paramVector3D2.crossProduct(localVector3D1).scalarMultiply(parama1.h()).crossProduct(paramVector3D2);
    Vector3D localVector3D3 = paramVector3D3.crossProduct(localVector3D1).scalarMultiply(parama2.h()).crossProduct(paramVector3D3);
    double d2 = parama1.f() + parama2.f() + localVector3D1.dotProduct(localVector3D2.add(localVector3D3));
    double d3 = -1.0D * d1 * paramVector3D4.dotProduct(localVector3D1) / d2;
    if (Math.abs(d3) < this.f) {
      return;
    }
    Vector3D localVector3D4;
    Vector3D localVector3D5;
    Vector3D localVector3D6;
    Vector3D localVector3D7;
    if (!parama1.e())
    {
      localVector3D4 = localVector3D1.scalarMultiply(d3 * parama1.f());
      localVector3D5 = a(parama1.u()).add(localVector3D4);
      parama1.b(localVector3D5.getX(), localVector3D5.getY());
      localVector3D6 = paramVector3D2.crossProduct(localVector3D1.scalarMultiply(d3)).scalarMultiply(parama1.h());
      localVector3D7 = a(parama1.y()).add(localVector3D6);
      parama1.l(localVector3D7.getZ());
    }
    if (!parama2.e())
    {
      localVector3D4 = localVector3D1.scalarMultiply(d3 * parama2.f());
      localVector3D5 = a(parama2.u()).subtract(localVector3D4);
      parama2.b(localVector3D5.getX(), localVector3D5.getY());
      localVector3D6 = paramVector3D3.crossProduct(localVector3D1.scalarMultiply(d3)).scalarMultiply(parama2.h());
      localVector3D7 = a(parama2.y()).subtract(localVector3D6);
      parama2.l(localVector3D7.getZ());
    }
  }
  
  private void d(a parama)
  {
    e(parama);
    f(parama);
  }
  
  private void e(a parama)
  {
    if (parama.u().getSquaredLength() > 0.0D) {
      parama.r().add(parama.u().copy().multiply(this.e));
    }
    if (parama.w().getSquaredLength() > 0.0D) {
      parama.u().add(parama.w().copy().multiply(parama.f()).multiply(this.e));
    }
    if (parama.i() >= 1.0D)
    {
      parama.a(parama.v().copy());
    }
    else if (parama.i() > 0.0D)
    {
      parama.o(this.e);
      if (parama.u().nearlyEquals(parama.v(), this.f)) {
        parama.a(parama.v().copy());
      }
    }
    parama.u().subtract(parama.v());
    parama.e(this.e);
    parama.u().add(parama.v());
  }
  
  private void f(a parama)
  {
    parama.k(parama.x() + parama.y() * this.e);
    parama.l(parama.y() + parama.A() * parama.h() * this.e);
    if (parama.j() >= 1.0D)
    {
      parama.l(parama.z());
    }
    else if (parama.j() > 0.0D)
    {
      parama.p(this.e);
      if (NumberUtil.nearlyEquals(Double.valueOf(parama.y()), Double.valueOf(parama.z()), this.f)) {
        parama.l(parama.z());
      }
    }
    double d1 = parama.y() - parama.z();
    if (Math.abs(d1) > 0.0D)
    {
      double d2 = parama.l() * this.e;
      if (d2 >= Math.abs(d1)) {
        parama.l(parama.z());
      } else if (d2 > 0.0D) {
        if (d1 > 0.0D) {
          parama.l(d1 - d2 + parama.z());
        } else {
          parama.l(d1 + d2 + parama.z());
        }
      }
    }
  }
  
  private void a(a parama1, a parama2, com.a.a.b.b.f paramf)
  {
    if (parama1.e())
    {
      parama2.r().subtract(paramf.d().multiply(paramf.e() + this.f));
    }
    else if (parama2.e())
    {
      parama1.r().add(paramf.d().multiply(paramf.e() + this.f));
    }
    else
    {
      Vector2D localVector2D = paramf.d().multiply(0.5D * (paramf.e() + this.f));
      parama1.r().add(localVector2D);
      parama2.r().subtract(localVector2D);
    }
  }
  
  public void a(d paramd, String paramString, double paramDouble)
  {
    c.a(paramString);
    if (this.j.containsKey(paramString)) {
      throw new IllegalArgumentException("Collider '" + paramString + "' is already registered.");
    }
    a locala = new a(paramString, paramDouble, paramd, null);
    this.j.put(paramString, locala);
    this.k.add(locala);
  }
  
  public void a(d paramd, String paramString)
  {
    a(paramd, paramString, 0.0D);
  }
  
  private void a(d paramd)
  {
    a(paramd, paramd.getClass().getSimpleName());
  }
  
  public void a(com.a.a.b.d.a parama, String paramString, double paramDouble)
  {
    c.a(paramString);
    if (this.l.containsKey(paramString)) {
      throw new IllegalArgumentException("Listener '" + paramString + "' is already registered.");
    }
    b localb = new b(paramString, paramDouble, parama, null);
    this.l.put(paramString, localb);
    this.m.add(localb);
  }
  
  public void a(com.a.a.b.d.a parama, String paramString)
  {
    a(parama, paramString, 0.0D);
  }
  
  public boolean a(String paramString)
  {
    c.a(paramString);
    return this.l.containsKey(paramString);
  }
  
  private static void b(com.a.a.b.b.f paramf)
  {
    if ((paramf.e() >= paramf.a().c().d() * 0.25D) || (paramf.e() >= paramf.b().c().d() * 0.25D))
    {
      if (a.isEnabledFor(Level.WARN)) {
        a.warn("Resolving collision (big depth) " + paramf + '.');
      }
    }
    else if (a.isDebugEnabled()) {
      a.debug("Resolving collision " + paramf + '.');
    }
  }
  
  private static Vector3D a(double paramDouble)
  {
    return new Vector3D(0.0D, 0.0D, paramDouble);
  }
  
  private static Vector3D a(Vector2D paramVector2D)
  {
    return new Vector3D(paramVector2D.getX(), paramVector2D.getY(), 0.0D);
  }
  
  private static Vector3D a(Point2D paramPoint2D1, Point2D paramPoint2D2)
  {
    return a(new Vector2D(paramPoint2D1, paramPoint2D2));
  }
  
  private static final class b
    extends c
  {
    private static final Comparator d = new j();
    public final double b;
    public final com.a.a.b.d.a c;
    
    private b(String paramString, double paramDouble, com.a.a.b.d.a parama)
    {
      super();
      this.b = paramDouble;
      this.c = parama;
    }
  }
  
  private static final class a
    extends c
  {
    private static final Comparator d = new i();
    public final double b;
    public final d c;
    
    private a(String paramString, double paramDouble, d paramd)
    {
      super();
      this.b = paramDouble;
      this.c = paramd;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\g.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */