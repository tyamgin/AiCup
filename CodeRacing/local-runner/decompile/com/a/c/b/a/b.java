package com.a.c.b.a;

import com.a.a.b.g;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class b
  implements com.a.c.e
{
  private final Lock a = new ReentrantLock();
  private final Map b = new HashMap();
  private final Map c = new HashMap();
  private final Map d = new HashMap();
  private final g e;
  
  public b()
  {
    this.e = new g();
  }
  
  public b(int paramInt1, int paramInt2, double paramDouble, com.a.a.b.a.a parama)
  {
    this.e = new g(paramInt1, paramInt2, paramDouble, parama);
  }
  
  public com.a.c.c a(com.a.c.c paramc)
  {
    this.a.lock();
    try
    {
      com.a.a.b.a locala = c(paramc.a());
      if (locala == null)
      {
        locala = e.a(paramc);
        this.e.a(locala);
        this.b.put(Long.valueOf(locala.a()), locala);
        this.c.put(Long.valueOf(paramc.a()), Long.valueOf(locala.a()));
        this.d.put(Long.valueOf(locala.a()), Long.valueOf(paramc.a()));
        e.a(locala, paramc, this);
      }
      com.a.c.c localc = e(paramc.a());
      return localc;
    }
    finally
    {
      this.a.unlock();
    }
  }
  
  /* Error */
  public void b(com.a.c.c paramc)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 23	com/a/c/b/a/b:a	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 62 1 0
    //   9: aload_0
    //   10: aload_1
    //   11: invokevirtual 47	com/a/c/c:a	()J
    //   14: invokevirtual 39	com/a/c/b/a/b:c	(J)Lcom/a/a/b/a;
    //   17: astore_2
    //   18: aload_2
    //   19: ifnonnull +38 -> 57
    //   22: new 13	java/lang/IllegalArgumentException
    //   25: dup
    //   26: new 17	java/lang/StringBuilder
    //   29: dup
    //   30: invokespecial 52	java/lang/StringBuilder:<init>	()V
    //   33: ldc 1
    //   35: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: aload_1
    //   39: invokevirtual 47	com/a/c/c:a	()J
    //   42: invokevirtual 54	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   45: bipush 46
    //   47: invokevirtual 53	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   50: invokevirtual 56	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   53: invokespecial 48	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   56: athrow
    //   57: aload_2
    //   58: aload_1
    //   59: aload_0
    //   60: invokestatic 45	com/a/c/b/a/e:a	(Lcom/a/a/b/a;Lcom/a/c/c;Lcom/a/c/b/a/b;)V
    //   63: aload_0
    //   64: getfield 23	com/a/c/b/a/b:a	Ljava/util/concurrent/locks/Lock;
    //   67: invokeinterface 63 1 0
    //   72: goto +15 -> 87
    //   75: astore_3
    //   76: aload_0
    //   77: getfield 23	com/a/c/b/a/b:a	Ljava/util/concurrent/locks/Lock;
    //   80: invokeinterface 63 1 0
    //   85: aload_3
    //   86: athrow
    //   87: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	b
    //   0	88	1	paramc	com.a.c.c
    //   17	41	2	locala	com.a.a.b.a
    //   75	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	63	75	finally
  }
  
  /* Error */
  public void c(com.a.c.c paramc)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 23	com/a/c/b/a/b:a	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 62 1 0
    //   9: aload_0
    //   10: aload_1
    //   11: invokevirtual 47	com/a/c/c:a	()J
    //   14: invokevirtual 39	com/a/c/b/a/b:c	(J)Lcom/a/a/b/a;
    //   17: astore_2
    //   18: aload_2
    //   19: ifnull +9 -> 28
    //   22: aload_2
    //   23: aload_1
    //   24: aload_0
    //   25: invokestatic 45	com/a/c/b/a/e:a	(Lcom/a/a/b/a;Lcom/a/c/c;Lcom/a/c/b/a/b;)V
    //   28: aload_0
    //   29: getfield 23	com/a/c/b/a/b:a	Ljava/util/concurrent/locks/Lock;
    //   32: invokeinterface 63 1 0
    //   37: goto +15 -> 52
    //   40: astore_3
    //   41: aload_0
    //   42: getfield 23	com/a/c/b/a/b:a	Ljava/util/concurrent/locks/Lock;
    //   45: invokeinterface 63 1 0
    //   50: aload_3
    //   51: athrow
    //   52: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	53	0	this	b
    //   0	53	1	paramc	com.a.c.c
    //   17	6	2	locala	com.a.a.b.a
    //   40	11	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	28	40	finally
  }
  
  public com.a.c.c d(com.a.c.c paramc)
  {
    this.a.lock();
    try
    {
      Long localLong = a(paramc.a());
      if (localLong == null)
      {
        localObject1 = null;
        return (com.a.c.c)localObject1;
      }
      Object localObject1 = (com.a.a.b.a)this.b.remove(localLong);
      if (localObject1 == null)
      {
        localc1 = null;
        return localc1;
      }
      com.a.c.c localc1 = e.a((com.a.a.b.a)localObject1, this);
      this.e.b((com.a.a.b.a)localObject1);
      this.c.remove(Long.valueOf(paramc.a()));
      this.d.remove(localLong);
      com.a.c.c localc2 = localc1;
      return localc2;
    }
    finally
    {
      this.a.unlock();
    }
  }
  
  /* Error */
  public void a()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 23	com/a/c/b/a/b:a	Ljava/util/concurrent/locks/Lock;
    //   4: invokeinterface 62 1 0
    //   9: aload_0
    //   10: getfield 27	com/a/c/b/a/b:e	Lcom/a/a/b/g;
    //   13: invokevirtual 37	com/a/a/b/g:d	()V
    //   16: aload_0
    //   17: getfield 23	com/a/c/b/a/b:a	Ljava/util/concurrent/locks/Lock;
    //   20: invokeinterface 63 1 0
    //   25: goto +15 -> 40
    //   28: astore_1
    //   29: aload_0
    //   30: getfield 23	com/a/c/b/a/b:a	Ljava/util/concurrent/locks/Lock;
    //   33: invokeinterface 63 1 0
    //   38: aload_1
    //   39: athrow
    //   40: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	41	0	this	b
    //   28	11	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   9	16	28	finally
  }
  
  public void a(com.a.c.b paramb)
  {
    this.a.lock();
    try
    {
      String str;
      do
      {
        str = com.a.a.a.a.c.e();
      } while (this.e.a(str));
      if ((paramb instanceof com.a.c.d))
      {
        com.a.c.d locald = (com.a.c.d)paramb;
        this.e.a(new c(this, locald), str);
      }
      else
      {
        this.e.a(new d(this, paramb), str);
      }
    }
    finally
    {
      this.a.unlock();
    }
  }
  
  public int b()
  {
    return this.e.a();
  }
  
  g c()
  {
    return this.e;
  }
  
  Long a(long paramLong)
  {
    return (Long)this.c.get(Long.valueOf(paramLong));
  }
  
  Long b(long paramLong)
  {
    return (Long)this.d.get(Long.valueOf(paramLong));
  }
  
  com.a.a.b.a c(long paramLong)
  {
    Long localLong = a(paramLong);
    return localLong == null ? null : (com.a.a.b.a)this.b.get(localLong);
  }
  
  com.a.c.c d(long paramLong)
  {
    com.a.a.b.a locala = (com.a.a.b.a)this.b.get(Long.valueOf(paramLong));
    return locala == null ? null : e.a(locala, this);
  }
  
  com.a.c.c e(long paramLong)
  {
    Long localLong = a(paramLong);
    return localLong == null ? null : d(localLong.longValue());
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\c\b\a\b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */