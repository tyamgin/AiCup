package com.a.b.a.a.b.d.e;

import com.a.b.a.a.b.d.c.b;
import com.a.b.a.a.b.n;
import com.a.b.a.a.c.r;
import com.a.b.e;
import com.a.b.f;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.reflection.Name;

public abstract class a
  extends e
{
  @Name("car")
  private final b a;
  @Name("player")
  private final n b;
  @Name("type")
  private final r c;
  @Name("creationTick")
  private final int d;
  private boolean e = true;
  
  protected a(com.a.c.a.c paramc, b paramb, n paramn, r paramr, int paramInt, Point2D paramPoint2D, Vector2D paramVector2D, double paramDouble1, double paramDouble2)
  {
    super(paramc);
    if ((Double.isNaN(paramDouble2)) || (Double.isInfinite(paramDouble2)) || (paramDouble2 <= 0.0D)) {
      throw new IllegalArgumentException("Argument 'mass' is not a positive number.");
    }
    this.a = paramb;
    this.b = paramn;
    this.c = paramr;
    this.d = paramInt;
    b().a(paramPoint2D.getX());
    b().b(paramPoint2D.getY());
    b().c(paramDouble1);
    b().l(paramDouble2);
    b().j(1.0D);
    b().k(1.0D);
    b().a(paramVector2D.copy());
  }
  
  public b k()
  {
    return this.a;
  }
  
  public n l()
  {
    return this.b;
  }
  
  public r m()
  {
    return this.c;
  }
  
  public int n()
  {
    return this.d;
  }
  
  public boolean o()
  {
    return this.e;
  }
  
  public void a(boolean paramBoolean)
  {
    this.e = paramBoolean;
  }
  
  public abstract double a(f paramf, int paramInt);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\d\e\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */