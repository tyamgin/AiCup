package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class AbstractByteHasher
  extends AbstractHasher
{
  private final ByteBuffer scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
  
  protected abstract void update(byte paramByte);
  
  protected void update(byte[] paramArrayOfByte)
  {
    update(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  protected void update(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++) {
      update(paramArrayOfByte[i]);
    }
  }
  
  public Hasher putByte(byte paramByte)
  {
    update(paramByte);
    return this;
  }
  
  public Hasher putBytes(byte[] paramArrayOfByte)
  {
    Preconditions.checkNotNull(paramArrayOfByte);
    update(paramArrayOfByte);
    return this;
  }
  
  public Hasher putBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    Preconditions.checkPositionIndexes(paramInt1, paramInt1 + paramInt2, paramArrayOfByte.length);
    update(paramArrayOfByte, paramInt1, paramInt2);
    return this;
  }
  
  private Hasher update(int paramInt)
  {
    try
    {
      update(this.scratch.array(), 0, paramInt);
    }
    finally
    {
      this.scratch.clear();
    }
    return this;
  }
  
  public Hasher putShort(short paramShort)
  {
    this.scratch.putShort(paramShort);
    return update(2);
  }
  
  public Hasher putInt(int paramInt)
  {
    this.scratch.putInt(paramInt);
    return update(4);
  }
  
  public Hasher putLong(long paramLong)
  {
    this.scratch.putLong(paramLong);
    return update(8);
  }
  
  public Hasher putChar(char paramChar)
  {
    this.scratch.putChar(paramChar);
    return update(2);
  }
  
  public Hasher putObject(Object paramObject, Funnel paramFunnel)
  {
    paramFunnel.funnel(paramObject, this);
    return this;
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\hash\AbstractByteHasher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */