package com.google.common.io;

import com.google.common.base.Preconditions;
import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;

public abstract class ByteSource
{
  private static final int BUF_SIZE = 4096;
  private static final byte[] countBuffer = new byte['က'];
  
  public CharSource asCharSource(Charset paramCharset)
  {
    return new AsCharSource(paramCharset, null);
  }
  
  public abstract InputStream openStream()
    throws IOException;
  
  public BufferedInputStream openBufferedStream()
    throws IOException
  {
    InputStream localInputStream = openStream();
    return (localInputStream instanceof BufferedInputStream) ? (BufferedInputStream)localInputStream : new BufferedInputStream(localInputStream);
  }
  
  public ByteSource slice(long paramLong1, long paramLong2)
  {
    return new SlicedByteSource(paramLong1, paramLong2, null);
  }
  
  public long size()
    throws IOException
  {
    Closer localCloser = Closer.create();
    long l;
    try
    {
      InputStream localInputStream1 = (InputStream)localCloser.register(openStream());
      l = countBySkipping(localInputStream1);
      return l;
    }
    catch (IOException localIOException) {}finally
    {
      localCloser.close();
    }
    localCloser = Closer.create();
    try
    {
      InputStream localInputStream2 = (InputStream)localCloser.register(openStream());
      l = countByReading(localInputStream2);
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localCloser.rethrow(localThrowable);
    }
    finally
    {
      localCloser.close();
    }
  }
  
  private long countBySkipping(InputStream paramInputStream)
    throws IOException
  {
    long l1 = 0L;
    for (;;)
    {
      long l2 = paramInputStream.skip(Math.min(paramInputStream.available(), Integer.MAX_VALUE));
      if (l2 <= 0L)
      {
        if (paramInputStream.read() == -1) {
          return l1;
        }
        l1 += 1L;
      }
      else
      {
        l1 += l2;
      }
    }
  }
  
  private long countByReading(InputStream paramInputStream)
    throws IOException
  {
    long l2;
    for (long l1 = 0L; (l2 = paramInputStream.read(countBuffer)) != -1L; l1 += l2) {}
    return l1;
  }
  
  public long copyTo(OutputStream paramOutputStream)
    throws IOException
  {
    Preconditions.checkNotNull(paramOutputStream);
    Closer localCloser = Closer.create();
    try
    {
      InputStream localInputStream = (InputStream)localCloser.register(openStream());
      long l = ByteStreams.copy(localInputStream, paramOutputStream);
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localCloser.rethrow(localThrowable);
    }
    finally
    {
      localCloser.close();
    }
  }
  
  public long copyTo(ByteSink paramByteSink)
    throws IOException
  {
    Preconditions.checkNotNull(paramByteSink);
    Closer localCloser = Closer.create();
    try
    {
      InputStream localInputStream = (InputStream)localCloser.register(openStream());
      OutputStream localOutputStream = (OutputStream)localCloser.register(paramByteSink.openStream());
      long l = ByteStreams.copy(localInputStream, localOutputStream);
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localCloser.rethrow(localThrowable);
    }
    finally
    {
      localCloser.close();
    }
  }
  
  public byte[] read()
    throws IOException
  {
    Closer localCloser = Closer.create();
    try
    {
      InputStream localInputStream = (InputStream)localCloser.register(openStream());
      byte[] arrayOfByte = ByteStreams.toByteArray(localInputStream);
      return arrayOfByte;
    }
    catch (Throwable localThrowable)
    {
      throw localCloser.rethrow(localThrowable);
    }
    finally
    {
      localCloser.close();
    }
  }
  
  public HashCode hash(HashFunction paramHashFunction)
    throws IOException
  {
    Hasher localHasher = paramHashFunction.newHasher();
    copyTo(Funnels.asOutputStream(localHasher));
    return localHasher.hash();
  }
  
  /* Error */
  public boolean contentEquals(ByteSource paramByteSource)
    throws IOException
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 26	com/google/common/base/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4: pop
    //   5: sipush 4096
    //   8: newarray <illegal type>
    //   10: astore_2
    //   11: sipush 4096
    //   14: newarray <illegal type>
    //   16: astore_3
    //   17: invokestatic 39	com/google/common/io/Closer:create	()Lcom/google/common/io/Closer;
    //   20: astore 4
    //   22: aload 4
    //   24: aload_0
    //   25: invokevirtual 32	com/google/common/io/ByteSource:openStream	()Ljava/io/InputStream;
    //   28: invokevirtual 40	com/google/common/io/Closer:register	(Ljava/io/Closeable;)Ljava/io/Closeable;
    //   31: checkcast 17	java/io/InputStream
    //   34: astore 5
    //   36: aload 4
    //   38: aload_1
    //   39: invokevirtual 32	com/google/common/io/ByteSource:openStream	()Ljava/io/InputStream;
    //   42: invokevirtual 40	com/google/common/io/Closer:register	(Ljava/io/Closeable;)Ljava/io/Closeable;
    //   45: checkcast 17	java/io/InputStream
    //   48: astore 6
    //   50: aload 5
    //   52: aload_2
    //   53: iconst_0
    //   54: sipush 4096
    //   57: invokestatic 36	com/google/common/io/ByteStreams:read	(Ljava/io/InputStream;[BII)I
    //   60: istore 7
    //   62: aload 6
    //   64: aload_3
    //   65: iconst_0
    //   66: sipush 4096
    //   69: invokestatic 36	com/google/common/io/ByteStreams:read	(Ljava/io/InputStream;[BII)I
    //   72: istore 8
    //   74: iload 7
    //   76: iload 8
    //   78: if_icmpne +11 -> 89
    //   81: aload_2
    //   82: aload_3
    //   83: invokestatic 49	java/util/Arrays:equals	([B[B)Z
    //   86: ifne +14 -> 100
    //   89: iconst_0
    //   90: istore 9
    //   92: aload 4
    //   94: invokevirtual 38	com/google/common/io/Closer:close	()V
    //   97: iload 9
    //   99: ireturn
    //   100: iload 7
    //   102: sipush 4096
    //   105: if_icmpeq +14 -> 119
    //   108: iconst_1
    //   109: istore 9
    //   111: aload 4
    //   113: invokevirtual 38	com/google/common/io/Closer:close	()V
    //   116: iload 9
    //   118: ireturn
    //   119: goto -69 -> 50
    //   122: astore 5
    //   124: aload 4
    //   126: aload 5
    //   128: invokevirtual 41	com/google/common/io/Closer:rethrow	(Ljava/lang/Throwable;)Ljava/lang/RuntimeException;
    //   131: athrow
    //   132: astore 10
    //   134: aload 4
    //   136: invokevirtual 38	com/google/common/io/Closer:close	()V
    //   139: aload 10
    //   141: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	142	0	this	ByteSource
    //   0	142	1	paramByteSource	ByteSource
    //   10	72	2	arrayOfByte1	byte[]
    //   16	67	3	arrayOfByte2	byte[]
    //   20	115	4	localCloser	Closer
    //   34	17	5	localInputStream1	InputStream
    //   122	5	5	localThrowable	Throwable
    //   48	15	6	localInputStream2	InputStream
    //   60	46	7	i	int
    //   72	7	8	j	int
    //   90	27	9	bool	boolean
    //   132	8	10	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   22	92	122	java/lang/Throwable
    //   100	111	122	java/lang/Throwable
    //   119	122	122	java/lang/Throwable
    //   22	92	132	finally
    //   100	111	132	finally
    //   119	134	132	finally
  }
  
  private final class SlicedByteSource
    extends ByteSource
  {
    private final long offset;
    private final long length;
    
    private SlicedByteSource(long paramLong1, long paramLong2)
    {
      Preconditions.checkArgument(paramLong1 >= 0L, "offset (%s) may not be negative", new Object[] { Long.valueOf(paramLong1) });
      Preconditions.checkArgument(paramLong2 >= 0L, "length (%s) may not be negative", new Object[] { Long.valueOf(paramLong2) });
      this.offset = paramLong1;
      this.length = paramLong2;
    }
    
    public InputStream openStream()
      throws IOException
    {
      InputStream localInputStream = ByteSource.this.openStream();
      if (this.offset > 0L) {
        try
        {
          ByteStreams.skipFully(localInputStream, this.offset);
        }
        catch (Throwable localThrowable)
        {
          Closer localCloser = Closer.create();
          localCloser.register(localInputStream);
          try
          {
            throw localCloser.rethrow(localThrowable);
          }
          finally
          {
            localCloser.close();
          }
        }
      }
      return ByteStreams.limit(localInputStream, this.length);
    }
    
    public ByteSource slice(long paramLong1, long paramLong2)
    {
      Preconditions.checkArgument(paramLong1 >= 0L, "offset (%s) may not be negative", new Object[] { Long.valueOf(paramLong1) });
      Preconditions.checkArgument(paramLong2 >= 0L, "length (%s) may not be negative", new Object[] { Long.valueOf(paramLong2) });
      long l = this.length - paramLong1;
      return ByteSource.this.slice(this.offset + paramLong1, Math.min(paramLong2, l));
    }
    
    public String toString()
    {
      return ByteSource.this.toString() + ".slice(" + this.offset + ", " + this.length + ")";
    }
  }
  
  private final class AsCharSource
    extends CharSource
  {
    private final Charset charset;
    
    private AsCharSource(Charset paramCharset)
    {
      this.charset = ((Charset)Preconditions.checkNotNull(paramCharset));
    }
    
    public Reader openStream()
      throws IOException
    {
      return new InputStreamReader(ByteSource.this.openStream(), this.charset);
    }
    
    public String toString()
    {
      return ByteSource.this.toString() + ".asCharSource(" + this.charset + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\ByteSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */