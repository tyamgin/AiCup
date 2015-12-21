package com.google.common.hash;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

abstract class AbstractNonStreamingHashFunction
  implements HashFunction
{
  public Hasher newHasher()
  {
    return new BufferingHasher(32);
  }
  
  public Hasher newHasher(int paramInt)
  {
    Preconditions.checkArgument(paramInt >= 0);
    return new BufferingHasher(paramInt);
  }
  
  public HashCode hashObject(Object paramObject, Funnel paramFunnel)
  {
    return newHasher().putObject(paramObject, paramFunnel).hash();
  }
  
  public HashCode hashString(CharSequence paramCharSequence)
  {
    int i = paramCharSequence.length();
    Hasher localHasher = newHasher(i * 2);
    for (int j = 0; j < i; j++) {
      localHasher.putChar(paramCharSequence.charAt(j));
    }
    return localHasher.hash();
  }
  
  public HashCode hashString(CharSequence paramCharSequence, Charset paramCharset)
  {
    return hashBytes(paramCharSequence.toString().getBytes(paramCharset));
  }
  
  public HashCode hashInt(int paramInt)
  {
    return newHasher(4).putInt(paramInt).hash();
  }
  
  public HashCode hashLong(long paramLong)
  {
    return newHasher(8).putLong(paramLong).hash();
  }
  
  public HashCode hashBytes(byte[] paramArrayOfByte)
  {
    return hashBytes(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  private static final class ExposedByteArrayOutputStream
    extends ByteArrayOutputStream
  {
    ExposedByteArrayOutputStream(int paramInt)
    {
      super();
    }
    
    byte[] byteArray()
    {
      return this.buf;
    }
    
    int length()
    {
      return this.count;
    }
  }
  
  private final class BufferingHasher
    extends AbstractHasher
  {
    final AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream stream;
    static final int BOTTOM_BYTE = 255;
    
    BufferingHasher(int paramInt)
    {
      this.stream = new AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream(paramInt);
    }
    
    public Hasher putByte(byte paramByte)
    {
      this.stream.write(paramByte);
      return this;
    }
    
    public Hasher putBytes(byte[] paramArrayOfByte)
    {
      try
      {
        this.stream.write(paramArrayOfByte);
      }
      catch (IOException localIOException)
      {
        throw Throwables.propagate(localIOException);
      }
      return this;
    }
    
    public Hasher putBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      this.stream.write(paramArrayOfByte, paramInt1, paramInt2);
      return this;
    }
    
    public Hasher putShort(short paramShort)
    {
      this.stream.write(paramShort & 0xFF);
      this.stream.write(paramShort >>> 8 & 0xFF);
      return this;
    }
    
    public Hasher putInt(int paramInt)
    {
      this.stream.write(paramInt & 0xFF);
      this.stream.write(paramInt >>> 8 & 0xFF);
      this.stream.write(paramInt >>> 16 & 0xFF);
      this.stream.write(paramInt >>> 24 & 0xFF);
      return this;
    }
    
    public Hasher putLong(long paramLong)
    {
      for (int i = 0; i < 64; i += 8) {
        this.stream.write((byte)(int)(paramLong >>> i & 0xFF));
      }
      return this;
    }
    
    public Hasher putChar(char paramChar)
    {
      this.stream.write(paramChar & 0xFF);
      this.stream.write(paramChar >>> '\b' & 0xFF);
      return this;
    }
    
    public Hasher putObject(Object paramObject, Funnel paramFunnel)
    {
      paramFunnel.funnel(paramObject, this);
      return this;
    }
    
    public HashCode hash()
    {
      return AbstractNonStreamingHashFunction.this.hashBytes(this.stream.byteArray(), 0, this.stream.length());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\AbstractNonStreamingHashFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */