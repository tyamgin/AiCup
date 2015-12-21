package com.a.b.a.a.b;

import com.a.b.a.a.b.a.a;
import com.a.b.f;
import com.a.b.g;
import com.a.b.h;
import com.a.c.c;
import com.google.inject.Inject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class k
  implements h
{
  @Inject
  private com.a.c.e a;
  private final Map b = new HashMap();
  private final Map c = new HashMap();
  private final List d = new ArrayList();
  private final boolean e;
  
  public k(boolean paramBoolean)
  {
    this.e = paramBoolean;
  }
  
  public com.a.b.e a(com.a.b.e parame)
  {
    c localc = this.a.a(parame.b());
    this.a.b(localc);
    this.b.put(Long.valueOf(parame.a()), parame);
    this.c.put(Long.valueOf(localc.a()), parame);
    parame.a(localc);
    return parame;
  }
  
  public void b(com.a.b.e parame)
  {
    this.a.d(parame.b());
    this.b.remove(Long.valueOf(parame.a()));
    this.c.remove(Long.valueOf(parame.b().a()));
  }
  
  public List a()
  {
    return Collections.unmodifiableList(new ArrayList(this.b.values()));
  }
  
  public a a(a parama)
  {
    this.d.add(parama);
    return parama;
  }
  
  public List b()
  {
    return Collections.unmodifiableList(this.d);
  }
  
  public void a(int paramInt)
  {
    Iterator localIterator = this.d.iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (a)localIterator.next();
      if (paramInt > ((a)localObject).c())
      {
        ((a)localObject).e();
        if (((a)localObject).f()) {
          localIterator.remove();
        }
      }
    }
    com.a.b.e locale;
    if (this.e)
    {
      localObject = this.b.values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        locale = (com.a.b.e)((Iterator)localObject).next();
        this.a.b(locale.b());
      }
    }
    else
    {
      localObject = this.b.values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        locale = (com.a.b.e)((Iterator)localObject).next();
        if (!locale.b().u()) {
          this.a.b(locale.b());
        }
      }
    }
    this.a.a();
    if (this.e)
    {
      localObject = this.b.values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        locale = (com.a.b.e)((Iterator)localObject).next();
        locale.a(this.a.a(locale.b()));
      }
    }
    else
    {
      localObject = this.b.values().iterator();
      while (((Iterator)localObject).hasNext())
      {
        locale = (com.a.b.e)((Iterator)localObject).next();
        if (!locale.b().u()) {
          locale.a(this.a.a(locale.b()));
        }
      }
    }
  }
  
  public void a(Class paramClass1, Class paramClass2, g paramg)
  {
    Method localMethod1;
    try
    {
      localMethod1 = paramg.getClass().getMethod("beforeCollision", new Class[] { h.class, com.a.b.e.class, com.a.b.e.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException1)
    {
      throw new IllegalArgumentException(String.format("Listener %s doesn't implement beforeCollision(...) method.", new Object[] { paramg.getClass().getSimpleName() }), localNoSuchMethodException1);
    }
    Method localMethod2;
    try
    {
      localMethod2 = paramg.getClass().getMethod("beforeResolvingCollision", new Class[] { f.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException2)
    {
      throw new IllegalArgumentException(String.format("Listener %s doesn't implement beforeResolvingCollision(...) method.", new Object[] { paramg.getClass().getSimpleName() }), localNoSuchMethodException2);
    }
    Method localMethod3;
    try
    {
      localMethod3 = paramg.getClass().getMethod("afterCollision", new Class[] { f.class });
    }
    catch (NoSuchMethodException localNoSuchMethodException3)
    {
      throw new IllegalArgumentException(String.format("Listener %s doesn't implement afterCollision(...) method.", new Object[] { paramg.getClass().getSimpleName() }), localNoSuchMethodException3);
    }
    l locall = new l(this, localMethod3, paramClass1, paramClass2, paramg);
    if ((localMethod1.getDeclaringClass() == g.class) && (localMethod2.getDeclaringClass() == g.class)) {
      this.a.a(locall);
    } else {
      this.a.a(new m(this, localMethod1, paramClass1, paramClass2, paramg, localMethod2, locall));
    }
  }
  
  public int c()
  {
    return this.a.b();
  }
  
  private void a(com.a.b.e parame1, com.a.b.e parame2)
  {
    if (this.e)
    {
      parame1.a(this.a.a(parame1.b()));
      parame2.a(this.a.a(parame2.b()));
    }
    else
    {
      if (!parame1.b().u()) {
        parame1.a(this.a.a(parame1.b()));
      }
      if (!parame2.b().u()) {
        parame2.a(this.a.a(parame2.b()));
      }
    }
  }
  
  private void b(com.a.b.e parame1, com.a.b.e parame2)
  {
    if (this.e)
    {
      this.a.c(parame1.b());
      this.a.c(parame2.b());
    }
    else
    {
      if (!parame1.b().u()) {
        this.a.c(parame1.b());
      }
      if (!parame2.b().u()) {
        this.a.c(parame2.b());
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\k.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */