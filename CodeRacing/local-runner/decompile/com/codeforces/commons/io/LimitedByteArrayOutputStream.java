package com.codeforces.commons.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class LimitedByteArrayOutputStream
  extends ByteArrayOutputStream
{
  private final int maxSize;
  private final boolean throwIfExceeded;
  
  public LimitedByteArrayOutputStream(int paramInt, boolean paramBoolean)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Argument 'maxSize' (" + paramInt + " B) is negative.");
    }
    this.maxSize = paramInt;
    this.throwIfExceeded = paramBoolean;
  }
  
  public synchronized void write(int paramInt)
  {
    if (size() < this.maxSize) {
      super.write(paramInt);
    } else if (this.throwIfExceeded) {
      throw new IllegalStateException("Buffer size (" + this.maxSize + " B) exceeded.");
    }
  }
  
  public synchronized void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (size() + paramInt2 <= this.maxSize)
    {
      super.write(paramArrayOfByte, paramInt1, paramInt2);
    }
    else
    {
      if (this.throwIfExceeded) {
        throw new IllegalStateException("Buffer size (" + this.maxSize + " B) exceeded.");
      }
      super.write(paramArrayOfByte, paramInt1, this.maxSize - size());
    }
  }
  
  public synchronized void write(byte[] paramArrayOfByte)
    throws IOException
  {
    if (size() + paramArrayOfByte.length <= this.maxSize)
    {
      super.write(paramArrayOfByte);
    }
    else
    {
      if (this.throwIfExceeded) {
        throw new IllegalStateException("Buffer size (" + this.maxSize + " B) exceeded.");
      }
      super.write(paramArrayOfByte, 0, this.maxSize - size());
    }
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\codeforces\commons\io\LimitedByteArrayOutputStream.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       0.7.1
 */