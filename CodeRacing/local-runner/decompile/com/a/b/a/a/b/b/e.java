package com.a.b.a.a.b.b;

import com.a.b.a.a.b.d.e.a;
import com.a.b.b;
import com.a.b.h;
import com.a.c.c;
import java.util.Iterator;
import java.util.List;

public class e
  implements b
{
  private final double a = -4000.0D;
  private final double b = -4000.0D;
  private final double c;
  private final double d;
  
  public e(int paramInt1, int paramInt2)
  {
    this.c = ((paramInt1 + 5) * 800.0D);
    this.d = ((paramInt2 + 5) * 800.0D);
  }
  
  public void a(h paramh, int paramInt)
  {
    Iterator localIterator = paramh.a().iterator();
    while (localIterator.hasNext())
    {
      com.a.b.e locale = (com.a.b.e)localIterator.next();
      if ((locale instanceof a))
      {
        a locala = (a)locale;
        c localc = locala.b();
        if ((localc.c() < this.a) || (localc.d() < this.b) || (localc.c() >= this.c) || (localc.d() >= this.d)) {
          paramh.b(locala);
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\b\e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */