package com.a.b.a.a.b.b;

import com.a.a.a.a.c;
import com.a.b.b;
import com.a.b.e;
import com.a.b.h;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import com.codeforces.commons.pair.IntPair;
import java.util.Iterator;
import java.util.List;

public class a
  implements b
{
  private final IntPair[] a;
  private final Runnable b;
  
  public a(IntPair[] paramArrayOfIntPair, Runnable paramRunnable)
  {
    this.a = paramArrayOfIntPair;
    this.b = paramRunnable;
  }
  
  public void a(h paramh, int paramInt)
  {
    int i = 0;
    Iterator localIterator = paramh.a().iterator();
    while (localIterator.hasNext())
    {
      e locale = (e)localIterator.next();
      if ((locale instanceof com.a.b.a.a.b.d.a.a)) {
        i++;
      }
    }
    int j = NumberUtil.toInt(Math.floor(0.25D * this.a.length));
    if ((i < j) && (c.d() < 0.0015D)) {
      this.b.run();
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\b\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */