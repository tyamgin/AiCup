package com.a.b.a.a.b.b;

import com.a.b.e;
import com.a.b.h;
import java.util.Iterator;
import java.util.List;

public class c
  implements com.a.b.b
{
  public void a(h paramh, int paramInt)
  {
    Iterator localIterator = paramh.a().iterator();
    while (localIterator.hasNext())
    {
      e locale = (e)localIterator.next();
      if ((locale instanceof com.a.b.a.a.b.d.c.b))
      {
        com.a.b.a.a.b.d.c.b localb = (com.a.b.a.a.b.d.c.b)locale;
        if (com.a.b.a.a.b.e.b.a(localb))
        {
          if (localb.o() == null) {
            localb.a(Integer.valueOf(paramInt + 300));
          }
          if (paramInt >= localb.o().intValue())
          {
            localb.a(null);
            localb.a(1.0D);
          }
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\b\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */