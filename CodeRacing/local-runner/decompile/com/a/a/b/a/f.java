package com.a.a.b.a;

import com.a.a.b.a;
import com.a.a.b.c.c;
import com.codeforces.commons.math.Math;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class f
  extends b
{
  private final List a = new LinkedList();
  
  public void a(a parama)
  {
    e(parama);
    if (this.a.contains(parama)) {
      throw new IllegalStateException(parama + " is already added.");
    }
    this.a.add(parama);
  }
  
  public void b(a parama)
  {
    if (parama == null) {
      return;
    }
    Iterator localIterator = this.a.iterator();
    while (localIterator.hasNext()) {
      if (((a)localIterator.next()).b(parama))
      {
        localIterator.remove();
        return;
      }
    }
  }
  
  public boolean c(a parama)
  {
    e(parama);
    return this.a.contains(parama);
  }
  
  public List a()
  {
    return Collections.unmodifiableList(this.a);
  }
  
  public List d(a parama)
  {
    e(parama);
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    Iterator localIterator = this.a.iterator();
    while (localIterator.hasNext())
    {
      a locala = (a)localIterator.next();
      if (locala.b(parama)) {
        i = 1;
      } else if (((!parama.e()) || (!locala.e())) && (Math.sqr(locala.c().d() + parama.c().d()) >= locala.a(parama))) {
        localArrayList.add(locala);
      }
    }
    if (i == 0) {
      throw new IllegalStateException("Can't find " + parama + '.');
    }
    return Collections.unmodifiableList(localArrayList);
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\b\a\f.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */