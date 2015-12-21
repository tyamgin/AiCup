package com.a.b.a.a.b.b;

import com.a.b.e;
import com.a.b.h;
import com.a.c.c;
import java.util.Iterator;
import java.util.List;

public class f
  implements com.a.b.b
{
  public void a(h paramh, int paramInt)
  {
    Iterator localIterator = paramh.a().iterator();
    while (localIterator.hasNext())
    {
      e locale = (e)localIterator.next();
      Object localObject;
      if ((locale instanceof com.a.b.a.a.b.d.c.b))
      {
        localObject = (com.a.b.a.a.b.d.c.b)locale;
        ((com.a.b.a.a.b.d.c.b)localObject).z();
        ((com.a.b.a.a.b.d.c.b)localObject).B();
        ((com.a.b.a.a.b.d.c.b)localObject).D();
        ((com.a.b.a.a.b.d.c.b)localObject).F();
        if (((com.a.b.a.a.b.d.c.b)localObject).G() > 0)
        {
          ((com.a.b.a.a.b.d.c.b)localObject).H();
          if (((com.a.b.a.a.b.d.c.b)localObject).G() <= 0)
          {
            ((com.a.b.a.a.b.d.c.b)localObject).b().a(Double.valueOf(0.25D));
            ((com.a.b.a.a.b.d.c.b)localObject).b().i(0.008726646259971648D);
          }
        }
        ((com.a.b.a.a.b.d.c.b)localObject).J();
      }
      else if ((locale instanceof com.a.b.a.a.b.d.a.a))
      {
        localObject = (com.a.b.a.a.b.d.a.a)locale;
        if (((com.a.b.a.a.b.d.a.a)localObject).l() > 1)
        {
          ((com.a.b.a.a.b.d.a.a)localObject).c(((com.a.b.a.a.b.d.a.a)localObject).l() - 1);
        }
        else
        {
          ((com.a.b.a.a.b.d.a.a)localObject).c(0);
          paramh.b((e)localObject);
        }
      }
      else if ((locale instanceof com.a.b.a.a.b.d.d.a))
      {
        localObject = (com.a.b.a.a.b.d.d.a)locale;
        if (((com.a.b.a.a.b.d.d.a)localObject).k() > 1)
        {
          ((com.a.b.a.a.b.d.d.a)localObject).c(((com.a.b.a.a.b.d.d.a)localObject).k() - 1);
        }
        else
        {
          ((com.a.b.a.a.b.d.d.a)localObject).c(0);
          paramh.b((e)localObject);
        }
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\b\f.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */