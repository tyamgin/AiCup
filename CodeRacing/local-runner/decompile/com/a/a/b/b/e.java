package com.a.a.b.b;

import com.a.a.b.a;
import com.a.a.b.c.c;
import com.codeforces.commons.geometry.Vector2D;

public abstract class e
  implements d
{
  protected final double a;
  
  protected e(double paramDouble)
  {
    this.a = paramDouble;
  }
  
  public final boolean c(a parama1, a parama2)
  {
    return (a(parama1, parama2)) || (a(parama2, parama1));
  }
  
  public final f d(a parama1, a parama2)
  {
    if (a(parama1, parama2)) {
      return b(parama1, parama2);
    }
    if (a(parama2, parama1))
    {
      f localf = b(parama2, parama1);
      return localf == null ? null : new f(parama1, parama2, localf.c(), localf.d().negate(), localf.e(), this.a);
    }
    throw new IllegalArgumentException(String.format("Unsupported %s of %s or %s of %s.", new Object[] { c.a(parama1.c()), parama1, c.a(parama2.c()), parama2 }));
  }
  
  protected abstract boolean a(a parama1, a parama2);
  
  protected abstract f b(a parama1, a parama2);
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\b\e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */