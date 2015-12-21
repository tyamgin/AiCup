package com.a.b.a.a.b.b;

import com.a.b.a.a.b.a.a;
import com.a.b.a.a.c.k;
import com.a.b.e;
import com.a.b.h;
import com.a.c.c;
import com.codeforces.commons.geometry.Vector2D;
import com.codeforces.commons.holder.Readable;
import com.codeforces.commons.math.Math;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class d
  implements com.a.b.b
{
  private static final Logger a = LoggerFactory.getLogger(d.class);
  private final AtomicInteger b = new AtomicInteger();
  private final Readable c;
  private final com.a.b.a.a.a.b d;
  
  public d(Readable paramReadable, com.a.b.a.a.a.b paramb)
  {
    this.c = paramReadable;
    this.d = paramb;
  }
  
  public void a(h paramh, int paramInt)
  {
    Iterator localIterator = paramh.a().iterator();
    while (localIterator.hasNext())
    {
      e locale = (e)localIterator.next();
      if ((locale instanceof com.a.b.a.a.b.d.c.b))
      {
        com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)locale;
        if ((!com.a.b.a.a.b.e.b.a(localb)) && (!localb.N()) && (localb.G() <= 0) && ((localb.L() == null) || (localb.L().intValue() + k.DRIFTING.getDuration() < paramInt)))
        {
          c localc = localb.b();
          double d1 = Math.abs(localc.f().dotProduct(new Vector2D(1.0D, 0.0D).rotate(localc.e() + 1.5707963267948966D)));
          if (d1 >= 15.0D)
          {
            int i = this.b.incrementAndGet();
            if (this.d.z()) {
              a.debug(String.format("Car {id=%d} drifting %d at tick %d (driftingSpeed=%.2f).", new Object[] { Long.valueOf(locale.a()), Integer.valueOf(i), this.c.get(), Double.valueOf(d1) }));
            }
            localb.b(Integer.valueOf(paramInt));
            paramh.a(new a(k.DRIFTING, ((Integer)this.c.get()).intValue(), locale));
          }
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\b\d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */