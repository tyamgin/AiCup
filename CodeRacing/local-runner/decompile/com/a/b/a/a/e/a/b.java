package com.a.b.a.a.e.a;

import com.a.b.a.a.c.c;
import com.a.b.a.a.c.l;
import com.a.b.a.a.c.m;
import com.a.b.a.a.c.v;

public class b
  implements f
{
  private final int a;
  
  public b(int paramInt)
  {
    this.a = paramInt;
  }
  
  public int a()
  {
    return 1;
  }
  
  public void a(l paraml) {}
  
  public m[] a(c[] paramArrayOfc, v paramv)
  {
    if (paramArrayOfc.length != this.a) {
      throw new IllegalArgumentException(String.format("Strategy adapter '%s' got %d cars while team size is %d.", new Object[] { getClass().getSimpleName(), Integer.valueOf(paramArrayOfc.length), Integer.valueOf(this.a) }));
    }
    return new m[this.a];
  }
  
  public void close() {}
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\a\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */