package de.schlichtherle.truezip.io;

import java.io.IOException;
import java.io.InputStream;

public abstract class DecoratingInputStream
  extends InputStream
{
  protected InputStream delegate;
  
  protected DecoratingInputStream(InputStream paramInputStream)
  {
    this.delegate = paramInputStream;
  }
  
  public int read()
    throws IOException
  {
    return this.delegate.read();
  }
  
  public final int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    return this.delegate.read(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    return this.delegate.skip(paramLong);
  }
  
  public int available()
    throws IOException
  {
    return this.delegate.available();
  }
  
  public void close()
    throws IOException
  {
    this.delegate.close();
  }
  
  public void mark(int paramInt)
  {
    this.delegate.mark(paramInt);
  }
  
  public void reset()
    throws IOException
  {
    this.delegate.reset();
  }
  
  public boolean markSupported()
  {
    return this.delegate.markSupported();
  }
  
  public String toString()
  {
    return String.format("%s[delegate=%s]", new Object[] { getClass().getName(), this.delegate });
  }
}


/* Location:              D:\Projects\AiCup\CodeRacing\local-runner\local-runner.jar!\de\schlichtherle\truezip\io\DecoratingInputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */