package com.a.a.b;

import java.util.Comparator;

final class i
  implements Comparator
{
  public int a(g.a parama1, g.a parama2)
  {
    int i = Double.compare(parama2.b, parama1.b);
    if (i != 0) {
      return i;
    }
    return parama1.a.compareTo(parama2.a);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\i.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */