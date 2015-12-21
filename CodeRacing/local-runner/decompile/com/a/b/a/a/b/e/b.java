package com.a.b.a.a.b.e;

import com.a.b.a.a.b.n;
import com.a.b.a.a.c.i;
import com.a.c.a.e;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.pair.IntPair;
import com.google.common.base.Preconditions;

public final class b
{
  public static com.a.b.a.a.c.c a(com.a.b.a.a.b.d.c.b paramb, double paramDouble, n paramn)
  {
    com.a.c.a.c localc = paramb.b().t();
    if (!(localc instanceof e)) {
      throw new IllegalArgumentException("Unsupported car form: " + localc + '.');
    }
    e locale = (e)localc;
    IntPair localIntPair = paramb.t();
    Preconditions.checkNotNull(localIntPair.getFirst());
    Preconditions.checkNotNull(localIntPair.getSecond());
    return new com.a.b.a.a.c.c(paramb.a(), paramb.b().s(), paramb.b().c(), paramb.b().d(), paramb.b().f().getX() * paramDouble, paramb.b().f().getY() * paramDouble, paramb.b().e(), paramb.b().h() * paramDouble, locale.a(), locale.b(), paramb.k().a(), paramb.l(), paramb.k().equals(paramn), paramb.m(), paramb.v(), paramb.w(), paramb.x(), paramb.y(), paramb.A(), paramb.C(), paramb.E(), paramb.G(), paramb.n(), paramb.p(), paramb.q(), ((Integer)localIntPair.getFirst()).intValue(), ((Integer)localIntPair.getSecond()).intValue(), paramb.N());
  }
  
  public static Point2D a(i.a parama, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    a(paramInt1, paramInt2, paramInt3, paramInt4);
    i locali = parama.d();
    IntPair localIntPair = parama.c()[0];
    double d1 = 16.0D;
    double d2 = 80.0D + d1 + 70.0D;
    double d3 = (140.0D + d1) * (paramInt4 * paramInt2 + paramInt1);
    Preconditions.checkNotNull(localIntPair.getFirst());
    Preconditions.checkNotNull(localIntPair.getSecond());
    switch (c.a[locali.ordinal()])
    {
    case 1: 
      return new Point2D(800.0D * (((Integer)localIntPair.getFirst()).intValue() + 0.5D), 800.0D * (((Integer)localIntPair.getSecond()).intValue() + 1) - d2 - d3);
    case 2: 
      return new Point2D(800.0D * (((Integer)localIntPair.getFirst()).intValue() + 0.5D), 800.0D * ((Integer)localIntPair.getSecond()).intValue() + d2 + d3);
    case 3: 
      return new Point2D(800.0D * ((Integer)localIntPair.getFirst()).intValue() + d2 + d3, 800.0D * (((Integer)localIntPair.getSecond()).intValue() + 0.5D));
    case 4: 
      return new Point2D(800.0D * (((Integer)localIntPair.getFirst()).intValue() + 1) - d2 - d3, 800.0D * (((Integer)localIntPair.getSecond()).intValue() + 0.5D));
    }
    throw new IllegalArgumentException("Unsupported starting direction: " + parama.d() + '.');
  }
  
  public static double b(i.a parama, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    a(paramInt1, paramInt2, paramInt3, paramInt4);
    switch (c.a[parama.d().ordinal()])
    {
    case 1: 
      return 3.141592653589793D;
    case 2: 
      return 0.0D;
    case 3: 
      return -1.5707963267948966D;
    case 4: 
      return 1.5707963267948966D;
    }
    throw new IllegalArgumentException("Unsupported starting direction: " + parama.d() + '.');
  }
  
  public static void a(com.a.b.a.a.b.d.c.b paramb, n paramn, double paramDouble)
  {
    a(paramb, paramn, paramDouble, 1.0D);
  }
  
  public static void a(com.a.b.a.a.b.d.c.b paramb, n paramn, double paramDouble1, double paramDouble2)
  {
    if ((a(paramb)) || (paramb.N()) || (paramDouble1 <= 0.0D)) {
      return;
    }
    int i = 0;
    paramDouble1 = Math.min(paramb.n(), paramDouble1);
    i = (int)(i + paramDouble1 * 100.0D);
    paramb.a(paramb.n() - paramDouble1);
    if (a(paramb))
    {
      paramb.a(0.0D);
      paramb.a(0.0D, false);
      paramb.j(0);
      paramb.l(0);
      paramb.b().b(new Vector2D(0.0D, 0.0D));
      paramb.b().e(0.0D);
      i += 100;
    }
    else
    {
      paramb.l(paramb.I() + NumberUtil.toInt(paramDouble1 * 300.0D * paramDouble2));
    }
    if ((paramn != null) && (paramn.a() != paramb.k().a())) {
      paramn.a(i);
    }
  }
  
  public static boolean a(com.a.b.a.a.b.d.c.b paramb)
  {
    return a(paramb.n());
  }
  
  public static boolean a(com.a.b.a.a.c.c paramc)
  {
    return a(paramc.getDurability());
  }
  
  public static boolean a(double paramDouble)
  {
    return paramDouble <= 1.0E-7D;
  }
  
  private static void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt2 != 2) && (paramInt2 != 4)) {
      throw new IllegalArgumentException(String.format("Argument 'teamCount' should be either 2 or 4, but got %d.", new Object[] { Integer.valueOf(paramInt2) }));
    }
    if ((paramInt1 < 0) || (paramInt1 >= paramInt2)) {
      throw new IllegalArgumentException(String.format("Argument 'teamIndex' should be non-negative and less than 'teamCount', but got %d.", new Object[] { Integer.valueOf(paramInt1) }));
    }
    if ((paramInt3 < 1) || (paramInt3 > 2)) {
      throw new IllegalArgumentException(String.format("Argument 'teamSize' should be in range of 1 to 2, but got %d.", new Object[] { Integer.valueOf(paramInt3) }));
    }
    if (paramInt2 * paramInt3 != 4) {
      throw new IllegalArgumentException(String.format("Expected exactly 4 cars on the track, but got %d.", new Object[] { Integer.valueOf(paramInt2 * paramInt3) }));
    }
    if ((paramInt4 < 0) || (paramInt4 >= paramInt3)) {
      throw new IllegalArgumentException(String.format("Argument 'carIndex' should be non-negative and less than 'teamSize', but got %d.", new Object[] { Integer.valueOf(paramInt4) }));
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\e\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */