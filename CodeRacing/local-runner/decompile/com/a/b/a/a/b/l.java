package com.a.b.a.a.b;

import com.a.b.e;
import com.a.b.f;
import com.a.b.g;
import com.a.c.a;
import com.a.c.b;
import com.a.c.c;
import com.codeforces.commons.geometry.Point2D;
import com.codeforces.commons.geometry.Vector2D;
import java.lang.reflect.Method;
import java.util.Map;

class l
  implements b
{
  l(k paramk, Method paramMethod, Class paramClass1, Class paramClass2, g paramg) {}
  
  public void a(a parama)
  {
    if (this.a.getDeclaringClass() == g.class) {
      return;
    }
    e locale1 = parama.a() == null ? null : (e)k.a(this.e).get(Long.valueOf(parama.a().a()));
    e locale2 = parama.b() == null ? null : (e)k.a(this.e).get(Long.valueOf(parama.b().a()));
    if ((this.b.isInstance(locale1)) && (this.c.isInstance(locale2)))
    {
      k.a(this.e, locale1, locale2);
      this.d.afterCollision(new f(this.e, locale1, locale2, parama.c().copy(), parama.d().copy()));
      k.b(this.e, locale1, locale2);
    }
    else if ((this.b.isInstance(locale2)) && (this.c.isInstance(locale1)))
    {
      k.a(this.e, locale1, locale2);
      this.d.afterCollision(new f(this.e, locale2, locale1, parama.c().copy(), parama.d().copyNegate()));
      k.b(this.e, locale1, locale2);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\l.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */