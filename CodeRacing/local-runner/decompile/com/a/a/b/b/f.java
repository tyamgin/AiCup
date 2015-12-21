package com.a.a.b.b;

import com.a.a.b.a;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.text.StringUtil;
import org.apache.log4j.Logger;

public class f
{
  private static final Logger a = Logger.getLogger(f.class);
  private final a b;
  private final a c;
  private final Point2D d;
  private final Vector2D e;
  private final double f;
  
  public f(a parama1, a parama2, Point2D paramPoint2D, Vector2D paramVector2D, double paramDouble1, double paramDouble2)
  {
    this.b = parama1;
    this.c = parama2;
    this.d = paramPoint2D;
    this.e = paramVector2D;
    if ((paramDouble1 < 0.0D) && (paramDouble1 > -paramDouble2)) {
      this.f = 0.0D;
    } else {
      this.f = paramDouble1;
    }
    if ((Double.isNaN(this.f)) || (Double.isInfinite(this.f)) || (this.f < 0.0D)) {
      a.error(String.format("Argument 'depth' should be non-negative number but got %s (%s and %s).", new Object[] { Double.valueOf(this.f), parama1, parama2 }));
    }
  }
  
  public a a()
  {
    return this.b;
  }
  
  public a b()
  {
    return this.c;
  }
  
  public Point2D c()
  {
    return this.d.copy();
  }
  
  public Vector2D d()
  {
    return this.e.copy();
  }
  
  public double e()
  {
    return this.f;
  }
  
  public String toString()
  {
    return StringUtil.toString(this, false, new String[0]);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b\f.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */