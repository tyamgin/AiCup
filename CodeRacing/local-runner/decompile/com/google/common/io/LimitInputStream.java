package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

@Beta
@Deprecated
public final class LimitInputStream
  extends FilterInputStream
{
  private long left;
  private long mark = -1L;
  
  public LimitInputStream(InputStream paramInputStream, long paramLong)
  {
    super(paramInputStream);
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


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\com\google\common\io\LimitInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */