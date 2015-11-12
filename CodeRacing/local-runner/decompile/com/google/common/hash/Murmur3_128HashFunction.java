package com.google.common.hash;

import com.google.common.primitives.UnsignedBytes;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

final class Murmur3_128HashFunction
  extends AbstractStreamingHashFunction
  implements Serializable
{
  private final int seed;
  private static final long serialVersionUID = 0L;
  
  Murmur3_128HashFunction(int paramInt)
  {
    this.seed = paramInt;
  }
  
  public int bits()
  {
    return 128;
  }
  
  public Hasher newHasher()
  {
    return new Murmur3_128Hasher(this.seed);
  }
  
  public String toString()
  {
    return "Hashing.murmur3_128(" + this.seed + ")";
  }
  
  private static final class Murmur3_128Hasher
    extends AbstractStreamingHashFunction.AbstractStreamingHasher
  {
    private static final int CHUNK_SIZE = 16;
    private static final long C1 = -8663945395140668459L;
    private static final long C2 = 5545529020109919103L;
    private long h1;
    private long h2;
    private int length;
    
    Murmur3_128Hasher(int paramInt)
    {
      super();
      this.h1 = paramInt;
      this.h2 = paramInt;
      this.length = 0;
    }
    
    protected void process(ByteBuffer paramByteBuffer)
    {
      long l1 = paramByteBuffer.getLong();
      long l2 = paramByteBuffer.getLong();
      bmix64(l1, l2);
      this.length += 16;
    }
    
    private void bmix64(long paramLong1, long paramLong2)
    {
      this.h1 ^= mixK1(paramLong1);
      this.h1 = Long.rotateLeft(this.h1, 27);
      this.h1 += this.h2;
      this.h1 = (this.h1 * 5L + 1390208809L);
      this.h2 ^= mixK2(paramLong2);
      this.h2 = Long.rotateLeft(this.h2, 31);
      this.h2 += this.h1;
      this.h2 = (this.h2 * 5L + 944331445L);
    }
    
    protected void processRemaining(ByteBuffer paramByteBuffer)
    {
      long l1 = 0L;
      long l2 = 0L;
      this.length += paramByteBuffer.remaining();
      switch (paramByteBuffer.remaining())
      {
      case 15: 
        l2 ^= UnsignedBytes.toInt(paramByteBuffer.get(14)) << 48;
      case 14: 
        l2 ^= UnsignedBytes.toInt(paramByteBuffer.get(13)) << 40;
      case 13: 
        l2 ^= UnsignedBytes.toInt(paramByteBuffer.get(12)) << 32;
      case 12: 
        l2 ^= UnsignedBytes.toInt(paramByteBuffer.get(11)) << 24;
      case 11: 
        l2 ^= UnsignedBytes.toInt(paramByteBuffer.get(10)) << 16;
      case 10: 
        l2 ^= UnsignedBytes.toInt(paramByteBuffer.get(9)) << 8;
      case 9: 
        l2 ^= UnsignedBytes.toInt(paramByteBuffer.get(8));
      case 8: 
        l1 ^= paramByteBuffer.getLong();
        break;
      case 7: 
        l1 ^= UnsignedBytes.toInt(paramByteBuffer.get(6)) << 48;
      case 6: 
        l1 ^= UnsignedBytes.toInt(paramByteBuffer.get(5)) << 40;
      case 5: 
        l1 ^= UnsignedBytes.toInt(paramByteBuffer.get(4)) << 32;
      case 4: 
        l1 ^= UnsignedBytes.toInt(paramByteBuffer.get(3)) << 24;
      case 3: 
        l1 ^= UnsignedBytes.toInt(paramByteBuffer.get(2)) << 16;
      case 2: 
        l1 ^= UnsignedBytes.toInt(paramByteBuffer.get(1)) << 8;
      case 1: 
        l1 ^= UnsignedBytes.toInt(paramByteBuffer.get(0));
        break;
      default: 
        throw new AssertionError("Should never get here.");
      }
      this.h1 ^= mixK1(l1);
      this.h2 ^= mixK2(l2);
    }
    
    public HashCode makeHash()
    {
      this.h1 ^= this.length;
      this.h2 ^= this.length;
      this.h1 += this.h2;
      this.h2 += this.h1;
      this.h1 = fmix64(this.h1);
      this.h2 = fmix64(this.h2);
      this.h1 += this.h2;
      this.h2 += this.h1;
      return HashCodes.fromBytesNoCopy(ByteBuffer.wrap(new byte[16]).order(ByteOrder.LITTLE_ENDIAN).putLong(this.h1).putLong(this.h2).array());
    }
    
    private static long fmix64(long paramLong)
    {
      paramLong ^= paramLong >>> 33;
      paramLong *= -49064778989728563L;
      paramLong ^= paramLong >>> 33;
      paramLong *= -4265267296055464877L;
      paramLong ^= paramLong >>> 33;
      return paramLong;
    }
    
    private static long mixK1(long paramLong)
    {
      paramLong *= -8663945395140668459L;
      paramLong = Long.rotateLeft(paramLong, 31);
      paramLong *= 5545529020109919103L;
      return paramLong;
    }
    
    private static long mixK2(long paramLong)
    {
      paramLong *= 5545529020109919103L;
      paramLong = Long.rotateLeft(paramLong, 33);
      paramLong *= -8663945395140668459L;
      return paramLong;
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\Murmur3_128HashFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */