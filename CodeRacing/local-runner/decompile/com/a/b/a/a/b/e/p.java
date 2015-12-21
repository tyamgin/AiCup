package com.a.b.a.a.b.e;

import com.a.b.a.a.c.a;
import com.a.b.a.a.c.c;
import com.a.b.a.a.c.n;
import com.a.b.a.a.c.q;
import com.a.b.a.a.c.u;

public class p
{
  public static boolean a(u paramu1, u paramu2)
  {
    if ((paramu1 == null) && (paramu2 == null)) {
      return true;
    }
    if (((paramu1 == null ? 1 : 0) ^ (paramu2 == null ? 1 : 0)) != 0) {
      return false;
    }
    if (((paramu1 instanceof c)) && ((paramu2 instanceof c))) {
      return c.areFieldEquals((c)paramu1, (c)paramu2);
    }
    if (((paramu1 instanceof q)) && ((paramu2 instanceof q))) {
      return q.areFieldEquals((q)paramu1, (q)paramu2);
    }
    if (((paramu1 instanceof a)) && ((paramu2 instanceof a))) {
      return a.areFieldEquals((a)paramu1, (a)paramu2);
    }
    if (((paramu1 instanceof n)) && ((paramu2 instanceof n))) {
      return n.areFieldEquals((n)paramu1, (n)paramu2);
    }
    throw new IllegalArgumentException(String.format("Unsupported classes of units %s and %s.", new Object[] { paramu1, paramu2 }));
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\e\p.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */