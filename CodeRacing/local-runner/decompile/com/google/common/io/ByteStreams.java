package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;
import java.util.zip.Checksum;

@Beta
public final class ByteStreams
{
  private static final int BUF_SIZE = 4096;
  private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream()
  {
    public void write(int paramAnonymousInt) {}
    
    public void write(byte[] paramAnonymousArrayOfByte)
    {
      Preconditions.checkNotNull(paramAnonymousArrayOfByte);
    }
    
    public void write(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      Preconditions.checkNotNull(paramAnonymousArrayOfByte);
    }
    
    public String toString()
    {
      return "ByteStreams.nullOutputStream()";
    }
  };
  
  public static InputSupplier newInputStreamSupplier(byte[] paramArrayOfByte)
  {
    return asInputSupplier(asByteSource(paramArrayOfByte));
  }
  
  public static InputSupplier newInputStreamSupplier(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return asInputSupplier(asByteSource(paramArrayOfByte).slice(paramInt1, paramInt2));
  }
  
  public static ByteSource asByteSource(byte[] paramArrayOfByte)
  {
    return new ByteArrayByteSource(paramArrayOfByte, null);
  }
  
  public static void write(byte[] paramArrayOfByte, OutputSupplier paramOutputSupplier)
    throws IOException
  {
    asByteSink(paramOutputSupplier).write(paramArrayOfByte);
  }
  
  public static long copy(InputSupplier paramInputSupplier, OutputSupplier paramOutputSupplier)
    throws IOException
  {
    return asByteSource(paramInputSupplier).copyTo(asByteSink(paramOutputSupplier));
  }
  
  public static long copy(InputSupplier paramInputSupplier, OutputStream paramOutputStream)
    throws IOException
  {
    return asByteSource(paramInputSupplier).copyTo(paramOutputStream);
  }
  
  public static long copy(InputStream paramInputStream, OutputSupplier paramOutputSupplier)
    throws IOException
  {
    return asByteSink(paramOutputSupplier).writeFrom(paramInputStream);
  }
  
  public static long copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException
  {
    Preconditions.checkNotNull(paramInputStream);
    Preconditions.checkNotNull(paramOutputStream);
    byte[] arrayOfByte = new byte['က'];
    int i;
    for (long l = 0L;; l += i)
    {
      i = paramInputStream.read(arrayOfByte);
      if (i == -1) {
        break;
      }
      paramOutputStream.write(arrayOfByte, 0, i);
    }
    return l;
  }
  
  public static long copy(ReadableByteChannel paramReadableByteChannel, WritableByteChannel paramWritableByteChannel)
    throws IOException
  {
    Preconditions.checkNotNull(paramReadableByteChannel);
    Preconditions.checkNotNull(paramWritableByteChannel);
    ByteBuffer localByteBuffer = ByteBuffer.allocate(4096);
    long l = 0L;
    while (paramReadableByteChannel.read(localByteBuffer) != -1)
    {
      localByteBuffer.flip();
      while (localByteBuffer.hasRemaining()) {
        l += paramWritableByteChannel.write(localByteBuffer);
      }
      localByteBuffer.clear();
    }
    return l;
  }
  
  public static byte[] toByteArray(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    copy(paramInputStream, localByteArrayOutputStream);
    return localByteArrayOutputStream.toByteArray();
  }
  
  public static byte[] toByteArray(InputSupplier paramInputSupplier)
    throws IOException
  {
    return asByteSource(paramInputSupplier).read();
  }
  
  public static ByteArrayDataInput newDataInput(byte[] paramArrayOfByte)
  {
    return new ByteArrayDataInputStream(paramArrayOfByte);
  }
  
  public static ByteArrayDataInput newDataInput(byte[] paramArrayOfByte, int paramInt)
  {
    Preconditions.checkPositionIndex(paramInt, paramArrayOfByte.length);
    return new ByteArrayDataInputStream(paramArrayOfByte, paramInt);
  }
  
  public static ByteArrayDataOutput newDataOutput()
  {
    return new ByteArrayDataOutputStream();
  }
  
  public static ByteArrayDataOutput newDataOutput(int paramInt)
  {
    Preconditions.checkArgument(paramInt >= 0, "Invalid size: %s", new Object[] { Integer.valueOf(paramInt) });
    return new ByteArrayDataOutputStream(paramInt);
  }
  
  public static OutputStream nullOutputStream()
  {
    return NULL_OUTPUT_STREAM;
  }
  
  public static InputStream limit(InputStream paramInputStream, long paramLong)
  {
    return new LimitedInputStream(paramInputStream, paramLong);
  }
  
  public static long length(InputSupplier paramInputSupplier)
    throws IOException
  {
    return asByteSource(paramInputSupplier).size();
  }
  
  public static boolean equal(InputSupplier paramInputSupplier1, InputSupplier paramInputSupplier2)
    throws IOException
  {
    return asByteSource(paramInputSupplier1).contentEquals(asByteSource(paramInputSupplier2));
  }
  
  public static void readFully(InputStream paramInputStream, byte[] paramArrayOfByte)
    throws IOException
  {
    readFully(paramInputStream, paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public static void readFully(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = read(paramInputStream, paramArrayOfByte, paramInt1, paramInt2);
    if (i != paramInt2) {
      throw new EOFException("reached end of stream after reading " + i + " bytes; " + paramInt2 + " bytes expected");
    }
  }
  
  public static void skipFully(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    long l1 = paramLong;
    while (paramLong > 0L)
    {
      long l2 = paramInputStream.skip(paramLong);
      if (l2 == 0L)
      {
        if (paramInputStream.read() == -1)
        {
          long l3 = l1 - paramLong;
          throw new EOFException("reached end of stream after skipping " + l3 + " bytes; " + l1 + " bytes expected");
        }
        paramLong -= 1L;
      }
      else
      {
        paramLong -= l2;
      }
    }
  }
  
  public static Object readBytes(InputSupplier paramInputSupplier, ByteProcessor paramByteProcessor)
    throws IOException
  {
    Preconditions.checkNotNull(paramInputSupplier);
    Preconditions.checkNotNull(paramByteProcessor);
    Closer localCloser = Closer.create();
    try
    {
      InputStream localInputStream = (InputStream)localCloser.register((Closeable)paramInputSupplier.getInput());
      Object localObject1 = readBytes(localInputStream, paramByteProcessor);
      return localObject1;
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
  
  public static Object readBytes(InputStream paramInputStream, ByteProcessor paramByteProcessor)
    throws IOException
  {
    Preconditions.checkNotNull(paramInputStream);
    Preconditions.checkNotNull(paramByteProcessor);
    byte[] arrayOfByte = new byte['က'];
    int i;
    do
    {
      i = paramInputStream.read(arrayOfByte);
    } while ((i != -1) && (paramByteProcessor.processBytes(arrayOfByte, 0, i)));
    return paramByteProcessor.getResult();
  }
  
  @Deprecated
  public static long getChecksum(InputSupplier paramInputSupplier, Checksum paramChecksum)
    throws IOException
  {
    Preconditions.checkNotNull(paramChecksum);
    ((Long)readBytes(paramInputSupplier, new ByteProcessor()
    {
      public boolean processBytes(byte[] paramAnonymousArrayOfByte, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        this.val$checksum.update(paramAnonymousArrayOfByte, paramAnonymousInt1, paramAnonymousInt2);
        return true;
      }
      
      public Long getResult()
      {
        long l = this.val$checksum.getValue();
        this.val$checksum.reset();
        return Long.valueOf(l);
      }
    })).longValue();
  }
  
  public static HashCode hash(InputSupplier paramInputSupplier, HashFunction paramHashFunction)
    throws IOException
  {
    return asByteSource(paramInputSupplier).hash(paramHashFunction);
  }
  
  public static int read(InputStream paramInputStream, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    Preconditions.checkNotNull(paramInputStream);
    Preconditions.checkNotNull(paramArrayOfByte);
    if (paramInt2 < 0) {
      throw new IndexOutOfBoundsException("len is negative");
    }
    int i = 0;
    while (i < paramInt2)
    {
      int j = paramInputStream.read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j == -1) {
        break;
      }
      i += j;
    }
    return i;
  }
  
  public static InputSupplier slice(InputSupplier paramInputSupplier, long paramLong1, long paramLong2)
  {
    return asInputSupplier(asByteSource(paramInputSupplier).slice(paramLong1, paramLong2));
  }
  
  public static InputSupplier join(Iterable paramIterable)
  {
    Preconditions.checkNotNull(paramIterable);
    new InputSupplier()
    {
      public InputStream getInput()
        throws IOException
      {
        return new MultiInputStream(this.val$suppliers.iterator());
      }
    };
  }
  
  public static InputSupplier join(InputSupplier... paramVarArgs)
  {
    return join(Arrays.asList(paramVarArgs));
  }
  
  static InputSupplier asInputSupplier(ByteSource paramByteSource)
  {
    Preconditions.checkNotNull(paramByteSource);
    new InputSupplier()
    {
      public InputStream getInput()
        throws IOException
      {
        return this.val$source.openStream();
      }
    };
  }
  
  static OutputSupplier asOutputSupplier(ByteSink paramByteSink)
  {
    Preconditions.checkNotNull(paramByteSink);
    new OutputSupplier()
    {
      public OutputStream getOutput()
        throws IOException
      {
        return this.val$sink.openStream();
      }
    };
  }
  
  static ByteSource asByteSource(InputSupplier paramInputSupplier)
  {
    Preconditions.checkNotNull(paramInputSupplier);
    new ByteSource()
    {
      public InputStream openStream()
        throws IOException
      {
        return (InputStream)this.val$supplier.getInput();
      }
    };
  }
  
  static ByteSink asByteSink(OutputSupplier paramOutputSupplier)
  {
    Preconditions.checkNotNull(paramOutputSupplier);
    new ByteSink()
    {
      public OutputStream openStream()
        throws IOException
      {
        return (OutputStream)this.val$supplier.getOutput();
      }
    };
  }
  
  private static final class LimitedInputStream
    extends FilterInputStream
  {
    private long left;
    private long mark = -1L;
    
    LimitedInputStream(InputStream paramInputStream, long paramLong)
    {
      super();
      Preconditions.checkNotNull(paramInputStream);
      Preconditions.checkArgument(paramLong >= 0L, "limit must be non-negative");
      this.left = paramLong;
    }
    
    public int available()
      throws IOException
    {
      return (int)Math.min(this.in.available(), this.left);
    }
    
    public synchronized void mark(int paramInt)
    {
      this.in.mark(paramInt);
      this.mark = this.left;
    }
    
    public int read()
      throws IOException
    {
      if (this.left == 0L) {
        return -1;
      }
      int i = this.in.read();
      if (i != -1) {
        this.left -= 1L;
      }
      return i;
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (this.left == 0L) {
        return -1;
      }
      paramInt2 = (int)Math.min(paramInt2, this.left);
      int i = this.in.read(paramArrayOfByte, paramInt1, paramInt2);
      if (i != -1) {
        this.left -= i;
      }
      return i;
    }
    
    public synchronized void reset()
      throws IOException
    {
      if (!this.in.markSupported()) {
        throw new IOException("Mark not supported");
      }
      if (this.mark == -1L) {
        throw new IOException("Mark not set");
      }
      this.in.reset();
      this.left = this.mark;
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      paramLong = Math.min(paramLong, this.left);
      long l = this.in.skip(paramLong);
      this.left -= l;
      return l;
    }
  }
  
  private static class ByteArrayDataOutputStream
    implements ByteArrayDataOutput
  {
    final DataOutput output;
    final ByteArrayOutputStream byteArrayOutputSteam;
    
    ByteArrayDataOutputStream()
    {
      this(new ByteArrayOutputStream());
    }
    
    ByteArrayDataOutputStream(int paramInt)
    {
      this(new ByteArrayOutputStream(paramInt));
    }
    
    ByteArrayDataOutputStream(ByteArrayOutputStream paramByteArrayOutputStream)
    {
      this.byteArrayOutputSteam = paramByteArrayOutputStream;
      this.output = new DataOutputStream(paramByteArrayOutputStream);
    }
    
    public void write(int paramInt)
    {
      try
      {
        this.output.write(paramInt);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void write(byte[] paramArrayOfByte)
    {
      try
      {
        this.output.write(paramArrayOfByte);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      try
      {
        this.output.write(paramArrayOfByte, paramInt1, paramInt2);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeBoolean(boolean paramBoolean)
    {
      try
      {
        this.output.writeBoolean(paramBoolean);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeByte(int paramInt)
    {
      try
      {
        this.output.writeByte(paramInt);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeBytes(String paramString)
    {
      try
      {
        this.output.writeBytes(paramString);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeChar(int paramInt)
    {
      try
      {
        this.output.writeChar(paramInt);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeChars(String paramString)
    {
      try
      {
        this.output.writeChars(paramString);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeDouble(double paramDouble)
    {
      try
      {
        this.output.writeDouble(paramDouble);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeFloat(float paramFloat)
    {
      try
      {
        this.output.writeFloat(paramFloat);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeInt(int paramInt)
    {
      try
      {
        this.output.writeInt(paramInt);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeLong(long paramLong)
    {
      try
      {
        this.output.writeLong(paramLong);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeShort(int paramInt)
    {
      try
      {
        this.output.writeShort(paramInt);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public void writeUTF(String paramString)
    {
      try
      {
        this.output.writeUTF(paramString);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public byte[] toByteArray()
    {
      return this.byteArrayOutputSteam.toByteArray();
    }
  }
  
  private static class ByteArrayDataInputStream
    implements ByteArrayDataInput
  {
    final DataInput input;
    
    ByteArrayDataInputStream(byte[] paramArrayOfByte)
    {
      this.input = new DataInputStream(new ByteArrayInputStream(paramArrayOfByte));
    }
    
    ByteArrayDataInputStream(byte[] paramArrayOfByte, int paramInt)
    {
      this.input = new DataInputStream(new ByteArrayInputStream(paramArrayOfByte, paramInt, paramArrayOfByte.length - paramInt));
    }
    
    public void readFully(byte[] paramArrayOfByte)
    {
      try
      {
        this.input.readFully(paramArrayOfByte);
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      try
      {
        this.input.readFully(paramArrayOfByte, paramInt1, paramInt2);
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public int skipBytes(int paramInt)
    {
      try
      {
        return this.input.skipBytes(paramInt);
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public boolean readBoolean()
    {
      try
      {
        return this.input.readBoolean();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public byte readByte()
    {
      try
      {
        return this.input.readByte();
      }
      catch (EOFException localEOFException)
      {
        throw new IllegalStateException(localEOFException);
      }
      catch (IOException localIOException)
      {
        throw new AssertionError(localIOException);
      }
    }
    
    public int readUnsignedByte()
    {
      try
      {
        return this.input.readUnsignedByte();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public short readShort()
    {
      try
      {
        return this.input.readShort();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public int readUnsignedShort()
    {
      try
      {
        return this.input.readUnsignedShort();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public char readChar()
    {
      try
      {
        return this.input.readChar();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public int readInt()
    {
      try
      {
        return this.input.readInt();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public long readLong()
    {
      try
      {
        return this.input.readLong();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public float readFloat()
    {
      try
      {
        return this.input.readFloat();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public double readDouble()
    {
      try
      {
        return this.input.readDouble();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public String readLine()
    {
      try
      {
        return this.input.readLine();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
    
    public String readUTF()
    {
      try
      {
        return this.input.readUTF();
      }
      catch (IOException localIOException)
      {
        throw new IllegalStateException(localIOException);
      }
    }
  }
  
  private static final class ByteArrayByteSource
    extends ByteSource
  {
    private final byte[] bytes;
    
    private ByteArrayByteSource(byte[] paramArrayOfByte)
    {
      this.bytes = ((byte[])Preconditions.checkNotNull(paramArrayOfByte));
    }
    
    public InputStream openStream()
      throws IOException
    {
      return new ByteArrayInputStream(this.bytes);
    }
    
    public long size()
      throws IOException
    {
      return this.bytes.length;
    }
    
    public byte[] read()
      throws IOException
    {
      return (byte[])this.bytes.clone();
    }
    
    public long copyTo(OutputStream paramOutputStream)
      throws IOException
    {
      paramOutputStream.write(this.bytes);
      return this.bytes.length;
    }
    
    public HashCode hash(HashFunction paramHashFunction)
      throws IOException
    {
      return paramHashFunction.hashBytes(this.bytes);
    }
    
    public String toString()
    {
      return "ByteStreams.asByteSource(" + BaseEncoding.base16().encode(this.bytes) + ")";
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\ByteStreams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */