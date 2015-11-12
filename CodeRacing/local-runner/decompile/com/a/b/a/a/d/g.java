package com.a.b.a.a.d;

import com.a.b.a.a.c.c;
import com.codeforces.commons.math.Math;
import java.util.Comparator;

class g
  implements Comparator
{
  g(a parama) {}
  
  public int a(c paramc1, c paramc2)
  {
    if (Math.abs(paramc1.getX() - paramc2.getX()) < Math.abs(paramc1.getY() - paramc2.getY()))
    {
      if (paramc1.getY() > paramc2.getY()) {
        return 1;
      }
      if (paramc1.getY() < paramc2.getY()) {
        return -1;
      }
    }
    else
    {
      if (paramc1.getX() > paramc2.getX()) {
        return 1;
      }
      if (paramc1.getX() < paramc2.getX()) {
        return -1;
      }
    }
    return Long.compare(paramc1.getId(), paramc2.getId());
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\d\g.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */