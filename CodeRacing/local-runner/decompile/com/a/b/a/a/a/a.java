package com.a.b.a.a.a;

import com.a.a.b.a.c;
import com.a.b.a.a.b.k;
import com.a.b.h;
import com.a.c.b.a.b;
import com.a.c.e;
import com.google.inject.AbstractModule;
import com.google.inject.binder.AnnotatedBindingBuilder;

public final class a
  extends AbstractModule
{
  protected void configure()
  {
    bind(com.a.b.a.class).toInstance(new com.a.b.a.a.b.a());
    c localc = new c(100.0D, 1000.0D);
    bind(e.class).toInstance(new b(10, 1, 1.0E-7D, localc));
    bind(h.class).toInstance(new k(false));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\a\a.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */