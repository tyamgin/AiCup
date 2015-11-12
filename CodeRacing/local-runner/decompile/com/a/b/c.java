package com.a.b;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class c
  implements Runnable
{
  private static final Logger a = LoggerFactory.getLogger(c.class);
  private final String[] b;
  
  public c(String[] paramArrayOfString)
  {
    this.b = paramArrayOfString;
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: new 8	com/a/b/d
    //   3: dup
    //   4: aload_0
    //   5: invokespecial 34	com/a/b/d:<init>	(Lcom/a/b/c;)V
    //   8: invokestatic 45	java/lang/Thread:setDefaultUncaughtExceptionHandler	(Ljava/lang/Thread$UncaughtExceptionHandler;)V
    //   11: aconst_null
    //   12: astore_1
    //   13: new 6	com/a/b/a/a/a/b
    //   16: dup
    //   17: aload_0
    //   18: getfield 28	com/a/b/c:b	[Ljava/lang/String;
    //   21: invokespecial 31	com/a/b/a/a/a/b:<init>	([Ljava/lang/String;)V
    //   24: astore_1
    //   25: aload_1
    //   26: invokestatic 32	com/a/b/a/a/a/b:a	(Lcom/a/b/a/a/a/b;)V
    //   29: iconst_1
    //   30: anewarray 11	com/google/inject/Module
    //   33: dup
    //   34: iconst_0
    //   35: new 5	com/a/b/a/a/a/a
    //   38: dup
    //   39: invokespecial 30	com/a/b/a/a/a/a:<init>	()V
    //   42: aastore
    //   43: invokestatic 35	com/google/inject/Guice:createInjector	([Lcom/google/inject/Module;)Lcom/google/inject/Injector;
    //   46: ldc 4
    //   48: invokeinterface 52 2 0
    //   53: checkcast 4	com/a/b/a
    //   56: astore_2
    //   57: aload_2
    //   58: aload_1
    //   59: invokeinterface 50 2 0
    //   64: aload_2
    //   65: invokeinterface 49 1 0
    //   70: aload_2
    //   71: invokeinterface 51 1 0
    //   76: goto +12 -> 88
    //   79: astore_3
    //   80: aload_2
    //   81: invokeinterface 51 1 0
    //   86: aload_3
    //   87: athrow
    //   88: goto +106 -> 194
    //   91: astore_2
    //   92: getstatic 27	com/a/b/c:a	Lorg/slf4j/Logger;
    //   95: ldc 3
    //   97: aload_2
    //   98: invokeinterface 53 3 0
    //   103: aload_2
    //   104: invokevirtual 38	java/lang/RuntimeException:printStackTrace	()V
    //   107: aload_1
    //   108: ifnonnull +4 -> 112
    //   111: return
    //   112: aload_1
    //   113: invokevirtual 33	com/a/b/a/a/a/b:j	()Ljava/io/File;
    //   116: astore_3
    //   117: aload_3
    //   118: ifnonnull +4 -> 122
    //   121: return
    //   122: new 18	java/lang/StringBuilder
    //   125: dup
    //   126: invokespecial 41	java/lang/StringBuilder:<init>	()V
    //   129: ldc 2
    //   131: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: aload_2
    //   135: invokestatic 47	org/apache/commons/lang3/exception/ExceptionUtils:getStackTrace	(Ljava/lang/Throwable;)Ljava/lang/String;
    //   138: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: bipush 10
    //   143: invokevirtual 42	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   146: invokevirtual 44	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: astore 4
    //   151: aload_3
    //   152: aload 4
    //   154: getstatic 29	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   157: invokevirtual 40	java/lang/String:getBytes	(Ljava/nio/charset/Charset;)[B
    //   160: invokestatic 46	org/apache/commons/io/FileUtils:writeByteArrayToFile	(Ljava/io/File;[B)V
    //   163: goto +31 -> 194
    //   166: astore 4
    //   168: getstatic 27	com/a/b/c:a	Lorg/slf4j/Logger;
    //   171: ldc 1
    //   173: iconst_1
    //   174: anewarray 14	java/lang/Object
    //   177: dup
    //   178: iconst_0
    //   179: aload_3
    //   180: invokevirtual 36	java/io/File:getPath	()Ljava/lang/String;
    //   183: aastore
    //   184: invokestatic 39	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   187: aload 4
    //   189: invokeinterface 53 3 0
    //   194: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	195	0	this	c
    //   12	101	1	localb	com.a.b.a.a.a.b
    //   56	25	2	locala	a
    //   91	44	2	localRuntimeException	RuntimeException
    //   79	8	3	localObject	Object
    //   116	64	3	localFile	java.io.File
    //   149	4	4	str	String
    //   166	22	4	localIOException	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   57	70	79	finally
    //   13	88	91	java/lang/RuntimeException
    //   122	163	166	java/io/IOException
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\a\b\c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */