package com.a.b.a.a.b.b;

import com.a.b.a.a.b.a.a;
import com.a.b.a.a.c.k;
import com.a.b.e;
import com.a.b.h;
import com.codeforces.commons.math.Math;
import com.codeforces.commons.math.NumberUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class b
  implements com.a.b.b
{
  private Map a;
  
  public void a(h paramh, int paramInt)
  {
    HashMap localHashMap1 = new HashMap();
    Iterator localIterator = paramh.a().iterator();
    while (localIterator.hasNext())
    {
      e locale = (e)localIterator.next();
      if ((locale instanceof com.a.b.a.a.b.d.c.b))
      {
        com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)locale;
        localHashMap1.put(Long.valueOf(localb.a()), Double.valueOf(localb.n()));
        if (this.a != null)
        {
          Double localDouble = (Double)this.a.get(Long.valueOf(localb.a()));
          if ((localDouble != null) && (!NumberUtil.equals(Double.valueOf(localb.n()), localDouble)))
          {
            int i = NumberUtil.toInt(Math.floor(localb.n() * 100.0D) - Math.floor(localDouble.doubleValue() * 100.0D));
            if (i != 0)
            {
              HashMap localHashMap2 = new HashMap();
              localHashMap2.put("durabilityPercentsChange", Integer.valueOf(i));
              paramh.a(new a(k.CAR_CONDITION_CHANGE, paramInt, localb, localHashMap2));
            }
          }
        }
      }
    }
    this.a = localHashMap1;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\b\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */