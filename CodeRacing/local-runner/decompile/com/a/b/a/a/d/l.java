package com.a.b.a.a.d;

import com.a.b.a.a.b.e.d;
import com.a.b.a.a.c.h;
import java.util.concurrent.atomic.AtomicReference;

class l
  implements Runnable
{
  l(k paramk, h paramh) {}
  
  public void run()
  {
    if (k.a(this.b).get() != null) {
      return;
    }
    try
    {
      String str = k.b(this.b).a(this.a);
      k.c(this.b);
      k.d(this.b).append(str).append('\n');
      k.e(this.b).append(str).append('\n');
      if (k.b(this.a)) {
        k.a(this.b, "", false);
      }
    }
    catch (RuntimeException|Error localRuntimeException)
    {
      k.a(this.b, localRuntimeException);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\d\l.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */