package com.google.common.hash;

import com.google.common.primitives.UnsignedBytes;
import java.io.Serializable;
import java.nio.ByteBuffer;

final class Murmur3_32HashFunction
  extends AbstractStreamingHashFunction
  implements Serializable
{
  private static final int C1 = -862048943;
  private static final int C2 = 461845907;
  private final int seed;
  private static final long serialVersionUID = 0L;
  
  Murmur3_32HashFunction(int paramInt)
  {
    this.seed = paramInt;
  }
  
  public int bits()
  {
    return 32;
  }
  
  public Hasher newHasher()
  {
    return new Murmur3_32Hasher(this.seed);
  }
  
  public String toString()
  {
    return "Hashing.murmur3_32(" + this.seed + ")";
  }
  
  public HashCode hashInt(int paramInt)
  {
    int i = mixK1(paramInt);
    int j = mixH1(this.seed, i);
    return fmix(j, 4);
  }
  
  public HashCode hashLong(long paramLong)
  {
    int i = (int)paramLong;
    int j = (int)(paramLong >>> 32);
    int k = mixK1(i);
    int m = mixH1(this.seed, k);
    k = mixK1(j);
    m = mixH1(m, k);
    return fmix(m, 8);
  }
  
  public HashCode hashString(CharSequence paramCharSequence)
  {
    int i = this.seed;
    for (int j = 1; j < paramCharSequence.length(); j += 2)
    {
      int k = paramCharSequence.charAt(j - 1) | paramCharSequence.charAt(j) << '\020';
      k = mixK1(k);
      i = mixH1(i, k);
    }
    if ((paramCharSequence.length() & 0x1) == 1)
    {
      j = paramCharSequence.charAt(paramCharSequence.length() - 1);
      j = mixK1(j);
      i ^= j;
    }
    return fmix(i, 2 * paramCharSequence.length());
  }
  
  private static int mixK1(int paramInt)
  {
    paramInt *= -862048943;
    paramInt = Integer.rotateLeft(paramInt, 15);
    paramInt *= 461845907;
    return paramInt;
  }
  
  private static int mixH1(int paramInt1, int paramInt2)
  {
    paramInt1 ^= paramInt2;
    paramInt1 = Integer.rotateLeft(paramInt1, 13);
    paramInt1 = paramInt1 * 5 + -430675100;
    return paramInt1;
  }
  
  private static HashCode fmix(int paramInt1, int paramInt2)
  {
    paramInt1 ^= paramInt2;
    paramInt1 ^= paramInt1 >>> 16;
    paramInt1 *= -2048144789;
    paramInt1 ^= paramInt1 >>> 13;
    paramInt1 *= -1028477387;
    paramInt1 ^= paramInt1 >>> 16;
    return HashCodes.fromInt(paramInt1);
  }
  
  private static final class Murmur3_32Hasher
    extends AbstractStreamingHashFunction.AbstractStreamingHasher
  {
    private static final int CHUNK_SIZE = 4;
    private int h1;
    private int length;
    
    Murmur3_32Hasher(int paramInt)
    {
      super();
      this.h1 = paramInt;
      this.length = 0;
    }
    
    protected void process(ByteBuffer paramByteBuffer)
    {
      int i = Murmur3_32HashFunction.mixK1(paramByteBuffer.getInt());
      this.h1 = Murmur3_32HashFunction.mixH1(this.h1, i);
      this.length += 4;
    }
    
    protected void processRemaining(ByteBuffer paramByteBuffer)
    {
      this.length += paramByteBuffer.remaining();
      int i = 0;
      for (int j = 0; paramByteBuffer.hasRemaining(); j += 8) {
        i ^= UnsignedBytes.toInt(paramByteBuffer.get()) << j;
      }
      this.h1 ^= Murmur3_32HashFunction.mixK1(i);
    }
    
    public HashCode makeHash()
    {
      return Murmur3_32HashFunction.fmix(this.h1, this.length);
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\Murmur3_32HashFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */