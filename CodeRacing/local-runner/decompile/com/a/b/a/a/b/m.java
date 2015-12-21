package com.a.b.a.a.b;

import com.a.b.e;
import com.a.b.f;
import com.a.b.g;
import com.a.c.a;
import com.a.c.b;
import com.a.c.c;
import com.a.c.d;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import java.lang.reflect.Method;
import java.util.Map;

class m
  implements d
{
  m(k paramk, Method paramMethod1, Class paramClass1, Class paramClass2, g paramg, Method paramMethod2, b paramb) {}
  
  public boolean a(c paramc1, c paramc2)
  {
    if (this.a.getDeclaringClass() == g.class) {
      return true;
    }
    e locale1 = paramc1 == null ? null : (e)k.a(this.g).get(Long.valueOf(paramc1.a()));
    e locale2 = paramc2 == null ? null : (e)k.a(this.g).get(Long.valueOf(paramc2.a()));
    boolean bool;
    if ((this.b.isInstance(locale1)) && (this.c.isInstance(locale2)))
    {
      k.a(this.g, locale1, locale2);
      bool = this.d.beforeCollision(this.g, locale1, locale2);
      k.b(this.g, locale1, locale2);
      return bool;
    }
    if ((this.b.isInstance(locale2)) && (this.c.isInstance(locale1)))
    {
      k.a(this.g, locale1, locale2);
      bool = this.d.beforeCollision(this.g, locale2, locale1);
      k.b(this.g, locale1, locale2);
      return bool;
    }
    return true;
  }
  
  public boolean b(a parama)
  {
    if (this.e.getDeclaringClass() == g.class) {
      return true;
    }
    e locale1 = parama.a() == null ? null : (e)k.a(this.g).get(Long.valueOf(parama.a().a()));
    e locale2 = parama.b() == null ? null : (e)k.a(this.g).get(Long.valueOf(parama.b().a()));
    boolean bool;
    if ((this.b.isInstance(locale1)) && (this.c.isInstance(locale2)))
    {
      k.a(this.g, locale1, locale2);
      bool = this.d.beforeResolvingCollision(new f(this.g, locale1, locale2, parama.c().copy(), parama.d().copy()));
      k.b(this.g, locale1, locale2);
      return bool;
    }
    if ((this.b.isInstance(locale2)) && (this.c.isInstance(locale1)))
    {
      k.a(this.g, locale1, locale2);
      bool = this.d.beforeResolvingCollision(new f(this.g, locale2, locale1, parama.c().copy(), parama.d().copyNegate()));
      k.b(this.g, locale1, locale2);
      return bool;
    }
    return true;
  }
  
  public void a(a parama)
  {
    this.f.a(parama);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\m.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */