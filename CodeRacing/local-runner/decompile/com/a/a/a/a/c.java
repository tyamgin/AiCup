package com.a.a.a.a;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class c
{
  private static final Logger a = LoggerFactory.getLogger(c.class);
  private static final Lock b = new ReentrantLock();
  private static Random c = new Random(a());
  
  private c()
  {
    throw new UnsupportedOperationException();
  }
  
  private static SecureRandom f()
  {
    try
    {
      return SecureRandom.getInstance("SHA1PRNG");
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      a.error(String.format("Can't create 'SHA1PRNG' instance of %s. Switching to use a default instance.", new Object[] { SecureRandom.class.getSimpleName() }), localNoSuchAlgorithmException);
    }
    return new SecureRandom();
  }
  
  public static long a()
  {
    return System.nanoTime() ^ Thread.currentThread().getId() + Runtime.getRuntime().maxMemory() * Runtime.getRuntime().freeMemory() & Runtime.getRuntime().totalMemory();
  }
  
  /* Error */
  public static void a(boolean paramBoolean, long paramLong)
  {
    // Byte code:
    //   0: getstatic 27	com/a/a/a/a/c:b	Ljava/util/concurrent/locks/Lock;
    //   3: invokeinterface 59 1 0
    //   8: iload_0
    //   9: ifeq +9 -> 18
    //   12: invokestatic 32	com/a/a/a/a/c:f	()Ljava/security/SecureRandom;
    //   15: goto +10 -> 25
    //   18: new 19	java/util/Random
    //   21: dup
    //   22: invokespecial 48	java/util/Random:<init>	()V
    //   25: putstatic 28	com/a/a/a/a/c:c	Ljava/util/Random;
    //   28: getstatic 28	com/a/a/a/a/c:c	Ljava/util/Random;
    //   31: lload_1
    //   32: invokevirtual 55	java/util/Random:setSeed	(J)V
    //   35: getstatic 27	com/a/a/a/a/c:b	Ljava/util/concurrent/locks/Lock;
    //   38: invokeinterface 60 1 0
    //   43: goto +14 -> 57
    //   46: astore_3
    //   47: getstatic 27	com/a/a/a/a/c:b	Ljava/util/concurrent/locks/Lock;
    //   50: invokeinterface 60 1 0
    //   55: aload_3
    //   56: athrow
    //   57: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	58	0	paramBoolean	boolean
    //   0	58	1	paramLong	long
    //   46	10	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	35	46	finally
  }
  
  public static int a(int paramInt1, int paramInt2)
  {
    b.lock();
    try
    {
      int i = paramInt1 + c.nextInt(paramInt2 - paramInt1 + 1);
      return i;
    }
    finally
    {
      b.unlock();
    }
  }
  
  public static long b()
  {
    b.lock();
    try
    {
      long l = c.nextLong();
      return l;
    }
    finally
    {
      b.unlock();
    }
  }
  
  public static boolean c()
  {
    b.lock();
    try
    {
      boolean bool = c.nextBoolean();
      return bool;
    }
    finally
    {
      b.unlock();
    }
  }
  
  public static double d()
  {
    b.lock();
    try
    {
      double d = c.nextDouble();
      return d;
    }
    finally
    {
      b.unlock();
    }
  }
  
  public static String e()
  {
    return Hex.encodeHexString(a(16));
  }
  
  /* Error */
  public static byte[] a(int paramInt)
  {
    // Byte code:
    //   0: iload_0
    //   1: ifge +13 -> 14
    //   4: new 7	java/lang/IllegalArgumentException
    //   7: dup
    //   8: ldc 1
    //   10: invokespecial 34	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   13: athrow
    //   14: iload_0
    //   15: ifne +7 -> 22
    //   18: getstatic 29	org/apache/commons/lang3/ArrayUtils:EMPTY_BYTE_ARRAY	[B
    //   21: areturn
    //   22: iload_0
    //   23: newarray <illegal type>
    //   25: astore_1
    //   26: getstatic 27	com/a/a/a/a/c:b	Ljava/util/concurrent/locks/Lock;
    //   29: invokeinterface 59 1 0
    //   34: getstatic 28	com/a/a/a/a/c:c	Ljava/util/Random;
    //   37: aload_1
    //   38: invokevirtual 51	java/util/Random:nextBytes	([B)V
    //   41: getstatic 27	com/a/a/a/a/c:b	Ljava/util/concurrent/locks/Lock;
    //   44: invokeinterface 60 1 0
    //   49: goto +14 -> 63
    //   52: astore_2
    //   53: getstatic 27	com/a/a/a/a/c:b	Ljava/util/concurrent/locks/Lock;
    //   56: invokeinterface 60 1 0
    //   61: aload_2
    //   62: athrow
    //   63: aload_1
    //   64: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	paramInt	int
    //   25	39	1	arrayOfByte	byte[]
    //   52	10	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   34	41	52	finally
  }
  
  /* Error */
  public static void a(java.util.List paramList)
  {
    // Byte code:
    //   0: getstatic 27	com/a/a/a/a/c:b	Ljava/util/concurrent/locks/Lock;
    //   3: invokeinterface 59 1 0
    //   8: aload_0
    //   9: getstatic 28	com/a/a/a/a/c:c	Ljava/util/Random;
    //   12: invokestatic 47	java/util/Collections:shuffle	(Ljava/util/List;Ljava/util/Random;)V
    //   15: getstatic 27	com/a/a/a/a/c:b	Ljava/util/concurrent/locks/Lock;
    //   18: invokeinterface 60 1 0
    //   23: goto +14 -> 37
    //   26: astore_1
    //   27: getstatic 27	com/a/a/a/a/c:b	Ljava/util/concurrent/locks/Lock;
    //   30: invokeinterface 60 1 0
    //   35: aload_1
    //   36: athrow
    //   37: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	38	0	paramList	java.util.List
    //   26	10	1	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   8	15	26	finally
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\a\a\a\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */