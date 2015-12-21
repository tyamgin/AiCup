package com.a.b.a.a.b.e;

import com.a.b.a.a.c.o;
import java.util.Comparator;

final class n
  implements Comparator
{
  public int a(o paramo1, o paramo2)
  {
    if (paramo2.getScore() > paramo1.getScore()) {
      return 1;
    }
    if (paramo2.getScore() < paramo1.getScore()) {
      return -1;
    }
    if (paramo2.getId() > paramo1.getId()) {
      return 1;
    }
    if (paramo2.getId() < paramo1.getId()) {
      return -1;
    }
    return 0;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\b\e\n.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */