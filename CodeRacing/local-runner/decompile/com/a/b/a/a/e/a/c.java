package com.a.b.a.a.e.a;

import com.a.b.a.a.c.l;
import com.a.b.a.a.c.m;
import com.a.b.a.a.c.v;
import java.util.concurrent.atomic.AtomicLong;

public class c
  implements f
{
  private final int a;
  private final a b;
  
  public c(int paramInt, a parama)
  {
    this.a = paramInt;
    this.b = parama;
  }
  
  public int a()
  {
    return 1;
  }
  
  public void a(l paraml) {}
  
  public m[] a(com.a.b.a.a.c.c[] paramArrayOfc, v paramv)
  {
    if (paramArrayOfc.length != this.a) {
      throw new IllegalArgumentException(String.format("Strategy adapter '%s' got %d cars while team size is %d.", new Object[] { getClass().getSimpleName(), Integer.valueOf(paramArrayOfc.length), Integer.valueOf(this.a) }));
    }
    m[] arrayOfm = new m[this.a];
    for (int i = 0; i < this.a; i++)
    {
      com.a.b.a.a.c.c localc = paramArrayOfc[i];
      m localm = new m();
      a(localc, localm);
      arrayOfm[i] = localm;
    }
    return arrayOfm;
  }
  
  public void close() {}
  
  private void a(com.a.b.a.a.c.c paramc, m paramm)
  {
    if (this.b.c(paramc.getTeammateIndex())) {
      paramm.setBrake(true);
    }
    if (this.b.a(paramc.getTeammateIndex())) {
      paramm.setEnginePower(1.0D);
    } else if (this.b.b(paramc.getTeammateIndex())) {
      paramm.setEnginePower(-1.0D);
    }
    if (this.b.d(paramc.getTeammateIndex())) {
      paramm.setWheelTurn(-1.0D);
    } else if (this.b.e(paramc.getTeammateIndex())) {
      paramm.setWheelTurn(1.0D);
    }
    if (this.b.f(paramc.getTeammateIndex())) {
      paramm.setThrowProjectile(true);
    }
    if (this.b.g(paramc.getTeammateIndex())) {
      paramm.setUseNitro(true);
    }
    if (this.b.h(paramc.getTeammateIndex())) {
      paramm.setSpillOil(true);
    }
  }
  
  public static final class a
  {
    private final AtomicLong a = new AtomicLong();
    
    public long a()
    {
      return this.a.get();
    }
    
    public boolean a(int paramInt)
    {
      return a(paramInt, 0);
    }
    
    public void a(int paramInt, boolean paramBoolean)
    {
      a(paramInt, 0, paramBoolean);
    }
    
    public boolean b(int paramInt)
    {
      return a(paramInt, 1);
    }
    
    public void b(int paramInt, boolean paramBoolean)
    {
      a(paramInt, 1, paramBoolean);
    }
    
    public boolean c(int paramInt)
    {
      return a(paramInt, 2);
    }
    
    public void c(int paramInt, boolean paramBoolean)
    {
      a(paramInt, 2, paramBoolean);
    }
    
    public boolean d(int paramInt)
    {
      return a(paramInt, 3);
    }
    
    public void d(int paramInt, boolean paramBoolean)
    {
      a(paramInt, 3, paramBoolean);
    }
    
    public boolean e(int paramInt)
    {
      return a(paramInt, 4);
    }
    
    public void e(int paramInt, boolean paramBoolean)
    {
      a(paramInt, 4, paramBoolean);
    }
    
    public boolean f(int paramInt)
    {
      return a(paramInt, 5);
    }
    
    public void f(int paramInt, boolean paramBoolean)
    {
      a(paramInt, 5, paramBoolean);
    }
    
    public boolean g(int paramInt)
    {
      return a(paramInt, 6);
    }
    
    public void g(int paramInt, boolean paramBoolean)
    {
      a(paramInt, 6, paramBoolean);
    }
    
    public boolean h(int paramInt)
    {
      return a(paramInt, 7);
    }
    
    public void h(int paramInt, boolean paramBoolean)
    {
      a(paramInt, 7, paramBoolean);
    }
    
    private boolean a(int paramInt1, int paramInt2)
    {
      j(paramInt1);
      return i(paramInt2 + paramInt1 * 8);
    }
    
    private void a(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      j(paramInt1);
      i(paramInt2 + paramInt1 * 8, paramBoolean);
    }
    
    private boolean i(int paramInt)
    {
      return (a() & 1L << paramInt) != 0L;
    }
    
    private void i(int paramInt, boolean paramBoolean)
    {
      for (;;)
      {
        long l1 = a();
        long l2 = paramBoolean ? l1 | 1L << paramInt : l1 & (1L << paramInt ^ 0xFFFFFFFFFFFFFFFF);
        if (this.a.compareAndSet(l1, l2)) {
          break;
        }
      }
    }
    
    private static void j(int paramInt)
    {
      if ((paramInt < 0) || (paramInt > 8)) {
        throw new IllegalArgumentException("Unsupported teammate index: " + paramInt + '.');
      }
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\a\a\e\a\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */