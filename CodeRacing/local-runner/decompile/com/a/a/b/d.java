package com.a.a.b;

import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.text.StringUtil;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class d
{
  private a a;
  private double b;
  private Map c;
  private SortedSet d;
  
  public d()
  {
    this.a = new a(0.0D, 0.0D, null);
  }
  
  public d(d paramd)
  {
    this.a = new a(paramd.a, null);
    this.b = paramd.b;
  }
  
  public Point2D g()
  {
    return this.a;
  }
  
  public void a(Point2D paramPoint2D)
  {
    Point2D localPoint2D1 = this.a.copy();
    Point2D localPoint2D2 = paramPoint2D.copy();
    Iterator localIterator;
    b localb;
    if (this.d != null)
    {
      localIterator = this.d.iterator();
      while (localIterator.hasNext())
      {
        localb = (b)localIterator.next();
        if (!localb.c.b(localPoint2D1.copy(), localPoint2D2)) {
          return;
        }
      }
    }
    this.a = new a(localPoint2D2, null);
    if (this.d != null)
    {
      localIterator = this.d.iterator();
      while (localIterator.hasNext())
      {
        localb = (b)localIterator.next();
        localb.c.a(localPoint2D1.copy(), localPoint2D2.copy());
      }
    }
  }
  
  public double h()
  {
    return this.b;
  }
  
  public void d(double paramDouble)
  {
    this.b = paramDouble;
  }
  
  public void i()
  {
    while (this.b > 3.141592653589793D) {
      this.b -= 6.283185307179586D;
    }
    while (this.b < -3.141592653589793D) {
      this.b += 6.283185307179586D;
    }
  }
  
  public void a(com.a.a.b.d.c paramc, String paramString, double paramDouble)
  {
    c.a(paramString);
    if (this.c == null)
    {
      this.c = new HashMap(1);
      this.d = new TreeSet(b.a());
    }
    else if (this.c.containsKey(paramString))
    {
      throw new IllegalArgumentException("Listener '" + paramString + "' is already registered.");
    }
    b localb = new b(paramString, paramDouble, paramc, null);
    this.c.put(paramString, localb);
    this.d.add(localb);
  }
  
  public void a(com.a.a.b.d.c paramc, String paramString)
  {
    a(paramc, paramString, 0.0D);
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[0]);
  }
  
  private static final class b
    extends c
  {
    private static final Comparator d = new f();
    public final double b;
    public final com.a.a.b.d.c c;
    
    private b(String paramString, double paramDouble, com.a.a.b.d.c paramc)
    {
      super();
      this.b = paramDouble;
      this.c = paramc;
    }
  }
  
  private final class a
    extends Point2D
  {
    private a(double paramDouble1, double paramDouble2)
    {
      super(paramDouble2);
    }
    
    private a(Point2D paramPoint2D)
    {
      super();
    }
    
    public void setX(double paramDouble)
    {
      a(Double.valueOf(paramDouble));
    }
    
    public void setY(double paramDouble)
    {
      b(Double.valueOf(paramDouble));
    }
    
    public Point2D add(Vector2D paramVector2D)
    {
      Point2D localPoint2D1 = super.copy();
      Point2D localPoint2D2 = super.copy().add(paramVector2D);
      return a(localPoint2D1, localPoint2D2);
    }
    
    public Point2D add(double paramDouble1, double paramDouble2)
    {
      Point2D localPoint2D1 = super.copy();
      Point2D localPoint2D2 = super.copy().add(paramDouble1, paramDouble2);
      return a(localPoint2D1, localPoint2D2);
    }
    
    public Point2D subtract(Vector2D paramVector2D)
    {
      Point2D localPoint2D1 = super.copy();
      Point2D localPoint2D2 = super.copy().subtract(paramVector2D);
      return a(localPoint2D1, localPoint2D2);
    }
    
    public void a(Double paramDouble)
    {
      Point2D localPoint2D1 = super.copy();
      Point2D localPoint2D2 = super.copy();
      localPoint2D2.setFirst(paramDouble);
      a(localPoint2D1, localPoint2D2);
    }
    
    public void b(Double paramDouble)
    {
      Point2D localPoint2D1 = super.copy();
      Point2D localPoint2D2 = super.copy();
      localPoint2D2.setSecond(paramDouble);
      a(localPoint2D1, localPoint2D2);
    }
    
    private Point2D a(Point2D paramPoint2D1, Point2D paramPoint2D2)
    {
      Iterator localIterator;
      d.b localb;
      if (d.a(d.this) != null)
      {
        localIterator = d.a(d.this).iterator();
        while (localIterator.hasNext())
        {
          localb = (d.b)localIterator.next();
          if (!localb.c.b(paramPoint2D1.copy(), paramPoint2D2)) {
            return this;
          }
        }
      }
      super.setFirst(paramPoint2D2.getFirst());
      super.setSecond(paramPoint2D2.getSecond());
      if (d.a(d.this) != null)
      {
        localIterator = d.a(d.this).iterator();
        while (localIterator.hasNext())
        {
          localb = (d.b)localIterator.next();
          localb.c.a(paramPoint2D1.copy(), paramPoint2D2.copy());
        }
      }
      return this;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */