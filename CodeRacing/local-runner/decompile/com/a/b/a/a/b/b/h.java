package com.a.b.a.a.b.b;

import com.a.b.a.a.b.e.i.a;
import com.a.b.a.a.b.n;
import com.a.b.e;
import com.a.c.c;
import com.codeforces.commons.holder.Readable;
import com.codeforces.commons.holder.Writable;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.pair.IntPair;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class h
  implements com.a.b.b
{
  private final IntPair[] a;
  private final int b;
  private final int c;
  private final Readable d;
  private final Writable e;
  private final AtomicInteger f = new AtomicInteger();
  
  public h(com.a.b.a.a.a.b paramb, Readable paramReadable, Writable paramWritable)
  {
    this.a = paramb.e().c();
    this.b = this.a.length;
    this.c = NumberUtil.toInt(Math.floor(500.0D / (this.b - 1)));
    this.d = paramReadable;
    this.e = paramWritable;
  }
  
  public void a(com.a.b.h paramh, int paramInt)
  {
    Iterator localIterator = paramh.a().iterator();
    while (localIterator.hasNext())
    {
      e locale = (e)localIterator.next();
      if ((locale instanceof com.a.b.a.a.b.d.c.b))
      {
        com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)locale;
        if (!localb.N())
        {
          double d1 = localb.b().c();
          double d2 = localb.b().d();
          double d3 = 800.0D * ((Integer)localb.t().getFirst()).intValue();
          double d4 = 800.0D * ((Integer)localb.t().getSecond()).intValue();
          double d5 = d3 + 800.0D;
          double d6 = d4 + 800.0D;
          if ((d1 >= d3) && (d1 <= d5) && (d2 >= d4) && (d2 <= d6))
          {
            localb.c((localb.u() + 1) % this.b);
            localb.a(this.a[localb.u()]);
            if (localb.u() == 1)
            {
              localb.k().a(1000 - (this.b - 1) * this.c);
              localb.m(((Integer)this.d.get()).intValue());
              if (localb.N())
              {
                localb.k().a(com.a.b.a.a.a.c.b[this.f.getAndIncrement()]);
                this.e.set(Integer.valueOf(paramInt));
              }
            }
            else
            {
              localb.k().a(this.c);
            }
          }
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\b\h.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */